package evaluation;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.File;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;

public class Benchmark {

	public static boolean save = true;
	public static String path = "/Users/kathrindentler/Dropbox/workspace/snomed_redundancies_tools/ontologies/";

	public static String ontologyname1 = "res_StatedOWLF_INT_20120731.owlf";
	public static String ontologyname2 = "snomed_nonredundant.owlf";

	public static void main(String[] args) {
		try {
			// Create an ELK reasoner factory.
			OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();

			System.out.println("Load ontology1");
			long start = System.currentTimeMillis();
			OWLOntology ontology1 = man
					.loadOntologyFromOntologyDocument(new File(path
							+ ontologyname1));
			long end = System.currentTimeMillis();
			long duration = end - start;
			System.out.println("Took so many seconds: " + (duration / 1000));

			System.out.println("Load ontology2");
			start = System.currentTimeMillis();
			OWLOntology ontology2 = man
					.loadOntologyFromOntologyDocument(new File(path
							+ ontologyname2));
			end = System.currentTimeMillis();
			duration = end - start;
			System.out.println("Took so many seconds: " + (duration / 1000));

			long ontology2sum = 0;
			int counter2 = 0;

			for (int j = 1; j < 11; j++) {

				System.out.println(j);
				counter2++;

				// Create an ELK reasoner.
				OWLReasoner reasoner = reasonerFactory
						.createReasoner(ontology2);

				start = System.currentTimeMillis();
				System.out.println("Start to classify ontology1");
				// Classify the ontology.
				reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
				System.out.println("Done with classification");
				end = System.currentTimeMillis();
				duration = end - start;
				ontology2sum += duration;
				System.out
						.println("Took so many seconds: " + (duration / 1000));

				reasoner.dispose();
			}

			long averageduration2 = ontology2sum / counter2;
			System.out.println("Took so many seconds on average: "
					+ (averageduration2 / 1000));

			long ontology1sum = 0;
			int counter1 = 0;

			for (int i = 1; i < 11; i++) {

				System.out.println(i);
				counter1++;

				// Create an ELK reasoner.
				OWLReasoner reasoner = reasonerFactory
						.createReasoner(ontology1);

				start = System.currentTimeMillis();
				System.out.println("Start to classify ontology1");
				// Classify the ontology.
				reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
				System.out.println("Done with classification");
				end = System.currentTimeMillis();
				duration = end - start;
				ontology1sum += duration;
				System.out
						.println("Took so many seconds: " + (duration / 1000));

				reasoner.dispose();
			}

			long averageduration = ontology1sum / counter1;
			System.out.println("Took so many seconds on average: "
					+ (averageduration / 1000));

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
}