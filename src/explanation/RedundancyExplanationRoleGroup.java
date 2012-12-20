package explanation;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;

public class RedundancyExplanationRoleGroup extends RedundancyExplanation {

	public Set<RedundancyExplanation> explanations = new HashSet<RedundancyExplanation>();
	public OWLObjectSomeValuesFrom redundantRoleGroup;

	public static void main(String[] args) {
	}

	public void setRedundantRoleGroup(
			OWLObjectSomeValuesFrom redundantRoleGroupIn) {
		redundantRoleGroup = redundantRoleGroupIn;
	}

	public void addExplanation(RedundancyExplanation explanation) {
		explanations.add(explanation);
	}

	public String getExplanations() {
		String explanationsString = "";
		for (RedundancyExplanation explanation : explanations) {
			explanationsString = explanationsString + "\t"
					+ explanation.explanationString;
		}
		return explanationsString;
	}
}
