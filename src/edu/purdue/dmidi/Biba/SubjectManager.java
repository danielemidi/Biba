package edu.purdue.dmidi.Biba;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.purdue.dmidi.Biba.domain.BibaSubject;

public class SubjectManager {

	private BibaSubject currentSubject = null;
	private Map<String, BibaSubject> subjects = new HashMap<String, BibaSubject>();
	
	public SubjectManager() {
	}
	
	public void init(Connection con, IntegrityLevelManager ilm) {
//		this.subjects.put("admin", new BibaSubject("admin", IntegrityLevel.VeryHigh, true));
//		this.subjects.put("inventory", new BibaSubject("inventory", IntegrityLevel.VeryHigh));
//		this.subjects.put("customerservice", new BibaSubject("customerservice", IntegrityLevel.Medium));
//		this.subjects.put("hr", new BibaSubject("hr", IntegrityLevel.High, true));
//		this.subjects.put("audit", new BibaSubject("audit", IntegrityLevel.VeryLow));
//		this.subjects.put("guest", new BibaSubject("guest", IntegrityLevel.Low));
		
		// load subjects from DB
		try {			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM subjects");
		    
		    while (rs.next()) {
		    	BibaSubject subj = new BibaSubject(rs.getString("username"), ilm.getLevel(rs.getInt("il")), rs.getString("istrusted").toLowerCase().equals("t") ? true : false);
		    	this.subjects.put(subj.getName(), subj);
	            System.out.println("Loaded " + subj.toString());
		    }
			
		} catch (SQLException e) {
	        System.out.println("Cannot load subjects: " + e.getMessage());
		}
	}
	
	public Boolean login(String username) {
		if(!this.subjects.containsKey(username)) return false;
		
		this.currentSubject = this.subjects.get(username);
		return true;
	}
	
	public BibaSubject getCurrentSubject() {
		return this.currentSubject;
	}
	

	public Collection<BibaSubject> getSubjects() {
		return this.subjects.values();
	}
	
	
}
