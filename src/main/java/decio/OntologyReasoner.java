package decio;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class OntologyReasoner {

	private Set<OWLClass> owlClasses;
	private Set<OWLNamedIndividual> individuals;
	private Set<Candidate> candidates;

	private Set<OWLClass> questions;
	private Set<OWLClass> positiveAnswers;
	private Set<OWLClass> negativeAnswers;

	public OntologyReasoner() {
		questions = owlClasses = OntologyHelper
				.getAllClasses(Main.OntologyPath);
		individuals = OntologyHelper.getAllIndividuals(Main.OntologyPath);
		
		candidates = new HashSet<Candidate>();
		
		positiveAnswers = new HashSet<OWLClass>();
		negativeAnswers = new HashSet<OWLClass>();

		for (OWLNamedIndividual owlNamedIndividual : individuals) {
			candidates.add(new Candidate(owlNamedIndividual));
		}
	}
	
	public Candidate winner(){
		return (Candidate) candidates.toArray()[0];
	}

	public void handleNew(OWLClass owlClass) {
		for (Candidate candidate : candidates) {
			if (candidate.belongsTo(owlClass)) {

			}
		}
	}
	
	public boolean haveAWinner(){
		return candidates.size() <= 1;
	}
	
	public void positiveAnswer(OWLClass owlClass){
		
		for (Candidate candidate : candidates) {
			if(!candidate.belongsTo(owlClass)){
				candidates.remove(candidate);
				positiveAnswer(owlClass);
				break;
			}
		}
		questions.remove(owlClass);
		positiveAnswers.add(owlClass);
	}

	public void negativeAnswer(OWLClass owlClass) {
		OWLReasoner r = new Reasoner.ReasonerFactory().createReasoner(Main.Ontology);

		Set<OWLClass> subClasses = r.getSubClasses(owlClass, false).getFlattened();
		for (OWLClass c : subClasses) {
			questions.remove(c);
		}
		
		for (Candidate candidate : candidates) {
			if(candidate.belongsTo(owlClass)){
				candidates.remove(candidate);
				negativeAnswer(owlClass);
				break;
			}
		}
		questions.remove(owlClass);
		if(OntologyHelper.getClassName(owlClass).equals("humano") || OntologyHelper.getClassName(owlClass).equals("animal") || OntologyHelper.getClassName(owlClass).equals("alienígena"))
			negativeAnswers.add(owlClass);
	}
	
	public OWLClass nextQuestion(){
		OWLClass q = (OWLClass) questions.toArray()[0];
		questions.remove(q);
		return q;
	}

	public Set<OWLClass> getOwlClasses() {
		return owlClasses;
	}

	public Set<OWLNamedIndividual> getIndividuals() {
		return individuals;
	}

}
