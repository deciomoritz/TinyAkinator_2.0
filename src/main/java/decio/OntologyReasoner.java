package decio;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
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
		if (candidates.toArray().length > 0)
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
		char c[] = name.trim().toCharArray();
		c[0] = Character.toUpperCase(c[0]);
		name = new String(c);
		
		c = characteristic.trim().toCharArray();
		c[0] = Character.toUpperCase(c[0]);
		characteristic = new String(c);
		
		IRI ontologyIRI = Main.Ontology.getOntologyID().getOntologyIRI();
		OWLOntology ont = Main.Ontology;
		OWLDataFactory factory = OntologyHelper.manager.getOWLDataFactory();

		OWLNamedIndividual newIndividual = factory.getOWLNamedIndividual(IRI
				.create(ontologyIRI + "#" + name));

		OWLClass owlClass = factory.getOWLClass(IRI.create(ontologyIRI + "#"
				+ characteristic));
		
		OWLAxiom axiom1 = factory.getOWLDeclarationAxiom(owlClass);

		OWLAxiom axiom = factory.getOWLClassAssertionAxiom(owlClass, newIndividual);

		OntologyHelper.manager.addAxiom(ont, axiom1);
		OntologyHelper.manager.addAxiom(ont, axiom);
		
		for (OWLClass positiveAnswer : positiveAnswers) {
			OWLAxiom axi = factory.getOWLClassAssertionAxiom(positiveAnswer, newIndividual);
			OntologyHelper.manager.addAxiom(ont, axi);
		}
		
		for (OWLClass negativeAnswer : negativeAnswers) {
			OWLClassExpression exp = negativeAnswer.getObjectComplementOf();
			OWLAxiom axi = factory.getOWLClassAssertionAxiom(exp, newIndividual);
			OntologyHelper.manager.addAxiom(ont, axi);
		}
		
		try {
			OntologyHelper.manager.saveOntology(ont);
		} catch (OWLOntologyStorageException e) {
			System.out.println("Não consegui salvar a ontologia!");
			e.printStackTrace();
		}
		File file = new File(Main.OntologyPath);
		
		OWLOntologyFormat format = OntologyHelper.manager.getOntologyFormat(ont);
		OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
		if (format.isPrefixOWLOntologyFormat()) { 
		  owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat()); 
		}
		try {
			OntologyHelper.manager.saveOntology(ont, owlxmlFormat, IRI.create(file.toURI()));
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

}
