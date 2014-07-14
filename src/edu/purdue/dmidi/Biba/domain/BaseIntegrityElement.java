package edu.purdue.dmidi.Biba.domain;

public abstract class BaseIntegrityElement {

	private String name;
	private IntegrityLevel initialIntegrityLevel;
	private IntegrityLevel integrityLevel;
	
	public BaseIntegrityElement(String name, IntegrityLevel initialIntegrityLevel){
		this.name = name;
		this.initialIntegrityLevel = initialIntegrityLevel;
		this.integrityLevel = initialIntegrityLevel;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public IntegrityLevel getInitialIntegrityLevel() {
		return initialIntegrityLevel;
	}
	public void setInitialIntegrityLevel(IntegrityLevel initialIntegrityLevel) {
		this.initialIntegrityLevel = initialIntegrityLevel;
	}
	
	public IntegrityLevel getIntegrityLevel() {
		return integrityLevel;
	}
	public void setIntegrityLevel(IntegrityLevel integrityLevel) {
		this.integrityLevel = integrityLevel;
	}
	
}
