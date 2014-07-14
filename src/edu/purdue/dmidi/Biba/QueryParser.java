package edu.purdue.dmidi.Biba;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.purdue.dmidi.Biba.domain.BibaQuery;

public class QueryParser {

	private Pattern tokenPattern, parenthesesPattern, 
					selectPattern, insertPattern, updatePattern, deletePattern;
	
	
	public QueryParser() {
		this.tokenPattern = Pattern.compile("([\\w\\.]+|,|\\(|\\)|\\.|\\>|\\<|=|\\<=|\\>=|\\<\\>|\\+|\\-|\\*|\\\\|\\/)");
		
		this.parenthesesPattern = Pattern.compile("(\\(|\\))");
		
		//	SELECT fields
		//	FROM table alias;
		this.selectPattern = Pattern.compile("^\\s*select\\s+(.*)\\s+from\\s+(.*?)(\\s+where.*|\\s+group\\s+by.*|\\s+order\\s+by.*)?\\s*$");

		//	INSERT INTO table_name [(column1,column2,column3,...)]
		//	VALUES (value1,value2,value3,...);
		// -or-
		//	INSERT INTO table_name [(column1,column2,column3,...)]
		//	SELECT...;
		this.insertPattern = Pattern.compile("^\\s*insert\\s+into\\s+(\\w+).*$");
		
		//	UPDATE table_name
		//	SET column1=value1,column2=value2,...
		//	WHERE some_column=some_value;
		this.updatePattern = Pattern.compile("^\\s*update\\s+(\\w+).*$");
			
		//	DELETE FROM table_name
		//	WHERE some_column=some_value;
		this.deletePattern = Pattern.compile("^\\s*delete\\s+from\\s+(\\w+).*$");
	}
	
	public BibaQuery parseQuery(String query) throws BibaException {
		BibaQuery result = new BibaQuery();
		result.setRawQuery(query);
		
		// remove strings from query
		String q = query.replaceAll("\".*\"", "").replaceAll("\'.*\'", "").toLowerCase().trim();
		
//	    System.out.println("PARSING: " + q);

		// detect and process subqueries by '(' and ')'
		Matcher matcher = this.parenthesesPattern.matcher(q);
		while(matcher.find()) {
			if(matcher.group(1).equals("(")) {
				int subexprstart = matcher.end(1);
				int outstandingparentheses = 1;
				while(matcher.find()) {
					if(matcher.group(1).equals("(")) outstandingparentheses++;
					else if(matcher.group(1).equals(")")) outstandingparentheses--;
					if(outstandingparentheses == 0) {
						BibaQuery subresult = parseQuery(q.substring(subexprstart, matcher.start(1)));
						result.mergeWith(subresult);
						q = q.substring(0, Math.max(subexprstart-1, 0)) + "(subexpr)" + q.substring(Math.min(matcher.start(1)+1, q.length()));
//					    System.out.println("PARSING REMAINING: " + q);
						break;
					}
				}
				if (outstandingparentheses != 0) throw new BibaException("Unbalanced parentheses.");
				matcher = this.parenthesesPattern.matcher(q);
				matcher.region(Math.max(subexprstart-1, 0) + "(subexpr)".length(), q.length());
//				System.out.println("REGION: " + q.substring(Math.max(subexprstart-1, 0) + "(subexpr)".length(), q.length()));
			}
		}

		// parses the query on the main nesting level to extract affected objects
		
		matcher = this.selectPattern.matcher(q);
		if (matcher.find()) {
//			for (int i = 1; i <= matcher.groupCount(); i++)
//				System.out.println("      " + i + ": " + matcher.group(i));
//			System.out.println("=> SELECT, reading from ");
			matcher = this.tokenPattern.matcher(matcher.group(2));
			boolean lastWasIdentifier = false;
			boolean isIdentifier = false;
			String token;
			while(matcher.find()) {
				token = matcher.group(1);
				isIdentifier = token.matches("[\\w.]+");
				if(!isIdentifier || token.equals("join") || token.equals("inner") || token.equals("outer") || token.equals("left") || token.equals("right")) {
					// found a comma or keyword
					lastWasIdentifier = false;
					continue;
				}
				if(token.equals("on")) {
					matcher.find(); matcher.find(); matcher.find(); // skip "A.a=B.b"
					lastWasIdentifier = false;
					continue;
				}
				if(lastWasIdentifier){
					// found an alias
					lastWasIdentifier = false;
					continue;
				}
				result.selectFrom.add(token);
//			    System.out.println("    - " + token);
				lastWasIdentifier = isIdentifier;
			}
			return result;
		}

		matcher = this.insertPattern.matcher(q);
		if (matcher.find()) {
//			for (int i = 1; i <= matcher.groupCount(); i++)
//				System.out.println("      " + i + ": " + matcher.group(i));
//			System.out.println("=> INSERT, writing to ");
//		    System.out.println("    - " + matcher.group(1));
			result.insertInto.add(matcher.group(1));
			return result;
		}

		matcher = this.updatePattern.matcher(q);
		if (matcher.find()) {
//			System.out.println("=> UPDATE, writing to ");
//			for (int i = 1; i <= matcher.groupCount(); i++)
//				System.out.println("      " + i + ": " + matcher.group(i));
			result.updateInto.add(matcher.group(1));
			return result;
		}

		matcher = this.deletePattern.matcher(q);
		if (matcher.find()) {
//			System.out.println("=> DELETE, writing to ");
//			for (int i = 1; i <= matcher.groupCount(); i++)
//				System.out.println("      " + i + ": " + matcher.group(i));
			result.deleteFrom.add(matcher.group(1));
			return result;
		}

		return result;
	}
		
		
		
		
	
	/* EXAMPLES */
//	SELECT * FROM TableName;
//	SELECT FirstName, LastName, Address, City, State FROM EmployeeAddressTable;
//	SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE WHERE SALARY >= 50000;
//	SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE WHERE POSITION = 'Manager';
//	SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE
//	WHERE SALARY > 40000 AND POSITION = 'Staff';
//	SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE
//	WHERE SALARY < 40000 OR BENEFITS < 10000;
//	SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE
//	WHERE POSITION = 'Manager' AND SALARY > 60000 OR BENEFITS > 12000;
//	SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE
//	WHERE POSITION = 'Manager' AND (SALARY > 50000 OR BENEFIT > 10000);
//	SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE
//	WHERE POSITION IN ('Manager', 'Staff');
//	SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE
//	WHERE SALARY BETWEEN 30000 AND 50000;
//	SELECT EMPLOYEEIDNO FROM EMPLOYEESTATISTICSTABLE
//	WHERE SALARY NOT BETWEEN 30000 AND 50000;
//	SELECT EMPLOYEEIDNO FROM EMPLOYEEADDRESSTABLE WHERE LASTNAME LIKE 'L%';
//	SELECT OWNERLASTNAME, OWNERFIRSTNAME FROM ANTIQUEOWNERS, ANTIQUES
//	WHERE BUYERID = OWNERID AND ITEM = 'Chair';
//	SELECT ANTIQUEOWNERS.OWNERLASTNAME, ANTIQUEOWNERS.OWNERFIRSTNAME
//	FROM ANTIQUEOWNERS, ANTIQUES
//	WHERE ANTIQUES.BUYERID = ANTIQUEOWNERS.OWNERID AND ANTIQUES.ITEM = 'Chair';
//	SELECT DISTINCT SELLERID, OWNERLASTNAME, OWNERFIRSTNAME
//	FROM ANTIQUES, ANTIQUEOWNERS
//	WHERE SELLERID = OWNERID
//	ORDER BY OWNERLASTNAME, OWNERFIRSTNAME, OWNERID;
//	SELECT OWN.OWNERLASTNAME Last Name, ORD.ITEMDESIRED Item Ordered
//	FROM ORDERS ORD, ANTIQUEOWNERS OWN
//	WHERE ORD.OWNERID = OWN.OWNERID
//	AND ORD.ITEMDESIRED IN (SELECT ITEM FROM ANTIQUES);
//	SELECT SUM(SALARY), AVG(SALARY) FROM EMPLOYEESTATISTICSTABLE;
//	SELECT MIN(BENEFITS) FROM EMPLOYEESTATISTICSTABLE WHERE POSITION = 'Manager';
//	SELECT COUNT(*) FROM EMPLOYEESTATISTICSTABLE WHERE POSITION = 'Staff';
//	SELECT SELLERID FROM ANTIQUES, ANTVIEW WHERE ITEMDESIRED = ITEM;
//	INSERT INTO ANTIQUES VALUES (21, 01, 'Ottoman', 200.00);
//	INSERT INTO ANTIQUES (BUYERID, SELLERID, ITEM) VALUES (01, 21, 'Ottoman');
//	DELETE FROM ANTIQUES WHERE ITEM = 'Ottoman';
//	DELETE FROM ANTIQUES WHERE ITEM = 'Ottoman' AND BUYERID = 01 AND SELLERID = 21;
//	UPDATE ANTIQUES SET PRICE = 500.00 WHERE ITEM = 'Chair';
//	SELECT BUYERID, MAX(PRICE) FROM ANTIQUES GROUP BY BUYERID;
//	SELECT BUYERID, MAX(PRICE) FROM ANTIQUES GROUP BY BUYERID HAVING PRICE > 1000;
//	SELECT OWNERID FROM ANTIQUES
//	WHERE PRICE > (SELECT AVG(PRICE) + 100 FROM ANTIQUES);
//	SELECT OWNERLASTNAME FROM ANTIQUEOWNERS
//	WHERE OWNERID = (SELECT DISTINCT BUYERID FROM ANTIQUES);
//	UPDATE ANTIQUEOWNERS SET OWNERFIRSTNAME = 'John'
//	WHERE OWNERID = (SELECT BUYERID FROM ANTIQUES WHERE ITEM = 'Bookcase');
//	SELECT OWNERFIRSTNAME, OWNERLASTNAME FROM ANTIQUEOWNERS
//	WHERE EXISTS (SELECT * FROM ANTIQUES WHERE ITEM = 'Chair');
//	SELECT BUYERID, ITEM FROM ANTIQUES
//	WHERE PRICE >= ALL (SELECT PRICE FROM ANTIQUES);
//	SELECT BUYERID FROM ANTIQUEOWNERS UNION SELECT OWNERID FROM ORDERS;
//	SELECT OWNERID, 'is in both Orders & Antiques' FROM ORDERS, ANTIQUES
//	WHERE OWNERID = BUYERID
//	UNION
//	SELECT BUYERID, 'is in Antiques only' FROM ANTIQUES
//	WHERE BUYERID NOT IN (SELECT OWNERID FROM ORDERS);
//	EXIT;
	
}
