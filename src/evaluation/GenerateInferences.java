package evaluation;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;

public class GenerateInferences {

	public static boolean save = true;
	public static String path = "/Users/kathrindentler/Dropbox/workspace/snomed_redundancies_tools/ontologies/";
	public static String ontologyname = "snomed_nonredundant.owlf";

	public static void main(String[] args) {
		try {
			// Create an ELK reasoner factory.
			OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();

			System.out.println("Load ontology");
			long start = System.currentTimeMillis();

			// Load your ontology.
			OWLOntology ont = man.loadOntologyFromOntologyDocument(new File(
					path + ontologyname));
			long end = System.currentTimeMillis();
			long duration = end - start;
			System.out.println("Took so many seconds: " + (duration / 1000));

			// Create an ELK reasoner.
			OWLReasoner reasoner = reasonerFactory.createReasoner(ont);

			start = System.currentTimeMillis();
			System.out.println("Start to classify");
			// Classify the ontology.
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
			System.out.println("Done with classification");
			end = System.currentTimeMillis();
			duration = end - start;
			System.out.println("Took so many seconds: " + (duration / 1000));

			if (save) {
				// To generate an inferred ontology we use implementations of
				// inferred axiom generators
				List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
				gens.add(new InferredSubClassAxiomGenerator());
				gens.add(new InferredEquivalentClassAxiomGenerator());

				// Put the inferred axioms into a fresh empty ontology.
				OWLOntology infOnt = man.createOntology();
				InferredOntologyGenerator iog = new InferredOntologyGenerator(
						reasoner, gens);
				iog.fillOntology(man, infOnt);

				// Save the inferred ontology.
				man.saveOntology(
						infOnt,
						new OWLFunctionalSyntaxOntologyFormat(),
						IRI.create((new File("ontologies/inferred_"
								+ ontologyname).toURI())));
			}
			reasoner.dispose();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}
}