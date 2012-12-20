package evaluation;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Set;

public class MergeOntologies {

	public static void main(String[] args) {

		String ontologyname = "snomed_nonredundant.owlf";

		File one = new File("ontologies/" + ontologyname);
		File two = new File("ontologies/inferred_" + ontologyname);

		SecureRandom random = new SecureRandom();

		try {
			// Just load two arbitrary ontologies for the purposes of this
			// example
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology file1 = manager.loadOntologyFromOntologyDocument(one
					.getAbsoluteFile());
			OWLOntology file2 = manager.loadOntologyFromOntologyDocument(two
					.getAbsoluteFile());

			System.out.println("Size file1: " + file1.getAxiomCount());
			System.out.println("Size file2: " + file2.getAxiomCount());

			Set<OWLAxiom> axioms = file2.getAxioms();

			int infcontainedinsnomed = 0;
			for (OWLAxiom axiom : axioms) {
				if (file1.containsAxiom(axiom)) {
					infcontainedinsnomed++;
				}
			}
			System.out
					.println("Inferences that are contained in the original file: "
							+ infcontainedinsnomed);

			// Create our ontology merger
			OWLOntologyMerger merger = new OWLOntologyMerger(manager);

			IRI mergedOntologyIRI = IRI.create("http://"
					+ new BigInteger(130, random).toString(32) + ".org");
			OWLOntology merged = merger.createMergedOntology(manager,
					mergedOntologyIRI);

			System.out.println("Merged: " + merged.getAxiomCount());
			System.out.println("Counted together: "
					+ (file1.getAxiomCount() + file2.getAxiomCount()));

			// Save to RDF/XML
			System.out.println(System.getProperty("user.dir"));
			manager.saveOntology(
					merged,
					new RDFXMLOntologyFormat(),
					IRI.create("file:" + System.getProperty("user.dir")
							+ "/ontologies/closure_" + ontologyname));
		} catch (OWLOntologyCreationException e) {
			System.out.println("Could not load ontology: " + e.getMessage());
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
