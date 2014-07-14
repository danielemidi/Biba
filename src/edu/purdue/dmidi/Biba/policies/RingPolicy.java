package edu.purdue.dmidi.Biba.policies;

import edu.purdue.dmidi.Biba.domain.BibaQuery;
import edu.purdue.dmidi.Biba.domain.IntegrityLevel;

public class RingPolicy implements IBibaPolicy {

	public Boolean validateAccess(IntegrityLevel subjectLevel, BibaQuery query) {
		if(query.getWriteIL() == null) return true; // can always only read

		return subjectLevel.getValue() >= query.getWriteIL().getValue(); // write-down
	}

}
