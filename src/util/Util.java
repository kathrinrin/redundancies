package util;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class Util {

	public enum ConceptType {
		FullyDefined, PrimitiveNonTrivial, PrimitiveTrivial
	}

	public static OWLDataFactory df = OWLManager.getOWLDataFactory();
	static URI ontologyIRI = URI.create("http://www.ihtsdo.org/");

	static String body_structure_string = "SCT_123037004";
	static String finding_string = "SCT_404684003";
	static String environment_location_string = "SCT_308916002";
	static String event_string = "SCT_272379006";
	static String observable_entity_string = "SCT_363787002";
	static String organism_string = "SCT_410607006";
	static String product_string = "SCT_373873005";
	static String physical_force_string = "SCT_78621006";
	static String physical_object_string = "SCT_260787004";
	static String procedure_string = "SCT_71388002";
	static String qualifier_value_string = "SCT_362981000";
	static String record_artifact_string = "SCT_419891008";
	// static String metadata_string = "SCT_900000000000441003";
	static String situation_string = "SCT_243796009";
	static String social_concept_string = "SCT_48176007";
	static String special_concept_string = "SCT_370115009";
	static String specimen_string = "SCT_123038009";
	static String staging_scale_string = "SCT_254291000";
	static String substance_string = "SCT_105590001";

	static OWLClass body_structure = df.getOWLClass(IRI.create(ontologyIRI
			+ body_structure_string));
	static OWLClass finding = df.getOWLClass(IRI.create(ontologyIRI
			+ finding_string));
	static OWLClass environment_location = df.getOWLClass(IRI
			.create(ontologyIRI + environment_location_string));
	static OWLClass event = df.getOWLClass(IRI.create(ontologyIRI
			+ event_string));
	static OWLClass observable_entity = df.getOWLClass(IRI.create(ontologyIRI
			+ observable_entity_string));
	static OWLClass organism = df.getOWLClass(IRI.create(ontologyIRI
			+ organism_string));
	static OWLClass product = df.getOWLClass(IRI.create(ontologyIRI
			+ product_string));
	static OWLClass physical_force = df.getOWLClass(IRI.create(ontologyIRI
			+ physical_force_string));
	static OWLClass physical_object = df.getOWLClass(IRI.create(ontologyIRI
			+ physical_object_string));
	static OWLClass procedure = df.getOWLClass(IRI.create(ontologyIRI
			+ procedure_string));
	static OWLClass qualifier_value = df.getOWLClass(IRI.create(ontologyIRI
			+ qualifier_value_string));
	static OWLClass record_artifact = df.getOWLClass(IRI.create(ontologyIRI
			+ record_artifact_string));
	// static OWLClass metadata = df.getOWLClass(IRI.create(ontologyIRI
	// + metadata_string));
	static OWLClass situation = df.getOWLClass(IRI.create(ontologyIRI
			+ situation_string));
	static OWLClass social_concept = df.getOWLClass(IRI.create(ontologyIRI
			+ social_concept_string));
	static OWLClass special_concept = df.getOWLClass(IRI.create(ontologyIRI
			+ special_concept_string));
	static OWLClass specimen = df.getOWLClass(IRI.create(ontologyIRI
			+ specimen_string));
	static OWLClass staging_scale = df.getOWLClass(IRI.create(ontologyIRI
			+ staging_scale_string));
	static OWLClass substance = df.getOWLClass(IRI.create(ontologyIRI
			+ substance_string));

	public static OWLOntology ontology;

	static Set<OWLClass> semanticTypes;

	public static void main(String[] args) {
	}

	public static void setOntology(OWLOntology ontolgoyIn) {
		ontology = ontolgoyIn;
	}

	public static Set<OWLClass> getSemanticTypes() {

		semanticTypes = new HashSet<OWLClass>();

		semanticTypes.add(body_structure);
		semanticTypes.add(finding);
		semanticTypes.add(environment_location);
		semanticTypes.add(event);
		semanticTypes.add(observable_entity);
		semanticTypes.add(organism);
		semanticTypes.add(product);
		semanticTypes.add(physical_force);
		semanticTypes.add(physical_object);
		semanticTypes.add(procedure);
		semanticTypes.add(qualifier_value);
		semanticTypes.add(record_artifact);
		// semanticTypes.add(metadata);
		semanticTypes.add(situation);
		semanticTypes.add(social_concept);
		semanticTypes.add(special_concept);
		semanticTypes.add(specimen);
		semanticTypes.add(staging_scale);
		semanticTypes.add(substance);

		return semanticTypes;
	}

	public static ConceptType getConceptType(OWLClass owlclass) {

		Set<OWLClassExpression> fullydefinedexpressions = owlclass
				.getEquivalentClasses(ontology);

		Set<OWLClassExpression> primitiveexpressions = owlclass
				.getSuperClasses(ontology);

		// a concept should only be defined once
		if (fullydefinedexpressions.size() > 1) {
			System.out.println("Problem: more than one equivalent classes for"
					+ owlclass + " " + fullydefinedexpressions.size());
		}

		if (primitiveexpressions.size() > 1) {
			System.out
					.println("Problem: more than one superclasses classes for "
							+ owlclass + " " + primitiveexpressions.size());
		}

		if ((fullydefinedexpressions.size() == 1)
				&& (primitiveexpressions.size() == 0)) {
			return ConceptType.FullyDefined;
		}

		if ((fullydefinedexpressions.size()) == 0
				&& (primitiveexpressions.size() == 1)) {

			if (primitiveexpressions.iterator().next().isAnonymous()) {
				return ConceptType.PrimitiveNonTrivial;
			}

			else {
				return ConceptType.PrimitiveTrivial;
			}
		}

		if (!owlclass.isBottomEntity())
			System.out
					.println("Problem: concept neither fully defined nor primitive: "
							+ owlclass);

		return null;
	}

	public static double round(double unrounded, int precision, int roundingMode) {
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, roundingMode);
		return rounded.doubleValue();
	}

	public static OWLClass getNamedClassFromAxiom(OWLLogicalAxiom logicalAxiom) {

		if (logicalAxiom.getAxiomType().equals(AxiomType.EQUIVALENT_CLASSES)) {

			OWLEquivalentClassesAxiom equivalents = (OWLEquivalentClassesAxiom) logicalAxiom;

			Set<OWLClass> named = equivalents.getNamedClasses();

			if (named.size() > 1) {
				System.out
						.println("Problem: More than one named classes in OWLEquivalentClassesAxiom");
			}

			for (OWLClass namedclass : named) {
				return namedclass;
			}
		}

		if (logicalAxiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
			OWLSubClassOfAxiom subclassaxiom = (OWLSubClassOfAxiom) logicalAxiom;
			OWLClass named = (OWLClass) subclassaxiom.getSubClass();
			return named;
		}

		System.out.println("Problem: no named class found");
		return null;
	}

	public static OWLClassExpression getClassExpressionFromAxiom(
			OWLLogicalAxiom logicalAxiom, OWLOntology ontology) {

		if (logicalAxiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {

			OWLClass owlsubclass = Util.getNamedClassFromAxiom(logicalAxiom);

			Set<OWLClassExpression> classExpressions = owlsubclass
					.getSuperClasses(ontology);

			if (classExpressions.size() > 1) {
				System.out.println("Problem: classExpressions size > 1");
			}

			for (OWLClassExpression classExpression : classExpressions) {

				if ((classExpression.getClassExpressionType()
						.equals(ClassExpressionType.OBJECT_INTERSECTION_OF))
						|| (classExpression.getClassExpressionType()
								.equals(ClassExpressionType.OWL_CLASS))) {
					return classExpression;
				}

				else {
					System.out.println("Problem: "
							+ classExpression.getClassExpressionType() + " "
							+ classExpression.toString());
				}
			}
		}

		if (logicalAxiom.getAxiomType().equals(AxiomType.EQUIVALENT_CLASSES)) {

			OWLClass namedClass = Util.getNamedClassFromAxiom(logicalAxiom);

			Set<OWLClassExpression> classExpressions = namedClass
					.getEquivalentClasses(ontology);

			if (classExpressions.size() > 1) {
				System.out.println("Problem: classExpressions size > 1");
			}

			for (OWLClassExpression classExpression : classExpressions) {
				if (classExpression.getClassExpressionType().equals(
						ClassExpressionType.OBJECT_INTERSECTION_OF)) {
					return classExpression;
				}

				else {
					System.out.println("Problem: "
							+ classExpression.getClassExpressionType() + " "
							+ classExpression.toString());
				}
			}
		}

		System.out.println("Problem: no class expression found");
		return null;
	}

	public static String getSemanticType(OWLClass owlClass) {

		String annotations = owlClass.getAnnotationAssertionAxioms(ontology)
				.toString();

		String typeString = annotations.substring(
				annotations.lastIndexOf(" (") + 2,
				(annotations.lastIndexOf(")\"")));

		if (typeString.equals("disorder")
				|| typeString.equals("morphologic abnormality")) {
			typeString = "finding";
		}

		if (typeString.equals("regime/therapy")) {
			typeString = "procedure";
		}

		if (typeString.equals("occupation")) {
			typeString = "social concept";
		}

		if (typeString.equals("cell") || typeString.equals("cell structure")) {
			typeString = "body structure";
		}

		if (typeString.equals("geographic location")) {
			typeString = "environment / location";
		}

		if (typeString.equals("ethnic group")) {
			typeString = "observable entity";
		}

		if (typeString.equals("environment")) {
			typeString = "environment / location";
		}

		if (typeString.equals("person")) {
			typeString = "social concept";
		}

		return typeString;
	}

	public static OWLClassExpression getOWLClassExpressionFromClass(
			OWLClass currentClass) {

		Set<OWLClassExpression> superclassexpressions = currentClass
				.getSuperClasses(ontology);

		if (superclassexpressions.size() == 1) {

			for (OWLClassExpression superclassexpression : superclassexpressions) {
				return superclassexpression;
			}
		}

		Set<OWLClassExpression> equivalentexpressions = currentClass
				.getEquivalentClasses(ontology);

		if (equivalentexpressions.size() == 1) {

			for (OWLClassExpression equivalentexpression : equivalentexpressions) {
				return equivalentexpression;
			}
		}
		System.out.println("Problem: no class expression found");
		return null;
	}

	public static List<OWLObjectSomeValuesFrom> getExistRestrictionsFromRoleGroup(
			OWLObjectSomeValuesFrom some) {

		List<OWLObjectSomeValuesFrom> existRestrictions = new ArrayList<OWLObjectSomeValuesFrom>();

		OWLClassExpression fillerexpression = some.getFiller();

		if (fillerexpression.getClassExpressionType().equals(
				ClassExpressionType.OBJECT_INTERSECTION_OF)) {

			OWLObjectIntersectionOf inter = (OWLObjectIntersectionOf) fillerexpression;

			Set<OWLClassExpression> elements = inter.getOperands();

			for (OWLClassExpression element : elements) {
				if (element.getClassExpressionType().equals(
						ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {

					existRestrictions.add((OWLObjectSomeValuesFrom) element);

				}
			}
		}

		else if (fillerexpression.getClassExpressionType().equals(
				ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
			existRestrictions.add((OWLObjectSomeValuesFrom) fillerexpression);
		}

		return existRestrictions;
	}

	public static List<OWLObjectSomeValuesFrom> getAllRoleGroups(
			OWLClass owlClass) {
		List<OWLObjectSomeValuesFrom> roleGroups = new LinkedList<OWLObjectSomeValuesFrom>();

		OWLClassExpression superClassExpression = Util
				.getOWLClassExpressionFromClass(owlClass);

		if (superClassExpression.getClassExpressionType().equals(
				ClassExpressionType.OBJECT_INTERSECTION_OF)) {

			OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) superClassExpression;

			Set<OWLClassExpression> operands = intersection.getOperands();
			for (OWLClassExpression operand : operands) {

				if (operand.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {

					OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) operand;
					OWLObjectPropertyExpression property = some.getProperty();

					if (property.toString().equals(
							"<http://www.ihtsdo.org/RoleGroup>")) {

						roleGroups.add(some);
					}
				}
			}

		}
		return roleGroups;
	}

	public static List<OWLObjectSomeValuesFrom> getAllUngrouped(
			OWLClass owlClass) {
		List<OWLObjectSomeValuesFrom> ungrouped = new LinkedList<OWLObjectSomeValuesFrom>();

		OWLClassExpression superClassExpression = Util
				.getOWLClassExpressionFromClass(owlClass);

		if (superClassExpression.getClassExpressionType().equals(
				ClassExpressionType.OBJECT_INTERSECTION_OF)) {

			OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) superClassExpression;

			Set<OWLClassExpression> operands = intersection.getOperands();
			for (OWLClassExpression operand : operands) {

				if (operand.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {

					OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) operand;
					OWLObjectPropertyExpression property = some.getProperty();

					if (!property.toString().equals(
							"<http://www.ihtsdo.org/RoleGroup>")) {

						ungrouped.add(some);
					}
				}
			}
		}
		return ungrouped;
	}

	public static List<OWLObjectSomeValuesFrom> getAllExistentialRestrictions(
			OWLClass owlClass) {
		List<OWLObjectSomeValuesFrom> existentialRestrictions = new LinkedList<OWLObjectSomeValuesFrom>();

		OWLClassExpression superClassExpression = Util
				.getOWLClassExpressionFromClass(owlClass);

		if (superClassExpression.getClassExpressionType().equals(
				ClassExpressionType.OBJECT_INTERSECTION_OF)) {

			OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) superClassExpression;

			Set<OWLClassExpression> operands = intersection.getOperands();
			for (OWLClassExpression operand : operands) {

				if (operand.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {

					OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) operand;
					existentialRestrictions.add(some);
				}
			}
		}

		java.util.Collections.sort(existentialRestrictions);
		return existentialRestrictions;
	}

	public static List<OWLClass> getAllClasses(OWLClass owlClass) {
		List<OWLClass> classes = new ArrayList<OWLClass>();

		OWLClassExpression superClassExpression = getOWLClassExpressionFromClass(owlClass);

		if (superClassExpression.getClassExpressionType().equals(
				ClassExpressionType.OBJECT_INTERSECTION_OF)) {

			OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) superClassExpression;

			Set<OWLClassExpression> operands = intersection.getOperands();

			for (OWLClassExpression operand : operands) {
				if (operand.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
					OWLClass superclass = (OWLClass) operand;
					classes.add(superclass);
				}
			}
		}

		else if (superClassExpression.getClassExpressionType().equals(
				ClassExpressionType.OWL_CLASS)) {

			OWLClass element = (OWLClass) superClassExpression;
			classes.add(element);
		}

		return classes;
	}
}
