package decio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyHelper {
	
	public static OWLOntology load(String path) throws OWLOntologyCreationException{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		return manager.loadOntologyFromOntologyDocument(new File(path));
	}
	
	public static OWLClass getOWLClass(String caracteristic){
		for (OWLClass owlClass : getAllClasses(Main.OntologyPath)) {
			if(owlClass.toString().toLowerCase().contains(caracteristic))
				return owlClass;
		}
		return null;
	}
	
	public static Set<OWLNamedIndividual> getIndividualsWichBelongTo(OWLClass owlClass){
		return owlClass.getIndividualsInSignature();
	}
	
	public static Set<OWLClass> getClassesOf(OWLIndividual owlIndividual) {
		Set<OWLClassAssertionAxiom> a = Main.Ontology.getClassAssertionAxioms(owlIndividual);
		Set<OWLClass> classes = new HashSet<>();
		for (OWLClassAssertionAxiom owlClassAssertionAxiom : a) {
			
			String[] aux = owlClassAssertionAxiom.toString().split(" ");
			aux[0] = aux[0].replaceAll("ClassAssertion", "");
			aux[0] = aux[0].replaceAll("ObjectIntersectionOf", "");
			aux[0] = aux[0].substring(2);
			for (int i = 0; i < aux.length - 1; i++) {
				
				if(!aux[i].contains("Complement")){
				
					Set<OWLClass> type = owlClassAssertionAxiom.getClassesInSignature();
					for (OWLClass owlClass : type) {
						String gamb = aux[i].split("#")[1];
						gamb = gamb.substring(0, gamb.length()-1).toLowerCase();
						
						if(gamb.charAt(gamb.length()-1) == '>')
							gamb = gamb.substring(0, gamb.length()-1).toLowerCase();
						
						String x = OntologyHelper.getClassName(owlClass).toLowerCase();
						
						if(gamb.equals(OntologyHelper.getClassName(owlClass).toLowerCase())){
							classes.add(owlClass);
							break;
						}
					}
				}
			}
		}
		return classes;
	}
	
	public static Set<OWLClass> getCClassesOf(OWLIndividual owlIndividual) {
		Set<OWLClassAssertionAxiom> a = Main.Ontology.getClassAssertionAxioms(owlIndividual);
		Set<OWLClass> classes = new HashSet<>();
		for (OWLClassAssertionAxiom owlClassAssertionAxiom : a) {
			
			String[] aux = owlClassAssertionAxiom.toString().split(" ");
			aux[0] = aux[0].replaceAll("ClassAssertion", "");
			aux[0] = aux[0].replaceAll("ObjectIntersectionOf", "");
			aux[0] = aux[0].substring(2);
			for (int i = 0; i < aux.length - 1; i++) {
				
				if(aux[i].contains("Complement")){
				
					Set<OWLClass> type = owlClassAssertionAxiom.getClassesInSignature();
					for (OWLClass owlClass : type) {
						String gamb = aux[i].split("#")[1];
						gamb = gamb.substring(0, gamb.length()-1).toLowerCase();
						
						if(gamb.equals(OntologyHelper.getClassName(owlClass).toLowerCase())){
							classes.add(owlClass);
							break;
						}
					}
				}
			}
		}
		return classes;
	}
	
	public static Set<OWLNamedIndividual> getAllIndividuals(String path){
		OWLOntology ontology = null;
		try {
			ontology = load(path);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return ontology.getIndividualsInSignature();
	}
	
	public static Set<OWLClass> getAllClasses(String path){
		OWLOntology ontology = null;
		try {
			ontology = load(path);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return ontology.getClassesInSignature();
	}
	
	public static String getClassName(OWLClass owlClass){
		String name = owlClass.toString();
		String[] aux = name.split("#");
		name = aux[aux.length-1];
		return name.substring(0, name.length()-1).toLowerCase();
	}
	
	public static String getIndividualName(OWLIndividual owlIndividual){
		String name = owlIndividual.toString();
		String[] aux = name.split("#");
		name = aux[aux.length-1];
		return name.substring(0, name.length()-1);
	}
}
