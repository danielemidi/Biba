package edu.purdue.dmidi.Biba.policies;

import edu.purdue.dmidi.Biba.domain.BibaQuery;
import edu.purdue.dmidi.Biba.domain.IntegrityLevel;

public class StrictPolicy implements IBibaPolicy {

	public Boolean validateAccess(IntegrityLevel subjectLevel, BibaQuery query) {
		return (query.getReadIL()!=null && subjectLevel.getValue() <= query.getReadIL().getValue()) || // read-up
			   (query.getWriteIL()!=null && subjectLevel.getValue() >= query.getWriteIL().getValue());  // write-down
	}

}
