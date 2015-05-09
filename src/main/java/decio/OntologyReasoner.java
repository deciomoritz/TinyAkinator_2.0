package decio;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
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

	public Candidate winner() {
		if(candidates.toArray().length > 0)
			return (Candidate) candidates.toArray()[0];
		return null;
	}

	public boolean haveAWinner() {
		return candidates.size() <= 1 || questions.size() == 0;
	}

	public void positiveAnswer(OWLClass owlClass) {

		eliminateDisjoints(owlClass);
		for (Candidate candidate : candidates) {
			if (!candidate.belongsTo(owlClass)) {
				candidates.remove(candidate);
				positiveAnswer(owlClass);
				break;
			}
		}
		questions.remove(owlClass);
		positiveAnswers.add(owlClass);
	}

	public void eliminateDisjoints(OWLClass owlClass) {
		Set<OWLClass> disjointClasses = OntologyHelper
				.getDisjointClassesOf(owlClass);
		for (Candidate candidate : candidates) {
			for (OWLClass owlClass2 : disjointClasses) {
				if (candidate.belongsTo(owlClass2)) {
					candidates.remove(candidate);
					eliminateDisjoints(owlClass);
					return;
				}
				questions.remove(owlClass2);
			}
		}
	}

	public void negativeAnswer(OWLClass owlClass) {
		OWLReasoner r = new Reasoner.ReasonerFactory()
				.createReasoner(Main.Ontology);

		Set<OWLClass> subClasses = r.getSubClasses(owlClass, false)
				.getFlattened();
		for (OWLClass c : subClasses) {
			questions.remove(c);
		}

		for (Candidate candidate : candidates) {
			if (candidate.belongsTo(owlClass)) {
				candidates.remove(candidate);
				negativeAnswer(owlClass);
				break;
			}
		}
		questions.remove(owlClass);
		negativeAnswers.add(owlClass);
	}

	public OWLClass nextQuestion() {
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

	public void popWinner() {
		candidates.remove(winner());
	}

	public void learn(String name, String characteristic) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		IRI ontologyIRI = Main.Ontology.getOntologyID().getOntologyIRI();
		OWLOntology ont = Main.Ontology;
		OWLDataFactory factory = manager.getOWLDataFactory();

		OWLIndividual newIndividual = factory.getOWLNamedIndividual(IRI
				.create(ontologyIRI + "#" + name));

		OWLObjectProperty assertion = factory.getOWLObjectProperty(IRI
				.create(ontologyIRI + "#" + characteristic));

		OWLObjectPropertyAssertionAxiom axiom1 = factory
				.getOWLObjectPropertyAssertionAxiom(assertion, newIndividual, newIndividual);

		AddAxiom addAxiom1 = new AddAxiom(ont, axiom1);
		// Now we apply the change using the manager.
		manager.applyChange(addAxiom1);

		System.out.println("RDF/XML: ");
		try {
			manager.saveOntology(ont, new StreamDocumentTarget(System.out));
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

}
