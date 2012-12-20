package explanation;

import org.semanticweb.owlapi.model.OWLClass;

public class RedundancyExplanationClass extends RedundancyExplanation {

	public OWLClass redundantClass;

	public static void main(String[] args) {
	}

	public void setRedundantClass(OWLClass redundantClassIn) {
		redundantClass = redundantClassIn;
	}
}
