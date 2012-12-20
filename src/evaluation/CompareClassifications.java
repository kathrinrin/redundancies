package evaluation;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class CompareClassifications {

	public static void main(String args[]) {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		File original = new File(
				"/Users/kathrindentler/Dropbox/workspace/snomed_redundancies_tools/lib/res_StatedOWLF_INT_20120731_inferred.owlf");

		File nonredundant = new File(
				"/Users/kathrindentler/Dropbox/workspace/snomed_redundancies_tools/lib/snomed_nonredundant_inferred.owlf");

		try {

			OWLOntology originalOnt = manager
					.loadOntologyFromOntologyDocument(original
							.getAbsoluteFile());

			OWLOntology nonRedundantOnt = manager
					.loadOntologyFromOntologyDocument(nonredundant
							.getAbsoluteFile());

			System.out.println("");
			System.out.println("~~~");
			System.out.println("");

			Set<OWLLogicalAxiom> originalAxioms = originalOnt
					.getLogicalAxioms();
			System.out.println("\t Number of logical axioms originalOnt: "
					+ originalAxioms.size());

			int things = 0;
			int nothings = 0;
			int differences = 0;
			int equivalentClasses = 0;
			int equivalentProperties = 0;
			int containedCounter = 0;

			for (OWLAxiom originalAxiom : originalAxioms) {
				if (!nonRedundantOnt.containsAxiom(originalAxiom)) {

					System.out.println(originalAxiom);

					differences++;

					if ((originalAxiom.toString().contains("owl:Thing"))
							|| (originalAxiom.toString()
									.contains("owl:Nothing"))
							|| (originalAxiom.toString()
									.contains("EquivalentClasses"))
							|| (originalAxiom.toString()
									.contains("EquivalentObjectProperties"))) {

						if (originalAxiom.toString().contains("owl:Thing")) {
							things++;
							// System.out.println(axiom);
						}

						if (originalAxiom.toString().contains("owl:Nothing")) {
							nothings++;
							// System.out.println(axiom);
						}

						if (originalAxiom.getAxiomType().equals(
								AxiomType.EQUIVALENT_CLASSES)) {
							equivalentClasses++;
							OWLEquivalentClassesAxiom sco = (OWLEquivalentClassesAxiom) originalAxiom;

							Set<OWLClass> ocl = sco.getNamedClasses();
							if (ocl.size() == 0) {
								System.out.println("EMPTY");
							}
						}

						if (originalAxiom.toString().contains(
								"EquivalentObjectProperties")) {
							equivalentProperties++;
						}
					}
				}

				if (nonRedundantOnt.containsAxiom(originalAxiom)) {
					containedCounter++;
				}
			}

			System.out.println("  " + things + "\t subClasses of owl:Thing ");

			System.out.println("  " + nothings
					+ "\t superClasses of owl:Nothing");

			System.out.println("  " + equivalentClasses
					+ "\t EquivalentClasses ");

			System.out.println("  " + equivalentProperties
					+ "\t EquivalentProperties ");

			System.out.println("  " + containedCounter
					+ "\t Contained in original ontology ");

			System.out.println("  " + differences
					+ "\t Differences from original ontology ");

			System.out.println(" ");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}