package decio;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Main {
	
	public static String OntologyPath;
	public static OWLOntology Ontology;

	public static void main(String args[]) throws OWLOntologyCreationException {

		OWLOntology ontology = OntologyHelper.load(args[0]);
		OntologyPath = args[0];
		
		Ontology = OntologyHelper.load(OntologyPath);
		
//		for (OWLClass c : OntologyHelper.getAllClasses(OntologyPath)) {
//			System.out.println(OntologyHelper.getClassName(c));
//			
//			OWLReasoner r = new Reasoner.ReasonerFactory().createReasoner(ontology);
//			
//			Set<OWLClass> subClasses = r.getSubClasses(c, false).getFlattened();
//			if(subClasses.size() > 1){
//				for (OWLClass owlClass : subClasses) {
//					System.out.println(OntologyHelper.getClassName(owlClass));
//				}
//			}
//			System.out.println("\n");
//		}
		
		new SpeakingInterface();

//		Set<OWLClass> classes = ontology.getClassesInSignature();
//
//		Set<OWLNamedIndividual> names = ontology.getIndividualsInSignature();
//
//		for (OWLNamedIndividual c : names) {
//			System.out.println(OntologyHelper.getIndividualName(c));
//			Set<OWLClassAssertionAxiom> a = ontology.getClassAssertionAxioms(c);
//			System.out.println(a.toString());
//			for (OWLClassAssertionAxiom t : a) {
//				Set<OWLClass> type = t.getClassesInSignature();
//				for (OWLClass owlClass : type) {
//					System.out.println(OntologyHelper.getClassName(owlClass));
//				}
//			}
//		}

	}
}
