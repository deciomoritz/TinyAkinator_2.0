package decio;

import java.util.Scanner;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class SpeakingInterface {

	private OntologyReasoner reasoner;

	private String name = "";
	private String characteristic = "";

	private static int MAX_ATTEMPTS = 3;

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

		tryToGuess();
		learn();
	}
	
	private void learn(){
		reasoner.learn(name, characteristic);
	}

	private void tryToGuess() {
		int num_attempts = 0;
		reasoner.popWinner();
		while (reasoner.winner() != null && MAX_ATTEMPTS <= num_attempts) {
			say("Eu acho que é: " + reasoner.winner().getName());
			say("Acertei?");

			if (hear().equals("sim")) {
				say("Que chato, sempre ganho. Quer jogar de novo?");
				if (hear().equals("sim"))
					startConversation();
				else
					say("Tchau!");
			} 
			reasoner.popWinner();
			num_attempts++;
		}
		say("Agora você me complicou, não sei mesmo.");
		say("Hmm... Qual o nome do seu personagem?");
		name = hear();
		say("E qual sua característica marcante?");
		characteristic = hear();
	}

	private void handleFirstQuestion(String s) {

		OWLClass owlClass = OntologyHelper.getOWLClass(s);

		if (owlClass == null)
			fail();

		if (owlClass.toString().toLowerCase().contains("animal")) {
			reasoner.positiveAnswer(OntologyHelper.getOWLClass("animal"));
		} else if (owlClass.toString().toLowerCase().contains("humano")) {
			reasoner.positiveAnswer(OntologyHelper.getOWLClass("humano"));
		} else if (owlClass.toString().toLowerCase().contains("alienígena")) {
			reasoner.positiveAnswer(OntologyHelper.getOWLClass("alienígena"));
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

	@SuppressWarnings("resource")
	public String hear() {
		return new Scanner(System.in).nextLine();
	}

}
