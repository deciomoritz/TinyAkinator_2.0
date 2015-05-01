package decio;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Main {

	public static void main(String args[]) throws OWLOntologyCreationException {

		OWLOntology ontology = OntologyHandler.load(args[0]);
		Set<OWLClass> classes = ontology.getClassesInSignature();

		Set<OWLNamedIndividual> names = ontology.getIndividualsInSignature();

		for (OWLNamedIndividual c : names) {
			System.out.println(OntologyHandler.getIndividualName(c));
			Set<OWLClassAssertionAxiom> a = ontology.getClassAssertionAxioms(c);
			for (OWLClassAssertionAxiom t : a) {
				Set<OWLClass> type = t.getClassesInSignature();
				for (OWLClass owlClass : type) {
					System.out.println(OntologyHandler.getClassName(owlClass));
				}
			}
		}
	}
}
