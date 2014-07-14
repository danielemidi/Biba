package edu.purdue.dmidi.Biba;

import java.util.HashSet;
import java.util.Set;

import edu.purdue.dmidi.Biba.domain.BibaObject;
import edu.purdue.dmidi.Biba.domain.BibaQuery;
import edu.purdue.dmidi.Biba.domain.BibaSubject;
import edu.purdue.dmidi.Biba.policies.IBibaPolicy;
import edu.purdue.dmidi.Biba.policies.RingPolicy;
import edu.purdue.dmidi.Biba.policies.StrictPolicy;

public class SecurityManager {

	private QueryParser queryParser;
	
	private SubjectManager subjectManager;
	private ObjectManager objectManager;
	
	private IBibaPolicy currentPolicy;
	private IBibaPolicy ringPolicy;

	public SecurityManager(SubjectManager subjectManager, ObjectManager objectManager) {
		this.queryParser = new QueryParser();
		this.subjectManager = subjectManager;
		this.objectManager = objectManager;
		this.currentPolicy = new StrictPolicy();
		this.ringPolicy = new RingPolicy();
	}
	

	public IBibaPolicy getCurrentPolicy() {
		return currentPolicy;
	}
	public void setCurrentPolicy(IBibaPolicy policy) {
		this.currentPolicy = policy;
	}
	
	public Boolean validateQuery(String query) throws BibaException {
		
		BibaSubject currSubj = this.subjectManager.getCurrentSubject();
		
		if(currSubj == null)
			throw new BibaException("No current subject.");
		
		// parse the query and extract relevant integrity objects
		BibaQuery parsedQuery = this.queryParser.parseQuery(query);
		
		// determine if it's a READ or a WRITE query
		//Boolean isReadQuery = q.startsWith("select");
		
		// determine security level of the query
//		IntegrityLevel queryLevel = null; //isReadQuery ? IntegrityLevel.VeryHigh : IntegrityLevel.VeryLow;
//		for(BibaObject o : this.objectManager.getObjects()) {
//			if(q.contains(o.getName())) {
//				if(queryLevel == null) queryLevel = o.getIntegrityLevel();
//				else {
//					if(isReadQuery) {
//						if(o.getIntegrityLevel().getValue() < queryLevel.getValue()) queryLevel = o.getIntegrityLevel();
//					} else {
//						if(o.getIntegrityLevel().getValue() > queryLevel.getValue()) queryLevel = o.getIntegrityLevel();
//					}
//				}
//			}
//		}
		
		// determine the read integrity level of the query
		for(String table : parsedQuery.selectFrom) {
			BibaObject obj = this.objectManager.getObject(table);
			if(obj == null) continue;
			if(parsedQuery.getReadIL() == null)
				parsedQuery.setReadIL(obj.getIntegrityLevel());
			else {
				if(obj.getIntegrityLevel().getValue() < parsedQuery.getReadIL().getValue())
					parsedQuery.setReadIL(obj.getIntegrityLevel());
			}
		}

		// determine the write integrity level of the query
		Set<String> writeTables = new HashSet<String>();
		writeTables.addAll(parsedQuery.insertInto);
		writeTables.addAll(parsedQuery.deleteFrom);
		writeTables.addAll(parsedQuery.updateInto);
		for(String table : writeTables) {
			BibaObject obj = this.objectManager.getObject(table);
			if(obj == null) continue;
			if(parsedQuery.getWriteIL() == null)
				parsedQuery.setWriteIL(obj.getIntegrityLevel());
			else {
				if(obj.getIntegrityLevel().getValue() > parsedQuery.getWriteIL().getValue())
					parsedQuery.setWriteIL(obj.getIntegrityLevel());
			}
		}
		
		System.out.println(parsedQuery);
		System.out.println("--> Query read IL: " + (parsedQuery.getReadIL()!=null ? parsedQuery.getReadIL().toString() : "<none>") +
							 "; Query write IL: " + (parsedQuery.getWriteIL()!=null ? parsedQuery.getWriteIL().toString() : "<none>") +
							 "; Subject IL: " + currSubj.getIntegrityLevel().toString());
		
//		if(currSubj.getIsTrusted())
//			return this.ringPolicy.validateAccess(isReadQuery, currSubj.getIntegrityLevel(), queryLevel);
//		else
//			return this.currentPolicy.validateAccess(isReadQuery, currSubj.getIntegrityLevel(), queryLevel);
		if(currSubj.getIsTrusted())
			return this.ringPolicy.validateAccess(currSubj.getIntegrityLevel(), parsedQuery);
		else
			return this.currentPolicy.validateAccess(currSubj.getIntegrityLevel(), parsedQuery);
	}
	
}
