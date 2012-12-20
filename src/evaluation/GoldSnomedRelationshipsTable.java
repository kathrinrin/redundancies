package evaluation;

import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import util.Util;

public class GoldSnomedRelationshipsTable {

	public static void main(String args[]) {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		File output = new File("ontologies/closure_snomed_nonredundant.owlf");
		// File output = new File(
		// "ontologies/closure_res_StatedOWLF_INT_20120731.owlf");

		int counter = 0;
		int containedcounter = 0;
		int notcontainedcounter = 0;
		int conceptmodelattributecounter = 0;

		int searchedcounter = 0;
		int foundcounter = 0;
		int notfoundcounter = 0;
		int subclassaxioms = 0;

		try {

			System.out.println("Load OWL file");
			OWLOntology ontology = manager
					.loadOntologyFromOntologyDocument(output.getAbsoluteFile());
			Util.setOntology(ontology);
			System.out.println("OWL file loaded");

			IRI startIRI = IRI.create("http://www.ihtsdo.org/SCT_");

			Statement stmt;
			Statement state;
			ResultSet rs;
			ResultSet res;

			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/snomed_july_2012";
			Connection con = DriverManager.getConnection(url, "root", "R00T");

			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			state = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			System.out.println("Compare computed closure against database");
			System.out.println();

			System.out.println("logicalAxioms: "
					+ ontology.getLogicalAxioms().size());
			Set<OWLLogicalAxiom> logicalAxioms = ontology.getLogicalAxioms();

			for (OWLLogicalAxiom logicalAxiom : logicalAxioms) {

				if (logicalAxiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {

					subclassaxioms++;

					OWLSubClassOfAxiom subclassAxiom = (OWLSubClassOfAxiom) logicalAxiom;
					OWLClass subclass = Util
							.getNamedClassFromAxiom(subclassAxiom);

					Set<OWLClass> superclasses = new HashSet<OWLClass>();

					OWLClassExpression superClassExpression = subclassAxiom
							.getSuperClass();

					// if (superClassExpression.getClassExpressionType().equals(
					// ClassExpressionType.OBJECT_INTERSECTION_OF)) {
					//
					// OWLObjectIntersectionOf intersection =
					// (OWLObjectIntersectionOf) superClassExpression;
					//
					// Set<OWLClassExpression> operands = intersection
					// .getOperands();
					//
					// for (OWLClassExpression operand : operands) {
					// if (operand.getClassExpressionType() ==
					// ClassExpressionType.OWL_CLASS) {
					// OWLClass superclass = (OWLClass) operand;
					// superclasses.add(superclass);
					// }
					// }
					// }

					if (superClassExpression.getClassExpressionType().equals(
							ClassExpressionType.OWL_CLASS)) {
						OWLClass element = (OWLClass) superClassExpression;
						superclasses.add(element);
					}

					for (OWLClass superclass : superclasses) {

						System.out.print(".");

						String subclassString = subclass.toString().substring(
								subclass.toString().indexOf("_") + 1,
								subclass.toString().length() - 1);

						String superclassString = superclass.toString()
								.substring(
										superclass.toString().indexOf("_") + 1,
										superclass.toString().length() - 1);

						if (!subclassString.contains("Thin")
								&& !superclassString.contains("Thin")) {

							searchedcounter++;
							rs = stmt
									.executeQuery("SELECT count(*) FROM sct2_relationship WHERE sourceId = "
											+ subclassString
											+ " AND destinationId = "
											+ superclassString
											+ " AND typeId = 116680003");

							while (rs.next()) {
								int count = rs.getInt("count(*)");

								if (count == 0) {

									notfoundcounter++;
									System.out.println("Not found: ");
									System.out.println("subclassAxiom: "
											+ subclassAxiom);

									System.out.println(subclassString);
									System.out.println(superclassString);
									System.out.println();
								}

								else {
									foundcounter++;
								}
							}
						}
					}
				}
			}

			System.out.println("subclassaxioms: " + subclassaxioms);
			System.out.println();
			System.out.println("searchedcounter: " + searchedcounter);
			System.out.println("foundcounter: " + foundcounter);
			System.out.println("notfoundcounter: " + notfoundcounter);

			System.out.println();
			System.out.println();

			System.out.println("Compare database against computed closure");
			System.out.println();

			rs = stmt
					.executeQuery("SELECT * from sct2_relationship LEFT JOIN sct2_concept AS concept1 ON sct2_relationship.sourceId = concept1.id LEFT JOIN sct2_concept AS concept2 ON sct2_relationship.destinationId = concept2.id WHERE typeId = 116680003 AND sct2_relationship.active = 1 AND concept1.active = 1 and concept1.moduleId = 900000000000207008 AND concept2. active = 1 and concept2.moduleId = 900000000000207008 ORDER by destinationId");

			while (rs.next()) {
				counter++;

				String CONCEPTID1 = rs.getString("sourceId");
				String CONCEPTID2 = rs.getString("destinationId");

				OWLDataFactory factory = manager.getOWLDataFactory();

				boolean found = false;

				if (ontology.containsClassInSignature(IRI.create(startIRI
						+ CONCEPTID1))) {

					OWLClass cls1 = factory.getOWLClass(IRI.create(startIRI
							+ CONCEPTID1));
					OWLClass cls2 = factory.getOWLClass(IRI.create(startIRI
							+ CONCEPTID2));

					Set<OWLClassExpression> superClasses = cls1
							.getSuperClasses(ontology);
					for (OWLClassExpression desc : superClasses) {
						if (desc.equals(cls2)) {
							found = true;
						} else {
							Set<OWLClass> classes = desc
									.getClassesInSignature();
							for (OWLClass cl : classes) {
								if (cl.equals(cls2)) {
									found = true;
								}
							}
						}
					}

					Set<OWLClassExpression> equivalentClasses = cls1
							.getEquivalentClasses(ontology);
					for (OWLClassExpression desc : equivalentClasses) {
						if (desc.equals(cls2)) {
							found = true;
						} else {
							Set<OWLClass> classes = desc
									.getClassesInSignature();
							for (OWLClass cl : classes) {
								if (cl.equals(cls2)) {
									found = true;
								}
							}
						}
					}
				}

				else if (ontology.containsObjectPropertyInSignature(IRI
						.create(startIRI + CONCEPTID1))) {

					OWLObjectProperty op1 = factory.getOWLObjectProperty(IRI
							.create(startIRI + CONCEPTID1));
					OWLObjectProperty op2 = factory.getOWLObjectProperty(IRI
							.create(startIRI + CONCEPTID2));

					Set<OWLObjectPropertyExpression> superProperties = op1
							.getSuperProperties(ontology);
					for (OWLObjectPropertyExpression desc : superProperties) {
						if (desc.equals(op2)) {
							found = true;
						} else {
							Set<OWLObjectProperty> properties = desc
									.getObjectPropertiesInSignature();
							for (OWLObjectProperty cl : properties) {
								if (cl.equals(op2)) {
									found = true;
								}
							}
						}
					}
				}

				if (found) {
					containedcounter++;
				} else {
					if (!CONCEPTID2.equals("410662002")) {
						notcontainedcounter++;

						String term1 = "";
						String term2 = "";

						res = state
								.executeQuery("SELECT Term from sct2_description WHERE conceptId = "
										+ CONCEPTID1);

						while (res.next()) {
							term1 = res.getString("Term");
						}

						res = state
								.executeQuery("SELECT Term from sct2_description WHERE conceptId = "
										+ CONCEPTID2);

						while (res.next()) {
							term2 = res.getString("Term");
						}

						System.out.println(CONCEPTID1 + " (" + term1
								+ ") SubClassOf " + CONCEPTID2 + " (" + term2
								+ ")");

					} else {
						conceptmodelattributecounter++;
					}
				}

			}// end while loop
			con.close();
			System.out.println("counter: " + counter);
			System.out.println("containedcounter: " + containedcounter);
			System.out.println("notcontainedcounter: " + notcontainedcounter);
			System.out.println("conceptmodelattributecounter: "
					+ conceptmodelattributecounter);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}