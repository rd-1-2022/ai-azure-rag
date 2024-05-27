package com.example.rag.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rag/chat")
class ChatController {
	private final ChatClient chatClient;

	public ChatController(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}
	@GetMapping
	Map query(@RequestParam(value = "question", defaultValue = "What is the purpose of Carina?") String question) {

		String answer = this.chatClient.prompt()
				.user(question)
				.call()
				.content();
		return Map.of("question", question, "answer", answer);
	}

}
