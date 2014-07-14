package edu.purdue.dmidi.Biba.domain;

public class BibaSubject extends BaseIntegrityElement {

	private Boolean isTrusted;
	

	public BibaSubject(String name, IntegrityLevel initialIntegrityLevel) {
		this(name, initialIntegrityLevel, false);
	}
	public BibaSubject(String name, IntegrityLevel initialIntegrityLevel, Boolean isTrusted) {
		super(name, initialIntegrityLevel);
		this.isTrusted = isTrusted;
	}

	public Boolean getIsTrusted() {
		return isTrusted;
	}
	public void setIsTrusted(Boolean isTrusted) {
		this.isTrusted = isTrusted;
	}
	
	@Override
	public String toString() {
		return "Subject '" + this.getName() + "' with integrity level " + this.getInitialIntegrityLevel().toString() + (this.getIsTrusted()?" (trusted)":"");		
	}
	
}
