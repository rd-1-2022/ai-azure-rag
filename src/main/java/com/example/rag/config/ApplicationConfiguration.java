package com.example.rag.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.chatbot.ChatBot;
import org.springframework.ai.chat.chatbot.DefaultChatBot;
import org.springframework.ai.chat.prompt.transformer.QuestionContextAugmentor;
import org.springframework.ai.chat.prompt.transformer.VectorStoreRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApplicationConfiguration {
	@Bean
	public ChatBot chatBot(ChatClient chatClient, VectorStore vectorStore) {
		SearchRequest searchRequest = SearchRequest.defaults()
				.withFilterExpression("filename == 'medicaid-wa-faqs.pdf'");

		return DefaultChatBot.builder(chatClient)
				.withRetrievers(List.of(new VectorStoreRetriever(vectorStore, searchRequest)))
				.withAugmentors(List.of(new QuestionContextAugmentor()))
				.build();

	}
}
