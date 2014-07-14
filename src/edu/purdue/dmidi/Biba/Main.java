package edu.purdue.dmidi.Biba;

import asg.cliche.CLIException;
import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;

import edu.purdue.dmidi.Biba.policies.SubjectLowWatermarkPolicy;
import edu.purdue.dmidi.Biba.policies.StrictPolicy;

public class Main {
	
	private Connection con;
	private SubjectManager subjectManager;
	private ObjectManager objectManager;
	private IntegrityLevelManager integrityLevelManager;
	private SecurityManager securityManager;
	
	private static Shell shell;

//	private QueryParser queryParser = new QueryParser();
	
	public static void main(String[] args) {
		try {
			Main m = new Main();
			m.init();
			
//			try {
//				BibaQuery queryresult = m.queryParser.parseQuery("SELECT cno FROM customers WHERE cno IN (SELECT O.ono FROM orders O LEFT OUTER JOIN parts ON O.pno=pno JOIN zipcodes ZZZ ON ZZZ.zip=C.zip WHERE O.ono = ANY (SELECT OO.ono FROM orders OO, zip)) AND cname NOT LIKE \"pippo\" GROUP BY cname");
//				//BibaQuery queryresult = m.queryParser.parseQuery("Insert into tableB(Name, CatId, Description) (select name,(select top(1) categoryid from dbo.category where dbo.category.categoryname=tableA.CategoryName) ,description from tableA)");
//				//BibaQuery queryresult = m.queryParser.parseQuery("UPDATE ANTIQUEOWNERS SET OWNERFIRSTNAME = 'John' WHERE OWNERID = (SELECT BUYERID FROM ANTIQUES WHERE ITEM = 'Bookcase');");
//				//BibaQuery queryresult = m.queryParser.parseQuery("DELETE FROM ANTIQUES WHERE ITEM = 'Ottoman' AND BUYERID = 01 AND SELLERID = 21;");
//				//BibaQuery queryresult = m.queryParser.parseQuery("DELETE FROM table t1 WHERE t1.V1 > t1.V2 and EXISTS ( SELECT * FROM table t2 WHERE t2.V1 = t1.V2           and t2.V2 = t1.V1 )");
//				System.out.println("***************");
//				System.out.println(queryresult);
//				System.out.println("***************");
//			} catch (BibaException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			shell = ShellFactory.createConsoleShell("", "Biba policy enforcement", m);
			
			// first of all, process commands coming from the command line args
			for(int i = 0; i<args.length; i++) {
				try {
					System.out.println("> "+args[i]);
					shell.processLine(args[i]);
					System.out.println();
				} catch (CLIException e) {
					System.err.println("Error during Biba command execution: " + e.getMessage());
				}
			}
			
			// start the interactive session
			shell.commandLoop();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void init() {
		try {
			
			System.out.println("STARTING SYSTEM INITIALIZATION...\r\n");
			
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			this.initDbConnection("dmidi", "b6sKCExY");
			
			this.integrityLevelManager = new IntegrityLevelManager();
			this.subjectManager = new SubjectManager();
			this.objectManager = new ObjectManager();
			this.securityManager = new SecurityManager(this.subjectManager, this.objectManager);
			
			this.integrityLevelManager.init(con);
			this.subjectManager.init(this.con, this.integrityLevelManager);
			this.objectManager.init(this.con, this.integrityLevelManager);

			System.out.println("\r\nSYSTEM INITIALIZATION COMPLETE.\r\n");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void initDbConnection(String username, String password) throws SQLException {
		this.con = DriverManager.getConnection("jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep", username, password);
	}

	
	
//	@Command(description="Quit the program")
//    public void quit() {
//    	this.query("SELECT zip, city FROM zipcodes");
//    }
	
	@Command(description="Impersonate a subject")
    public String login(
    		@Param(name="username", description="Subject to impersonate")
    		String username) {
		
		if(this.subjectManager.login(username))
			return "Current subject: " + username + ".";
		else
			return "Invalid subject name.";
    }

	
	@Command(description="Set the Biba policy to enforce")
    public String setPolicy(
    		@Param(name="policy", description="The Biba policy to enforce")
    		String policy) {
		
		if(policy.toLowerCase().equals("strict")) {
			this.securityManager.setCurrentPolicy(new StrictPolicy());
		}else if(policy.toLowerCase().equals("subjectwatermark")) {
			this.securityManager.setCurrentPolicy(new SubjectLowWatermarkPolicy(this.subjectManager));
		}else{
			return "Invalid policy.";
		}
		
		return "New policy enforced.";
    }

	
	@Command(description="Execute a sample query on the database")
    public void query() {
    	this.query("SELECT zip, city FROM zipcodes");
    }
		
	
	@Command(description="Execute a query on the database")
    public void query(
    		@Param(name="query", description="Query to be executed")
    		String query) {
    	
		Statement stmt;
		try {
			
			// split multiple queries by ';'
			for(String qq : query.split(";")) {
				
				if(!this.securityManager.validateQuery(qq))
			        throw new BibaException("ACCESS DENIED.");
				
				stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(qq);
		
				ResultSetMetaData rsmd = rs.getMetaData();
			    int columnsNumber = rsmd.getColumnCount();
	
		        for (int i = 1; i <= columnsNumber; i++) {
		            System.out.print(String.format("%-" + (rsmd.getColumnDisplaySize(i)+2) + "s", rsmd.getColumnName(i)));
		        }
		        System.out.println("");
			    
			    while (rs.next()) {
			        for (int i = 1; i <= columnsNumber; i++) {
			            System.out.print(String.format("%-" + (rsmd.getColumnDisplaySize(i)+2) + "s", rs.getString(i)));
			        }
			        System.out.println("");
			    }
			    
			}
			
		} catch (SQLException e) {
	        System.err.println(e.getMessage());
			//e.printStackTrace();
		} catch (BibaException e) {
	        System.out.println(e.getMessage());
		} finally {
	        System.out.println("******************\r\n");
		}
		
    }

}