package evaluation;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyInputSourceException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;

import util.Util;
import util.Util.ConceptType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ValidateRedundantConceptsCornet {

	public static boolean printSemanticType = true;
	private static boolean printMaxSize = true;
	private static boolean printNonredundants = false;
	private static boolean compareToAllRedundant = true;

	private static OWLDataFactory datafactory = OWLManager.getOWLDataFactory();
	private static OWLOntologyManager manager = OWLManager
			.createOWLOntologyManager();
	private static OWLOntology ontology;

	public final OWLReasonerFactory factory = new ElkReasonerFactory();
	private static OWLReasoner reasoner;

	private static BufferedWriter bufferedWriter;

	public static void main(String[] args) {

		try {

			Set<OWLClass> redundantClasses = new HashSet<OWLClass>();

			String strLine;
			if (compareToAllRedundant) {
				FileInputStream fstream = new FileInputStream(
						"results/RedundantConceptsRegardingExhaustive.txt");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));

				while ((strLine = br.readLine()) != null) {
					// System.out.println(strLine);

					redundantClasses.add(datafactory.getOWLClass(IRI
							.create(strLine.substring(strLine.indexOf("<") + 1,
									strLine.indexOf(">")))));
				}

				br.close();
				in.close();
			}

			System.out.println("redundantClasses: " + redundantClasses.size());

			bufferedWriter = new BufferedWriter(new FileWriter(new File(
					"results/EquivalentSubconceptsAll.txt")));

			// Create file
			System.setProperty("file.encoding", "UTF-8");

			System.out.println("Load ontology");
			long start = System.currentTimeMillis();

			ontology = manager.loadOntologyFromOntologyDocument(new File(
					"ontologies/res_StatedOWLF_INT_20120731.owlf"));
			Util.setOntology(ontology);

			long end = System.currentTimeMillis();
			long duration = end - start;
			System.out.println("Took so many seconds: " + (duration / 1000));

			OWLReasonerFactory factory = new ElkReasonerFactory();
			reasoner = factory.createReasoner(ontology);
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

			FileInputStream fistream = new FileInputStream(
					"results/ClassesWithEquivalentClass.txt");
			DataInputStream inStream = new DataInputStream(fistream);
			BufferedReader bread = new BufferedReader(new InputStreamReader(
					inStream));

			Map<OWLClass, Set<OWLClass>> equivalentSubClasses = new TreeMap<OWLClass, Set<OWLClass>>();

			int nontrivialprimitivecounter = 0;
			int trivialprimitivecounter = 0;
			int fullycounter = 0;

			while ((strLine = bread.readLine()) != null) {
				String delims = "\t";
				String[] tokens = strLine.split(delims);

				String equivalent1 = tokens[0];
				String equivalent2 = tokens[1];

				OWLClass equivalent1Class = datafactory.getOWLClass(IRI
						.create(equivalent1.substring(
								equivalent1.indexOf("<") + 1,
								equivalent1.indexOf(">"))));
				OWLClass equivalent2Class = datafactory.getOWLClass(IRI
						.create(equivalent2.substring(
								equivalent2.indexOf("<") + 1,
								equivalent2.indexOf(">"))));

				Set<OWLClass> superClasses1 = new HashSet<OWLClass>();
				superClasses1.addAll(Util.getAllClasses(equivalent1Class));

				Set<OWLClass> superClasses2 = new HashSet<OWLClass>();
				superClasses2.addAll(Util.getAllClasses(equivalent2Class));

				if ((superClasses1.contains(equivalent2Class))
						&& (Util.getConceptType(equivalent1Class)
								.equals(ConceptType.PrimitiveNonTrivial))) {

					Set<OWLClass> EquivalentSuperClasses = equivalentSubClasses
							.get(equivalent1Class);

					if (EquivalentSuperClasses == null)
						EquivalentSuperClasses = new HashSet<OWLClass>();

					if (!EquivalentSuperClasses.contains(equivalent2Class)) {

						EquivalentSuperClasses.add(equivalent2Class);
						equivalentSubClasses.put(equivalent1Class,
								EquivalentSuperClasses);

						ConceptType conceptType = Util
								.getConceptType(equivalent2Class);

						if (conceptType.equals(ConceptType.PrimitiveNonTrivial)) {
							nontrivialprimitivecounter++;
						}

						if (conceptType.equals(ConceptType.PrimitiveTrivial)) {
							trivialprimitivecounter++;
						}

						if (conceptType.equals(ConceptType.FullyDefined)) {
							fullycounter++;
						}
					}
				}

				else if ((superClasses2.contains(equivalent1Class))
						&& (Util.getConceptType(equivalent2Class)
								.equals(ConceptType.PrimitiveNonTrivial))) {

					Set<OWLClass> EquivalentSuperClasses = equivalentSubClasses
							.get(equivalent2Class);
					if (EquivalentSuperClasses == null)
						EquivalentSuperClasses = new HashSet<OWLClass>();

					if (!EquivalentSuperClasses.contains(equivalent1Class)) {

						EquivalentSuperClasses.add(equivalent1Class);

						equivalentSubClasses.put(equivalent2Class,
								EquivalentSuperClasses);

						ConceptType conceptType = Util
								.getConceptType(equivalent1Class);

						if (conceptType.equals(ConceptType.PrimitiveNonTrivial)) {
							nontrivialprimitivecounter++;
						}

						if (conceptType.equals(ConceptType.PrimitiveTrivial)) {
							trivialprimitivecounter++;
						}

						if (conceptType.equals(ConceptType.FullyDefined)) {
							fullycounter++;
						}
					}
				}
			}

			System.out.println("nontrivialprimitivecounter: "
					+ nontrivialprimitivecounter);
			System.out.println("trivialprimitivecounter: "
					+ trivialprimitivecounter);
			System.out.println("fullycounter: " + fullycounter);

			System.out.println("Equivalent Subclasses: "
					+ equivalentSubClasses.size());
			System.out.println();

			int maxNumberOfSuperClasses = 0;
			int totalSuperClasses = 0;

			for (Map.Entry<OWLClass, Set<OWLClass>> entry : equivalentSubClasses
					.entrySet()) {
				OWLClass subclass = entry.getKey();
				Set<OWLClass> superclasses = entry.getValue();
				totalSuperClasses += superclasses.size();

				if (printMaxSize) {
					if (superclasses.size() > maxNumberOfSuperClasses) {
						maxNumberOfSuperClasses = superclasses.size();
						System.out
								.println(maxNumberOfSuperClasses
										+ " superclasses: "
										+ subclass
												.getAnnotationAssertionAxioms(ontology));
					}
				}

				if (printNonredundants) {
					if (!redundantClasses.contains(subclass)) {
						System.out.println("Should be redundant but is not: "
								+ subclass.toString());

						System.out.println(" (superclasses: "
								+ superclasses.toString() + ")");

						System.out.println();
					}
				}

				if (!Util.getConceptType(subclass).equals(
						ConceptType.PrimitiveNonTrivial)) {
					System.out
							.println("Should be PrimitiveNonTrivial but is not: "
									+ subclass
									+ " "
									+ Util.getConceptType(subclass));
				}

				bufferedWriter.write(subclass.toString() + "\t"
						+ superclasses.toString());
				bufferedWriter.newLine();
			}

			System.out.println("totalSuperClasses: " + totalSuperClasses);

			bufferedWriter.flush();
			bufferedWriter.close();

			reasoner.dispose();

		} catch (OWLOntologyInputSourceException e) {
			System.out
					.println("File not found, probably wrong path. The SNOMED OWL file needs to be located in the lib folder!");
		}

		catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		catch (FileNotFoundException e) {
			System.out
					.println("File not found. Probably the results folder is missing, or the file EquivalentSubconceptsAll.txt does not exist.");
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}
}