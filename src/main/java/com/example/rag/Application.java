package com.example.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@SpringBootApplication
public class Application  {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner(SpringAI springAI, Hollywood hollywood) {
		return args -> {
			System.out.println(springAI.tellJoke("Robert DeNiro"));

			//System.out.println(hollywood.filmography());

			//System.out.println(hollywood.films());
		};
	}

	@Service
	public class SpringAI {

		private ChatClient chatClient;

		public SpringAI(ChatClient.Builder builder) {
			this.chatClient = builder
					.defaultSystem("You are a friendly assistant that answers question in the voice of {voice}.")
					.build();
		}

		public String tellJoke(String voice) {
			return chatClient.prompt()
					.user("Tell me a joke")
					.system(sp -> sp.param("voice", voice))
					.call()
					.content();
		}
	}

	record ActorFilms(String actor, List<String> films) {}

	@Service
	public class Hollywood {
		private ChatClient chatClient;

		public Hollywood(ChatClient.Builder builder) {
			this.chatClient = builder.build();
		}

		ActorFilms filmography() {
			return chatClient.prompt()
					.user("Generate the filmography for Tom Hanks")
					.call()
					.entity(ActorFilms.class);

		}

		List<ActorFilms> films() {
			return chatClient.prompt()
					.user("Generate the filmography for Tom Hanks and Robert DeNiro.  Maximum 10 films")
					.call()
					.entity(new ParameterizedTypeReference<List<ActorFilms>>() {});
		}
	}

}
