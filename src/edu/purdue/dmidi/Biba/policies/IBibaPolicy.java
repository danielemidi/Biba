package edu.purdue.dmidi.Biba.policies;

import edu.purdue.dmidi.Biba.domain.BibaQuery;
import edu.purdue.dmidi.Biba.domain.IntegrityLevel;

public interface IBibaPolicy {

	//Boolean validateAccess(Boolean isRead, IntegrityLevel subjectLevel, IntegrityLevel objectLevel);
	Boolean validateAccess(IntegrityLevel subjectLevel, BibaQuery query);
	
}
