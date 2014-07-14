package edu.purdue.dmidi.Biba;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.purdue.dmidi.Biba.domain.BibaObject;

public class ObjectManager {
	private Map<String, BibaObject> objects = new HashMap<String, BibaObject>();
	
	public ObjectManager() {
	}
	
	public void init(Connection con, IntegrityLevelManager ilm) {
//		this.objects.put("zipcodes", new BibaObject("zipcodes", IntegrityLevel.Low));
//		this.objects.put("employees", new BibaObject("employees", IntegrityLevel.High));
//		this.objects.put("parts", new BibaObject("parts", IntegrityLevel.VeryHigh));
//		this.objects.put("customers", new BibaObject("customers", IntegrityLevel.High));
//		this.objects.put("orders", new BibaObject("orders", IntegrityLevel.Medium));
//		this.objects.put("odetails", new BibaObject("odetails", IntegrityLevel.High));
		
		// load objects from DB
		try {			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM objects");
		    
		    while (rs.next()) {
		    	BibaObject obj = new BibaObject(rs.getString("oname"), ilm.getLevel(rs.getInt("il")));
		    	this.objects.put(obj.getName(), obj);
	            System.out.println("Loaded " + obj.toString());
		    }
			
		} catch (SQLException e) {
	        System.out.println("Cannot load objects: " + e.getMessage());
		}
	}
	
	public BibaObject getObject(String name) {
		return this.objects.get(name);
	}

	public Collection<BibaObject> getObjects() {
		return this.objects.values();
	}
	
}
