package decio;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Candidate {

	private HashSet<OWLClass> classes;
	private HashSet<OWLClass> cClasses;
	
	private OWLNamedIndividual me;
	
	public Candidate(OWLNamedIndividual me) {
		classes = new HashSet<OWLClass>();
		cClasses = new HashSet<OWLClass>();
		this.me = me;
		
		Set<OWLClass> ind = OntologyHelper.getClassesOf(me);
		for (OWLClass owlClass : ind) 
			classes.add(owlClass);
		
		ind = OntologyHelper.getCClassesOf(me);
		for (OWLClass owlClass : ind) 
			cClasses.add(owlClass);
	}
	
	public String getName(){
		return OntologyHelper.getIndividualName(me);
	}
	
	public boolean belongsTo(OWLClass owlClass){
		return classes.contains(owlClass);
	}
	
	public boolean belongsNTo(OWLClass owlClass){
		return cClasses.contains(owlClass);
	}
	
	public HashSet<OWLClass> getClasses(){
		return classes;
	}
	
	public HashSet<OWLClass> getCClasses(){
		return cClasses;
	}
}
