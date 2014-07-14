package edu.purdue.dmidi.Biba.domain;

public class BibaObject extends BaseIntegrityElement {

	public BibaObject(String name, IntegrityLevel initialIntegrityLevel) {
		super(name, initialIntegrityLevel);
	}
	
	@Override
	public String toString() {
		return "Object '" + this.getName() + "' with integrity level " + this.getInitialIntegrityLevel().toString();		
	}

}
