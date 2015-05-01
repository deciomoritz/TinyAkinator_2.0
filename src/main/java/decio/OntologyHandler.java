package decio;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyHandler {

	public static OWLOntology load(String path) throws OWLOntologyCreationException{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		return manager.loadOntologyFromOntologyDocument(new File(path));
	}

	public static void printClasses(OWLOntology ontology){
		Set<OWLNamedIndividual> names = ontology.getIndividualsInSignature();
		for (OWLNamedIndividual n : names) {
			System.out.println(n.toString());
		}
	}
	
	public static String getClassName(OWLClass owlClass){
		String name = owlClass.toString();
		String[] aux = name.split("#");
		
		name = aux[aux.length-1];
		name = name.substring(0, name.length()-1);
		return name.toLowerCase();
	}
	
	public static String getIndividualName(OWLIndividual owlIndividual){
		String name = owlIndividual.toString();
		String[] aux = name.split("#");
		
		name = aux[aux.length-1];
		return name.substring(0, name.length()-1);
	}
}
