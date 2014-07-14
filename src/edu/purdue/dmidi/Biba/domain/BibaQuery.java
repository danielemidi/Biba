package edu.purdue.dmidi.Biba.domain;

import java.util.*;

public class BibaQuery {

	private String rawQuery;

	public Set<String> selectFrom = new HashSet<String>();
	public Set<String> insertInto = new HashSet<String>();
	public Set<String> deleteFrom = new HashSet<String>();
	public Set<String> updateInto = new HashSet<String>();
	
	private IntegrityLevel readIL;
	private IntegrityLevel writeIL;
	
	public BibaQuery(){
		this.rawQuery = "";
	}
	
	public void mergeWith(BibaQuery other) {
		this.selectFrom.addAll(other.selectFrom);
		this.insertInto.addAll(other.insertInto);
		this.deleteFrom.addAll(other.deleteFrom);
		this.updateInto.addAll(other.updateInto);
	}

	
	public String getRawQuery() {
		return rawQuery;
	}
	public void setRawQuery(String rawQuery) {
		this.rawQuery = rawQuery;
	}
	
	
	public IntegrityLevel getReadIL() {
		return readIL;
	}
	public void setReadIL(IntegrityLevel readIL) {
		this.readIL = readIL;
	}

	public IntegrityLevel getWriteIL() {
		return writeIL;
	}
	public void setWriteIL(IntegrityLevel writeIL) {
		this.writeIL = writeIL;
	}
	
	@Override
	public String toString() {
		String s;
		
		s = this.rawQuery + "\r\n";
		
		if (this.selectFrom.size() > 0) {
			s += "SELECT:\r\n";
			for(String t : this.selectFrom)
				s += " - "+t+"\r\n";
		}

		if (this.insertInto.size() > 0) {
			s += "INSERT:\r\n";
			for(String t : this.insertInto)
				s += " - "+t+"\r\n";
		}

		if (this.deleteFrom.size() > 0) {
			s += "DELETE:\r\n";
			for(String t : this.deleteFrom)
				s += " - "+t+"\r\n";
		}

		if (this.updateInto.size() > 0) {
			s += "UPDATE:\r\n";
			for(String t : this.updateInto)
				s += " - "+t+"\r\n";
		}

		if(this.readIL != null) s += "Reading Integrity level: " + this.readIL.toString() + "\r\n";

		if(this.writeIL != null) s += "Writing Integrity level: " + this.writeIL.toString() + "\r\n";
		
		return s;
	}
	
}
