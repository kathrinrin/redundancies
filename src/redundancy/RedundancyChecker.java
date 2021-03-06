package redundancy;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLOntologyInputSourceException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import util.Util;
import util.Util.ConceptType;

import explanation.RedundancyExplanation;
import explanation.RedundancyExplanation.Rule;
import explanation.RedundancyExplanationClass;
import explanation.RedundancyExplanationRestriction;
import explanation.RedundancyExplanationRoleGroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class RedundancyChecker {

	public static OWLOntology ontology;
	public static OWLOntology manipulatedOntology;
	private static OWLReasoner reasoner;

	private static boolean doAll = false;
	private static boolean doList = false;
	private static boolean doConcept = true;

	private static boolean doRule1 = true;
	private static boolean doRule2 = true;
	private static boolean doRule3 = true;
	private static boolean doRule4 = true;

	private static boolean saveManipulatedOntology = false;
	private static boolean exhaustiveSearch = false;
	private static boolean doSuperClasses = true;

	private static boolean printSemanticType = true;
	private static boolean printDistance = true;
	private static boolean printExplanations = true;
	private static boolean printNumberOfRedundantElements = true;

	private static OWLOntologyManager manager = OWLManager
			.createOWLOntologyManager();
	private static OWLOntologyManager manipulatedManager = OWLManager
			.createOWLOntologyManager();
	private static OWLDataFactory datafactory = manager.getOWLDataFactory();
	private static OWLDataFactory manipulatedDataFactory = manipulatedManager
			.getOWLDataFactory();

	private static Set<OWLClass> redundantConcepts = new HashSet<OWLClass>();
	private static Set<OWLClass> fullyDefinedSet = new HashSet<OWLClass>();
	private static Set<OWLClass> primitiveTrivialSet = new HashSet<OWLClass>();
	private static Set<OWLClass> primitiveNonTrivialSet = new HashSet<OWLClass>();

	private static BufferedWriter bufferedWriter;

	private static int onlyconceptscounter = 0;

	private static int[] distances = new int[50];
	private static int[] numberofexplanations = new int[500];
	private static int[] numberofredundantelements = new int[50];

	private static int maxDistance = 0;
	private static int maxExplanations = 0;
	private static int maxRedundantElements = 0;

	private static int explanationcounter = 0;
	private static int rule1counter = 0;
	private static int rule2counter = 0;
	private static int rule3counter = 0;
	private static int rule4counter = 0;

	private static Set<OWLClass> semanticTypes = Util.getSemanticTypes();
	private static Map<String, Integer> typeMap = new HashMap<String, Integer>();

	public static void main(String[] args) {

		try {

			if (doAll && doSuperClasses && !exhaustiveSearch) {
				bufferedWriter = new BufferedWriter(new FileWriter(new File(
						"results/RedundantConceptsRegarding.txt")));
			} else if (doAll && doSuperClasses && exhaustiveSearch) {
				bufferedWriter = new BufferedWriter(new FileWriter(new File(
						"results/RedundantConceptsRegardingExhaustive.txt")));
			} else if (doAll && !doSuperClasses) {
				bufferedWriter = new BufferedWriter(new FileWriter(new File(
						"results/RedundantConceptsDisregarding.txt")));
			} else {
				bufferedWriter = new BufferedWriter(new FileWriter(new File(
						"results/RedundantConcepts.txt")));
			}

			System.out.println("Load ontology");
			long start = System.currentTimeMillis();

			File ontologyfile = new File(
					"ontologies/SnomedCT_Release_INT_20120731_mini.owlf");

			ontology = manager.loadOntologyFromOntologyDocument(ontologyfile);
			Util.setOntology(ontology);

			if (saveManipulatedOntology)
				manipulatedOntology = manipulatedManager
						.loadOntologyFromOntologyDocument(ontologyfile);

			long end = System.currentTimeMillis();
			long duration = end - start;
			System.out.println("Took so many seconds: " + (duration / 1000));

			OWLReasonerFactory factory = new ElkReasonerFactory();
			reasoner = factory.createReasoner(ontology);
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

			for (OWLClass semanticType : semanticTypes) {
				typeMap.put(Util.getSemanticType(semanticType), 0);
			}

			start = System.currentTimeMillis();

			if (doAll) {
				int counter = 0;
				Set<OWLLogicalAxiom> allAxioms = ontology.getLogicalAxioms();
				System.out.println("So many logical axioms: "
						+ allAxioms.size());

				for (OWLLogicalAxiom logicalAxiom : allAxioms) {

					if ((logicalAxiom.getAxiomType()
							.equals(AxiomType.EQUIVALENT_CLASSES))
							|| (logicalAxiom.getAxiomType()
									.equals(AxiomType.SUBCLASS_OF))) {

						counter++;

						if ((counter % 500) == 0) {
							System.out.println(counter);
							System.gc();
						}

						OWLClass rootClass = Util
								.getNamedClassFromAxiom(logicalAxiom);
						isConceptRedundant(rootClass);
					}
				}
			}

			if (doList) {

				Set<OWLClass> classes = new HashSet<OWLClass>();

				FileInputStream fstream = new FileInputStream(
						"results/EquivalentSubconceptsAll.txt");

				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				String strLine;
				while ((strLine = br.readLine()) != null) {
					classes.add(datafactory.getOWLClass(IRI.create(strLine
							.substring(strLine.indexOf('<') + 1,
									strLine.indexOf('>')))));
				}

				br.close();
				in.close();
				fstream.close();

				System.out.println("So many classes: " + classes.size());

				for (OWLClass rootClass : classes) {
					isConceptRedundant(rootClass);
				}
			}

			if (doConcept) {
				OWLClass classOfInterest = datafactory.getOWLClass(IRI
						.create("http://www.ihtsdo.org/SCT_47324001"));
				isConceptRedundant(classOfInterest);
			}

			bufferedWriter.flush();
			bufferedWriter.close();
			System.out.println();

			System.out.println("onlyconceptscounter: " + onlyconceptscounter);

			end = System.currentTimeMillis();
			duration = end - start;
			System.out.println("Took so many seconds: " + (duration / 1000));

			System.out.println();
			System.out.println("Redundant concepts: "
					+ redundantConcepts.size());
			System.out.println();

			System.out.println("So many explanations: " + explanationcounter);
			System.out.println("of which rule 1: " + rule1counter);
			System.out.println("of which rule 2: " + rule2counter);
			System.out.println("of which rule 3: " + rule3counter);
			System.out.println("of which rule 4: " + rule4counter);
			System.out.println();

			if (printDistance) {
				for (int i = 0; i < distances.length; i++) {
					if (distances[i] != 0) {
						System.out.println("Distance " + i + ": "
								+ distances[i] + " times");
					}
				}
			}

			System.out.println();
			if (printSemanticType) {
				for (Entry<String, Integer> entry : typeMap.entrySet()) {
					if (entry.getValue() != 0) {
						System.out.println(entry.getKey() + ": "
								+ entry.getValue());
					}
				}
			}

			System.out.println();
			if (printExplanations) {
				for (int i = 0; i < numberofexplanations.length; i++) {
					if (numberofexplanations[i] != 0) {
						System.out.println(i + " numberofexplanations: "
								+ numberofexplanations[i] + " times");
					}
				}
			}

			System.out.println();
			if (printNumberOfRedundantElements) {
				for (int i = 0; i < numberofredundantelements.length; i++) {
					if (numberofredundantelements[i] != 0) {
						System.out.println(i + " numberofredundantelements: "
								+ numberofredundantelements[i] + " times");
					}
				}
			}

			System.out.println();
			Set<OWLClass> intersection = new HashSet<OWLClass>(fullyDefinedSet);
			intersection.retainAll(redundantConcepts);
			System.out.println("FullyDefined: " + fullyDefinedSet.size());
			System.out.println("Of which redundant: " + intersection.size());

			intersection = new HashSet<OWLClass>(primitiveNonTrivialSet);
			intersection.retainAll(redundantConcepts);
			System.out.println("PrimitiveNonTrivial: "
					+ primitiveNonTrivialSet.size());
			System.out.println("Of which redundant: " + intersection.size());

			intersection = new HashSet<OWLClass>(primitiveTrivialSet);
			intersection.retainAll(redundantConcepts);
			System.out.println("PrimitiveTrivial: "
					+ primitiveTrivialSet.size());

			System.out.println("Of which redundant: " + intersection.size());

			if (saveManipulatedOntology) {
				String myNewDir = "ontologies";
				new File(myNewDir).mkdir();
				File outFile = new File(myNewDir, "snomed_nonredundant.owlf");

				manipulatedManager.saveOntology(manipulatedOntology,
						new OWLFunctionalSyntaxOntologyFormat(),
						IRI.create(outFile.toURI()));
				System.out.println("saved");
			}

			reasoner.dispose();

		} catch (OWLOntologyInputSourceException e) {
			System.out
					.println("File not found, probably wrong path. The SNOMED OWL file needs to be located in the ontologies folder!");
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
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

	public static Set<RedundancyExplanation> getRedundantExplanations(
			Set<RedundancyExplanation> explanationSet,
			List<OWLObjectSomeValuesFrom> searchedRoleGroups,
			List<OWLObjectSomeValuesFrom> searchedUngroupedRoles,
			OWLClass rootClass, OWLClass highClass, Boolean first, int distance)
			throws IOException {

		if (!highClass.toString().equals(
				"<http://www.ihtsdo.org/SCT_138875005>")) {

			distance = distance + 1;

			OWLClassExpression superClassExpression = Util
					.getOWLClassExpressionFromClass(highClass);
			
			
			System.out.println(highClass);
			
			

			if (superClassExpression.getClassExpressionType().equals(
					ClassExpressionType.OBJECT_INTERSECTION_OF)) {

				List<OWLObjectSomeValuesFrom> roleGroups = Util
						.getAllRoleGroups(highClass);

				List<OWLObjectSomeValuesFrom> unGroupedRoles = Util
						.getAllUngrouped(highClass);

				if (first) {
					searchedRoleGroups = roleGroups;
					searchedUngroupedRoles = unGroupedRoles;
				}

				// 1) An ungrouped exists restriction is redundant when it is
				// equivalent to or more general than an ungrouped
				// exists restriction within the definition of the same concept
				// or a supertype.

				if (doRule1) {
					for (int i = 0; i < searchedUngroupedRoles.size(); i++) {
						for (int j = 0; j < unGroupedRoles.size(); j++) {

							RedundancyExplanationRestriction explanation = null;

							if (first) {
								if (i != j) {
									explanation = isRestrictionRedundant(
											searchedUngroupedRoles.get(i),
											unGroupedRoles.get(j));
								}
							}

							else {
								explanation = isRestrictionRedundant(
										searchedUngroupedRoles.get(i),
										unGroupedRoles.get(j));
							}

							if (explanation != null) {
								if (!exhaustiveSearch) {
									if (explanationSet.size() == 0)
										addExplanation(explanation,
												explanationSet, Rule.rule1,
												highClass, distance,
												searchedUngroupedRoles.get(i),
												null, null);
									return explanationSet;
								}

								addExplanation(explanation, explanationSet,
										Rule.rule1, highClass, distance,
										searchedUngroupedRoles.get(i), null,
										null);
							}
						}
					}
				}

				// 2) A rolegroup is redundant when all its exists restrictions
				// are equivalent to or more general than those contained in
				// another rolegroup in the definition of the same concept or a
				// supertype.

				if (doRule2) {

					// System.out.println("Rule 2");
					// System.out.println("rootClass: " + rootClass);
					// System.out.println("highClass: " + highClass);
					// System.out.println("searchedRoleGroups: "
					// + searchedRoleGroups);
					// System.out.println("roleGroups: " + roleGroups);
					// System.out.println();

					for (int i = 0; i < searchedRoleGroups.size(); i++) {
						for (int j = 0; j < roleGroups.size(); j++) {
							RedundancyExplanationRoleGroup explanation = null;

							if (first) {
								if (i != j) {
									explanation = isRoleGroupRedundant(
											searchedRoleGroups.get(i),
											roleGroups.get(j));
								}
							}

							else {
								explanation = isRoleGroupRedundant(
										searchedRoleGroups.get(i),
										roleGroups.get(j));
							}

							if (explanation != null) {
								if (!exhaustiveSearch) {
									if (explanationSet.size() == 0)
										addExplanation(explanation,
												explanationSet, Rule.rule2,
												highClass, distance, null,
												searchedRoleGroups.get(i), null);
									return explanationSet;
								}

								addExplanation(explanation, explanationSet,
										Rule.rule2, highClass, distance, null,
										searchedRoleGroups.get(i), null);
							}
						}
					}
				}

				// 3) An exists restriction is redundant within a rolegroup when
				// it is equivalent to or more general than another exists
				// restriction in the same rolegroup.

				if (doRule3) {
					if (first) {
						for (OWLObjectSomeValuesFrom roleGroup : roleGroups) {

							List<OWLObjectSomeValuesFrom> existRestrictions = Util
									.getExistRestrictionsFromRoleGroup(roleGroup);

							for (int i = 0; i < existRestrictions.size(); i++) {
								for (int j = 0; j < existRestrictions.size(); j++) {
									if (i != j) {

										RedundancyExplanationRestriction explanation = isRestrictionRedundant(
												existRestrictions.get(i),
												existRestrictions.get(j));

										if (explanation != null) {
											if (!exhaustiveSearch) {
												if (explanationSet.size() == 0)
													addExplanation(explanation,
															explanationSet,
															Rule.rule3,
															highClass,
															distance,
															existRestrictions
																	.get(i),
															null, null);
												return explanationSet;
											}

											addExplanation(explanation,
													explanationSet, Rule.rule3,
													highClass, distance,
													existRestrictions.get(i),
													null, null);
										}
									}
								}
							}
						}
					}
				}

				// 4) A concept that is part of a concept definition is
				// redundant when it is equivalent to or more general than one
				// of the other concepts in the definition of the same concept.

				if (doRule4) {

					RedundancyExplanationClass explanation = isClassRedundant(
							rootClass, highClass);

					if (explanation != null) {

						if (!exhaustiveSearch) {
							if (explanationSet.size() == 0)
								addExplanation(explanation, explanationSet,
										Rule.rule4, highClass, distance, null,
										null, rootClass);
							return explanationSet;
						}

						addExplanation(explanation, explanationSet, Rule.rule4,
								highClass, distance, null, null, rootClass);
					}
				}
			}

			if (doSuperClasses
					&& ((searchedRoleGroups != null) || (searchedUngroupedRoles != null))) {
				List<OWLClass> superClasses = Util.getAllClasses(highClass);
				for (OWLClass superclass : superClasses) {
					getRedundantExplanations(explanationSet,
							searchedRoleGroups, searchedUngroupedRoles,
							rootClass, superclass, false, distance);
				}
			}
		}
		return explanationSet;
	}

	private static Set<RedundancyExplanation> addExplanation(
			RedundancyExplanation explanation,
			Set<RedundancyExplanation> explanationSet, Rule rule,
			OWLClass highClass, int distance, OWLObjectSomeValuesFrom some,
			OWLObjectSomeValuesFrom roleGroup, OWLClass redundantClass) {

		boolean contained = false;

		for (RedundancyExplanation setExplanation : explanationSet) {

			if (rule.equals(Rule.rule1) || rule.equals(Rule.rule3)) {
				if (setExplanation.ruleCategory.equals(rule)
						&& setExplanation.superClass.equals(highClass)
						&& setExplanation.distance == distance
						&& ((RedundancyExplanationRestriction) setExplanation).redundantRestriction
								.equals(some)) {
					contained = true;
				}
			}

			if (rule.equals(Rule.rule2)) {
				if (setExplanation.ruleCategory.equals(rule)
						&& setExplanation.superClass.equals(highClass)
						&& setExplanation.distance == distance
						&& ((RedundancyExplanationRoleGroup) setExplanation).redundantRoleGroup
								.equals(roleGroup)) {
					contained = true;
				}
			}

			if (rule.equals(Rule.rule4)) {
				if (setExplanation.ruleCategory.equals(rule)
						&& setExplanation.superClass.equals(highClass)
						&& setExplanation.distance == distance
						&& ((RedundancyExplanationClass) setExplanation).redundantClass
								.equals(redundantClass)) {
					contained = true;
				}
			}
		}

		if (!contained) {

			if (rule.equals(Rule.rule1) || rule.equals(Rule.rule3)) {
				explanation.setCategory(rule);
				explanation.setSuperClass(highClass);
				explanation.setDistance(distance);
				((RedundancyExplanationRestriction) explanation)
						.setRedundantRestriction(some);
				explanationSet.add(explanation);
			}

			if (rule.equals(Rule.rule2)) {
				explanation.setCategory(rule);
				explanation.setSuperClass(highClass);
				explanation.setDistance(distance);
				((RedundancyExplanationRoleGroup) explanation)
						.setRedundantRoleGroup(roleGroup);
				explanationSet.add(explanation);
			}

			if (rule.equals(Rule.rule4)) {
				explanation.setCategory(rule);
				explanation.setSuperClass(highClass);
				explanation.setDistance(distance);
				((RedundancyExplanationClass) explanation)
						.setRedundantClass(redundantClass);
				explanationSet.add(explanation);
			}
		}

		return explanationSet;
	}

	public static RedundancyExplanationClass isClassRedundant(
			OWLClass rootClass, OWLClass highClass) {

		List<OWLClass> rootClasses = Util.getAllClasses(rootClass);
		List<OWLClass> highClasses = Util.getAllClasses(highClass);

		if ((rootClasses.size() > 0) && (highClasses.size() > 0)) {

			// not redundant within searched class group
			for (int i = 0; i < rootClasses.size(); i++) {
				for (int j = 0; j < highClasses.size(); j++) {

					if (rootClass.equals(highClass)) {
						if (i != j) {

							if (reasoner
									.getSubClasses(rootClasses.get(i), false)
									.getFlattened()
									.contains(highClasses.get(j))) {

								RedundancyExplanationClass explanation = new RedundancyExplanationClass();
								explanation.setExplanationString(rootClasses
										.get(i)
										+ " more general than "
										+ rootClasses.get(j));

								explanation.setSuperClass(highClass);
								explanation.setDistance(0);
								explanation.setRedundantClass(rootClasses
										.get(i));
								return explanation;
							}
						}
					}

					else {
						if (reasoner.getSubClasses(rootClasses.get(i), false)
								.getFlattened().contains(highClasses.get(j))) {

							RedundancyExplanationClass explanation = new RedundancyExplanationClass();
							explanation.setExplanationString(rootClasses.get(i)
									+ " more general than "
									+ highClasses.get(j));

							explanation.setSuperClass(highClass);
							explanation.setDistance(0);
							explanation.setRedundantClass(rootClasses.get(i));
							return explanation;
						}
					}
				}
			}
		}
		return null;
	}

	public static RedundancyExplanationRoleGroup isRoleGroupRedundant(
			OWLObjectSomeValuesFrom searchedRoleGroup,
			OWLObjectSomeValuesFrom roleGroupComp) {

		if (searchedRoleGroup.equals(roleGroupComp)) {
			RedundancyExplanationRoleGroup rolegroupexplanation = new RedundancyExplanationRoleGroup();
			rolegroupexplanation.setExplanationString("Role groups equal: "
					+ searchedRoleGroup + " " + roleGroupComp);
			return rolegroupexplanation;
		}

		List<OWLObjectSomeValuesFrom> restrictionsSearched = Util
				.getExistRestrictionsFromRoleGroup(searchedRoleGroup);

		List<OWLObjectSomeValuesFrom> restrictionsComp = Util
				.getExistRestrictionsFromRoleGroup(roleGroupComp);

		Boolean foundAllInComp = true;

		RedundancyExplanationRoleGroup rolegroupexplanation = new RedundancyExplanationRoleGroup();
		for (OWLObjectSomeValuesFrom restrictionSearched : restrictionsSearched) {

			Boolean foundOneInComp = false;

			for (OWLObjectSomeValuesFrom existRestrictionComp : restrictionsComp) {

				RedundancyExplanationRestriction exp = isRestrictionRedundant(
						restrictionSearched, existRestrictionComp);

				if ((exp != null)) {
					foundOneInComp = true;
					rolegroupexplanation.addExplanation(exp);
				}
			}

			if (!foundOneInComp) {

				foundAllInComp = false;
			}
		}

		if (foundAllInComp)
			return rolegroupexplanation;
		else
			return null;
	}

	public static RedundancyExplanationRestriction isRestrictionRedundant(
			OWLObjectSomeValuesFrom existsRestrictionSearched,
			OWLObjectSomeValuesFrom existsRestrictionComp) {

		if (existsRestrictionSearched.equals(existsRestrictionComp)) {
			RedundancyExplanationRestriction e = new RedundancyExplanationRestriction();
			e.setExplanationString("Equal Restrictions: "
					+ existsRestrictionSearched + "\t" + existsRestrictionComp);
			return e;
		}

		OWLObjectPropertyExpression existsPropertySearched = existsRestrictionSearched
				.getProperty();
		OWLObjectPropertyExpression existsPropertyComp = existsRestrictionComp
				.getProperty();

		OWLClass existsClassSearched = (OWLClass) existsRestrictionSearched
				.getFiller();
		OWLClass existsClassComp = (OWLClass) existsRestrictionComp.getFiller();

		Set<OWLClass> existsClassSearchedSubs = reasoner.getSubClasses(
				existsClassSearched, false).getFlattened();

		if (existsPropertySearched.equals(existsPropertyComp)) {

			if ((existsClassSearchedSubs.contains(existsClassComp))) {
				RedundancyExplanationRestriction e = new RedundancyExplanationRestriction();
				e.setExplanationString("Equal Properties and "
						+ existsClassSearched + " is more general than "
						+ existsClassComp);
				return e;
			}
		}

		// Problem: ELK does not give sub properties!?
		Set<OWLObjectPropertyExpression> existsPropertySearchedSubs = existsPropertySearched
				.getSubProperties(ontology);

		if (existsClassSearched.equals(existsClassComp)) {

			if ((existsPropertySearchedSubs.contains(existsPropertyComp))) {
				RedundancyExplanationRestriction e = new RedundancyExplanationRestriction();
				e.setExplanationString("Equal classes and "
						+ existsPropertySearched + " is more general than "
						+ existsPropertyComp);
				return e;
			}
		}

		if ((existsPropertySearchedSubs.contains(existsPropertyComp))
				&& (existsClassSearchedSubs.contains(existsClassComp))) {
			RedundancyExplanationRestriction e = new RedundancyExplanationRestriction();
			e.setExplanationString(existsPropertySearched
					+ " is more general than " + existsPropertyComp
					+ " and also " + existsClassSearched
					+ " is more general than " + existsClassComp);
			return e;
		}

		return null;
	}

	public static void isConceptRedundant(OWLClass rootClass)
			throws IOException {
		ConceptType type = Util.getConceptType(rootClass);

		switch (type) {
		case FullyDefined:
			fullyDefinedSet.add(rootClass);
			break;

		case PrimitiveNonTrivial:
			primitiveNonTrivialSet.add(rootClass);
			break;

		case PrimitiveTrivial:
			primitiveTrivialSet.add(rootClass);
			break;
		}

		Set<RedundancyExplanation> explanations = getRedundantExplanations(
				new HashSet<RedundancyExplanation>(), null, null, rootClass,
				rootClass, true, -1);

		List<OWLObjectSomeValuesFrom> rolegroups = Util
				.getAllRoleGroups(rootClass);
		List<OWLObjectSomeValuesFrom> ungrouped = Util
				.getAllUngrouped(rootClass);

		if (explanations.size() == 0) {

			if ((rolegroups.size() == 0) && (ungrouped.size() == 0)) {
				onlyconceptscounter++;
			} else if (doList) {
				System.out.println("Not redundant: " + type + ": "
						+ rootClass.getAnnotationAssertionAxioms(ontology));
				System.out.println();
			}
		}

		else if (explanations.size() > 0) {

			redundantConcepts.add(rootClass);

			Set<OWLObjectSomeValuesFrom> redundantRestrictions = new HashSet<OWLObjectSomeValuesFrom>();
			Set<OWLClass> redundantClasses = new HashSet<OWLClass>();

			bufferedWriter.write(rootClass.toString());

			numberofexplanations[explanations.size()] = numberofexplanations[explanations
					.size()] + 1;

			if (explanations.size() > maxExplanations) {
				maxExplanations = explanations.size();
				System.out.println("maxExplanations " + explanations.size()
						+ ": " + rootClass);
			}

			for (RedundancyExplanation explanation : explanations) {
				distances[explanation.distance] = distances[explanation.distance] + 1;

				if (explanation.distance > maxDistance) {
					maxDistance = explanation.distance;
					System.out.println("Distance " + explanation.distance
							+ ": " + rootClass);
				}

				explanationcounter++;
				bufferedWriter.write("\t" + explanation.superClass
						+ " (Distance " + explanation.distance + " - "
						+ explanation.ruleCategory + "): ");

				if (explanation.explanationString != null) {
					bufferedWriter.write(explanation.explanationString);
				}

				if (explanation instanceof RedundancyExplanationRoleGroup) {
					bufferedWriter
							.write(((RedundancyExplanationRoleGroup) explanation)
									.getExplanations());

					redundantRestrictions
							.add(((RedundancyExplanationRoleGroup) explanation).redundantRoleGroup);
				}

				if (explanation instanceof RedundancyExplanationRestriction) {
					redundantRestrictions
							.add(((RedundancyExplanationRestriction) explanation).redundantRestriction);
				}

				if (explanation instanceof RedundancyExplanationClass) {
					redundantClasses
							.add(((RedundancyExplanationClass) explanation).redundantClass);
				}

				switch (explanation.ruleCategory) {
				case rule1:
					rule1counter++;
					break;

				case rule2:
					rule2counter++;
					break;

				case rule3:
					rule3counter++;
					break;

				case rule4:
					rule4counter++;
					break;
				}
			}

			bufferedWriter.newLine();
			bufferedWriter.flush();

			String semanticType = Util.getSemanticType(rootClass);
			if (semanticType != null) {
				typeMap.put(semanticType, typeMap.get(semanticType) + 1);
			}

			int numberredundant = redundantRestrictions.size()
					+ redundantClasses.size();
			numberofredundantelements[numberredundant] = numberofredundantelements[numberredundant] + 1;

			if (numberredundant > maxRedundantElements) {
				maxRedundantElements = numberredundant;
				System.out.println("maxRedundantElements " + numberredundant
						+ ": " + rootClass);
			}

			if (saveManipulatedOntology) {

				Set<OWLClassExpression> originalelements = new HashSet<OWLClassExpression>();
				originalelements.addAll(rolegroups);
				originalelements.addAll(ungrouped);
				originalelements.addAll(Util.getAllClasses(rootClass));

				originalelements.removeAll(redundantRestrictions);
				originalelements.removeAll(redundantClasses);

				if (originalelements.size() == 0) {
					System.out.println("Drama! RootClass: " + rootClass);
				}

				else {

					Set<OWLClassAxiom> axioms = manipulatedOntology
							.getAxioms(rootClass);

					if (axioms.size() != 1) {
						System.out.println("So many axioms: " + axioms.size());
						System.out.println(axioms.toString());
					}

					for (OWLClassAxiom removeClassAxiom : axioms) {
						RemoveAxiom removeAxiom = new RemoveAxiom(
								manipulatedOntology, removeClassAxiom);
						manipulatedManager.applyChange(removeAxiom);
					}

					OWLAxiom axiom = null;

					OWLObjectIntersectionOf intersection = manipulatedDataFactory
							.getOWLObjectIntersectionOf(originalelements);

					ConceptType cType = Util.getConceptType(rootClass);

					if (cType.equals(ConceptType.FullyDefined)) {

						if (originalelements.size() == 1) {
							axiom = manipulatedDataFactory
									.getOWLEquivalentClassesAxiom(rootClass,
											originalelements.iterator().next());
						} else {
							axiom = manipulatedDataFactory
									.getOWLEquivalentClassesAxiom(rootClass,
											intersection);
						}
						AddAxiom addAxiom = new AddAxiom(manipulatedOntology,
								axiom);
						manipulatedManager.applyChange(addAxiom);
					} else {
						if (originalelements.size() == 1) {
							axiom = manipulatedDataFactory
									.getOWLSubClassOfAxiom(rootClass,
											originalelements.iterator().next());
						} else {
							axiom = manipulatedDataFactory
									.getOWLSubClassOfAxiom(rootClass,
											intersection);
						}
					}

					AddAxiom addAxiom = new AddAxiom(manipulatedOntology, axiom);
					manipulatedManager.applyChange(addAxiom);
					// System.out.println("added axiom!");
				}
			}
		}
	}
}