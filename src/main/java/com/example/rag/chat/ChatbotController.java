package com.example.rag.chat;

import org.springframework.ai.chat.chatbot.ChatBot;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.transformer.PromptContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rag/chatbot")
public class ChatbotController {
	private ChatBot chatBot;

	public ChatbotController(ChatBot chatBot) {
		this.chatBot = chatBot;
	}
	@GetMapping
	public Map query(
			@RequestParam(value = "question", defaultValue = "What is the purpose of Carina?") String question) {

		var prompt = new Prompt(new UserMessage(question));

		var chatBotResponse  = this.chatBot.call(new PromptContext(prompt));

		Map<String, Object> response = new HashMap<>();
		response.put("question", question);
		response.put("answer", chatBotResponse.getChatResponse().getResult().getOutput().getContent());
		return response;
	}

}
