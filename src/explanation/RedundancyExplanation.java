package explanation;

import org.semanticweb.owlapi.model.OWLClass;

public class RedundancyExplanation {

	public String explanationString;
	public Rule ruleCategory;
	public OWLClass superClass;
	public int distance;

	public enum Rule {
		rule1, rule2, rule3, rule4
	}

	public static void main(String args[]) {

	}

	public void setCategory(Rule category) {
		ruleCategory = category;
	}

	public void setExplanationString(String explanation) {
		explanationString = explanation;
	}

	public void setDistance(int distanceIn) {
		distance = distanceIn;
	}

	public void setSuperClass(OWLClass superClassIn) {
		superClass = superClassIn;
	}
}
