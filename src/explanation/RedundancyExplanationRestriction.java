package explanation;

import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;

public class RedundancyExplanationRestriction extends RedundancyExplanation {

	public OWLObjectSomeValuesFrom redundantRestriction;

	public static void main(String[] args) {
	}

	public void setRedundantRestriction(
			OWLObjectSomeValuesFrom redundantRestrictionIn) {
		redundantRestriction = redundantRestrictionIn;
	}
}
