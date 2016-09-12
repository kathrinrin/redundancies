Note: All programs require the OWL version of SNOMED CT (res_StatedOWLF_INT_20120731.owlf) in the ontologies folder. 
This version can be generated with the script that comes with the official SNOMED release. 


The classes to reproduce the experiments described in the paper "Intra-Axiom Redundancies in SNOMED CT" are: 



**redundancy.RedundancyChecker**

Runtime: several minutes for the list of concepts that must be redundant, ca. 6 hours for all concepts regarding supertypes, and ca. 12 hours with exhaustive search. 
Input: ontologies/res_StatedOWLF_INT_20120731.owlf; results/EquivalentSubconceptsAll.txt (for doList) 
Output: results/RedundantConceptsDisregarding.txt, results/RedundantConceptsRegarding.txt or RedundantConceptsRegardingExhaustive, depending on doSuperClasses and exhaustiveSearch

Checks for concepts whether they are redundant. 
Can be run to check all concepts (doAll), a list (doList) or a single concept only (doConcept).
It can be run regarding or disregarding supertypes (doSuperClasses). 
Returns either only the first explanation per redundancy, or all explanations. 



**util.Util**

Input: -
Output: -

The Util class contains useful methods. 



**CornetsEquivalenceMethod**

Runtime: several minutes, ca. 2 hours with Pellet
Input: ontologies/res_StatedOWLF_INT_20120731.owlf
Output: results/ClassesWithEquivalentClass.txt; results/Explanations.txt

Generates a list of all pairs of equivalent classes 
Can be run with Pellet to generate explanations for non-equivalent files (needs > 8 GB heap space; explanations = true) 



**ValidateRedundantConceptsCornet**

Runtime: several minutes
Input: ontologies/res_StatedOWLF_INT_20120731.owlf; results/RedundantConceptsRegarding.txt
Output: results/EquivalentSubconceptsAll.txt

Generates a list (EquivalentSubconceptsAll.txt) of concepts that must be redundantly defined according to Cornet's method. 
This list can be taken as input for the RedundancyChecker (doList) 



**Final notes**

- All classes should be run with sufficient heap space (-Xmx2G) 
- Optimizations to all programs would be welcome 
