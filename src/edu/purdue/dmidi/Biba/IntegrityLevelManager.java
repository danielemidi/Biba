package edu.purdue.dmidi.Biba;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.purdue.dmidi.Biba.domain.IntegrityLevel;

public class IntegrityLevelManager {	
	
	private Map<Integer, IntegrityLevel> levels = new HashMap<Integer, IntegrityLevel>();
	
	public IntegrityLevelManager() {
	}
	
	public void init(Connection con) {
		// loads the integrity levels from the DB
		try {			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM integritylevels");
		    
		    while (rs.next()) {
		    	IntegrityLevel level = new IntegrityLevel(rs.getString("label"), rs.getInt("il"));
		    	this.levels.put(level.getValue(), level);
	            System.out.println("Loaded integrity level " + level.toString());
		    }
			
		} catch (SQLException e) {
	        System.out.println("Cannot load integrity levels: " + e.getMessage());
		}		
	}
		

	public IntegrityLevel getLevel(int il) {
		return this.levels.get(il);
	}
	
	public Collection<IntegrityLevel> getLevels() {
		return this.levels.values();
	}
	
	
}
