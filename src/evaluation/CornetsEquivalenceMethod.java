package evaluation;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.OWLOntologyInputSourceException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import uk.ac.manchester.cs.bhig.util.Tree;
import uk.ac.manchester.cs.owl.explanation.ordering.DefaultExplanationOrderer;
import uk.ac.manchester.cs.owl.explanation.ordering.ExplanationTree;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;
import util.Util;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;

public class CornetsEquivalenceMethod {

	public static boolean print = false;
	public static int anoncounter = 0;

	public static Set<OWLClass> ClassesWithEquivalentClass = new HashSet<OWLClass>();

	public static BufferedWriter bufferedWriter;
	public static BufferedWriter explanationWriter;

	public static boolean explanations = false;

	public static void main(String[] args) {

		try {

			bufferedWriter = new BufferedWriter(new FileWriter(new File(
					"results/ClassesWithEquivalentClass.txt")));

			if (explanations) {
				explanationWriter = new BufferedWriter(new FileWriter(new File(
						"results/Explanations.txt")));
			}

			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory df = OWLManager.getOWLDataFactory();

			Map<OWLClass, OWLClass> map = new TreeMap<OWLClass, OWLClass>();

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_206099001")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_81402009")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_51435005")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_226119005")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_206089006")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_81402009")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_166890005")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_166921001")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_442634006")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_442096005")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_206090002")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_81402009")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_206091003")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_81402009")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_176422003")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_77739005")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_104345003")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_2220009")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_188857008")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_92432002")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_204621006")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_304067009")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_8929007")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_36889006")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_209492000")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_263130004")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_188445006")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_94627008")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_445334007")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_253414002")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_410166000")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_385791001")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_35899005")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_281531008")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_71101001")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_72604007")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_238890008")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_446933000")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_13067005")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_77068002")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_68708005")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_446836009")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_202617003")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_299576008")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_198377006")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_310789003")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_10699001")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_397825006")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_78698008")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_33159007")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_288018003")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_238257004")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_410164002")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_385789009")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_202295002")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_239743009")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_51338006")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_359804008")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_109839004")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_93669004")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_81338003")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_171964005")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_441325006")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_439090000")));

			map.put(df.getOWLClass(IRI
					.create("http://www.ihtsdo.org/SCT_166892002")), df
					.getOWLClass(IRI
							.create("http://www.ihtsdo.org/SCT_444780001")));

			System.out.println("Equivalent size: " + map.entrySet().size());

			System.out.println("Load ontology");
			long start = System.currentTimeMillis();

			OWLOntology ontology = manager
					.loadOntologyFromOntologyDocument(new File(
							"ontologies/res_StatedOWLF_INT_20120731.owlf"));

			long end = System.currentTimeMillis();
			long duration = end - start;
			System.out.println("Took so many seconds: " + (duration / 1000));

			OWLReasoner reasoner = null;

			if (explanations) {
				reasoner = PelletReasonerFactory.getInstance()
						.createNonBufferingReasoner(ontology);
			}

			else {
				OWLReasonerFactory factory = new ElkReasonerFactory();
				reasoner = factory.createReasoner(ontology);
			}

			Set<OWLSubClassOfAxiom> subclasses = ontology
					.getAxioms(AxiomType.SUBCLASS_OF);

			for (OWLSubClassOfAxiom subclassaxiom : subclasses) {

				OWLClass subclass = (OWLClass) subclassaxiom.getSubClass();
				OWLClassExpression superClassExpression = subclassaxiom
						.getSuperClass();

				if (superClassExpression.isAnonymous()) {
					anoncounter++;

					OWLAxiom axiom = df.getOWLEquivalentClassesAxiom(subclass,
							superClassExpression);

					RemoveAxiom removeAxiom = new RemoveAxiom(ontology,
							subclassaxiom);
					manager.applyChange(removeAxiom);

					AddAxiom addAxiom = new AddAxiom(ontology, axiom);
					manager.applyChange(addAxiom);

				}
			}

			System.out.println("Anonymous (non-trivial) primitive concepts: "
					+ anoncounter);
			System.out.println();
			System.out.println("Start to classify");
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
			System.out.println("Done with classification");
			System.out.println();

			Set<OWLLogicalAxiom> allAxioms = ontology.getLogicalAxioms();
			for (OWLLogicalAxiom logicalAxiom : allAxioms) {

				if ((logicalAxiom.getAxiomType()
						.equals(AxiomType.EQUIVALENT_CLASSES))
						|| (logicalAxiom.getAxiomType()
								.equals(AxiomType.SUBCLASS_OF))) {

					OWLClass namedClass = Util
							.getNamedClassFromAxiom(logicalAxiom);

					if (print)
						System.out.println(namedClass.toString());

					Set<OWLClass> nodeSet = reasoner.getEquivalentClasses(
							namedClass).getEntities();

					if (nodeSet.size() > 1) {
						if (print)
							System.out
									.println("More than one equivalent class!!!");
					}

					for (OWLClass node : nodeSet) {
						if (!node.toString().equals(namedClass.toString())) {
							ClassesWithEquivalentClass.add(namedClass);

							bufferedWriter.write(namedClass.toString() + "\t"
									+ node.toString());
							bufferedWriter.newLine();

							if (print)
								System.out.println(namedClass.toString() + " "
										+ namedClass.getAnnotations(ontology));
							if (print)
								System.out.println("Equivalent class: " + node
										+ " " + node.getAnnotations(ontology));

							if (explanations) {
								java.util.Iterator<Entry<OWLClass, OWLClass>> it = map
										.entrySet().iterator();
								while (it.hasNext()) {
									Map.Entry<OWLClass, OWLClass> pairs = it
											.next();
									if (pairs.getKey().equals(namedClass)
											&& pairs.getValue().equals(node)) {

										explanationWriter
												.write("Indeed equivalent");
										explanationWriter.newLine();

										OWLAxiom equivalentaxiom = df
												.getOWLEquivalentClassesAxiom(
														pairs.getKey(),
														pairs.getValue());

										DefaultExplanationGenerator explanationGenerator = new DefaultExplanationGenerator(
												manager,
												PelletReasonerFactory
														.getInstance(),
												ontology,
												reasoner,
												new SilentExplanationProgressMonitor());
										Set<OWLAxiom> explanation = explanationGenerator
												.getExplanation(equivalentaxiom);
										explanationWriter.write(explanation
												.toString());
										explanationWriter.newLine();
										DefaultExplanationOrderer deo = new DefaultExplanationOrderer();
										ExplanationTree explanationTree = deo
												.getOrderedExplanation(
														equivalentaxiom,
														explanation);
										explanationWriter.newLine();

										explanationWriter.newLine();
										explanationWriter.write(explanationTree
												.toString());
										explanationWriter.newLine();
										explanationWriter.newLine();
										explanationWriter
												.write("-- explanation "
														+ pairs.getKey()
														+ " is equivalent to "
														+ pairs.getValue()
														+ " --");
										printIndented(explanationTree, "");
										explanationWriter.newLine();
										explanationWriter.newLine();
										explanationWriter.newLine();
									}
								}
							}
						}
					}
				}
			}

			if (explanations) {
				explanationWriter.flush();
				explanationWriter.close();
			}

			bufferedWriter.flush();
			bufferedWriter.close();

			System.out.println("ClassesWithEquivalentClass: "
					+ ClassesWithEquivalentClass.size());

			reasoner.dispose();
		}

		catch (OWLOntologyInputSourceException e) {
			System.out
					.println("File not found, probably wrong path. The SNOMED OWL file needs to be located in the lib folder!");
		}

		catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		catch (FileNotFoundException e) {
			System.out
					.println("File not found. Probably the results folder is missing.");
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printIndented(Tree<OWLAxiom> node, String indent)
			throws IOException {
		OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();

		OWLAxiom axiom = node.getUserObject();
		explanationWriter.write(indent + renderer.render(axiom));
		explanationWriter.newLine();
		if (!node.isLeaf()) {
			for (Tree<OWLAxiom> child : node.getChildren()) {
				printIndented(child, indent + "    ");
			}
		}
	}
}