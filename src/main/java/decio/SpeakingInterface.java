package decio;

import java.util.Scanner;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class SpeakingInterface {

	OntologyReasoner reasoner;

	public SpeakingInterface() throws OWLOntologyCreationException {
		startConversation();
	}

	public void startConversation() {
		reasoner = new OntologyReasoner();

		say("Pense num personagem da Looney Tunes e eu vou tentar adivinhar.");
		say("O seu personagem é um animal, humano ou alienígena?");

		handleFirstQuestion(hear());

		String answer;
		while (!reasoner.haveAWinner()) {
			OWLClass nextQuestion = reasoner.nextQuestion();

			say("O seu personagem é "
					+ OntologyHelper.getClassName(nextQuestion) + "?");

			answer = hear().toLowerCase();

			switch (answer) {
			case "não":
				reasoner.negativeAnswer(nextQuestion);
				break;
			case "sim":
				reasoner.positiveAnswer(nextQuestion);
				break;
			case "talvez":
				break;
			case "não sei":
				break;
			default:
				fail();
				break;
			}
		}
		say("Eu acho que é: " + reasoner.winner().getName());
		say("Acertei?");

		String name = "";
		String caracteristic = "";
		if (hear().equals("sim"))
			say("Que chato, sempre ganho.");
		else {
			say("Hmm... Qual o nome do seu personagem?");
			name = hear();
			say("E qual sua característica marcante?");
			caracteristic = hear();
		}
	}

	private void handleFirstQuestion(String s) {

		OWLClass owlClass = OntologyHelper.getOWLClass(s);

		if (owlClass == null)
			fail();

		if (owlClass.toString().toLowerCase().contains("animal")) {
			reasoner.positiveAnswer(OntologyHelper.getOWLClass("animal"));
			reasoner.negativeAnswer(OntologyHelper.getOWLClass("humano"));
			reasoner.negativeAnswer(OntologyHelper.getOWLClass("alienígena"));
		} else if (owlClass.toString().toLowerCase().contains("humano")) {
			reasoner.positiveAnswer(OntologyHelper.getOWLClass("humano"));
			reasoner.negativeAnswer(OntologyHelper.getOWLClass("animal"));
			reasoner.negativeAnswer(OntologyHelper.getOWLClass("alienígena"));
		} else if (owlClass.toString().toLowerCase().contains("alienígena")) {
			reasoner.positiveAnswer(OntologyHelper.getOWLClass("alienígena"));
			reasoner.negativeAnswer(OntologyHelper.getOWLClass("humano"));
			reasoner.negativeAnswer(OntologyHelper.getOWLClass("animal"));
		} else {
			fail();
		}
	}

	public void fail() {
		say("Usuário, não complique a minha vida. Tenta de novo aí.");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		startConversation();
	}

	public void say(String s) {
		System.out.println(s);
	}

	public String hear() {
		Scanner s = new Scanner(System.in);
		return s.nextLine();
	}

}
