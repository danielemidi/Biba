package edu.purdue.dmidi.Biba.policies;

import edu.purdue.dmidi.Biba.SubjectManager;
import edu.purdue.dmidi.Biba.domain.BibaQuery;
import edu.purdue.dmidi.Biba.domain.IntegrityLevel;

public class SubjectLowWatermarkPolicy implements IBibaPolicy {

	private SubjectManager subjectManager;
	
	public SubjectLowWatermarkPolicy(SubjectManager subjectManager) {
		this.subjectManager = subjectManager;
	}
	
	public Boolean validateAccess(IntegrityLevel subjectLevel, BibaQuery query) {
		if(query.getReadIL() != null) {
			// can always read but gets watermarked
			if(query.getReadIL().getValue() < subjectLevel.getValue()) {
				this.subjectManager.getCurrentSubject().setIntegrityLevel(query.getReadIL());
				System.out.println("[Subject's IL lowered to " + query.getReadIL().toString() + "]");
			}
		}
		return query.getWriteIL()==null || subjectLevel.getValue() >= query.getWriteIL().getValue();  // write-down
	}

}
