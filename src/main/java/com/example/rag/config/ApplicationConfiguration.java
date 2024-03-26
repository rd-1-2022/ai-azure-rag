package com.example.rag.config;

import com.example.rag.spring.engine.QueryEngine;
import com.example.rag.spring.retriever.VectorStoreRetriever;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.document.DocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

	@Bean
	QueryEngine carinaQueryEngine(ChatClient chatClient, VectorStore vectorStore) {
		DocumentRetriever documentRetriever = new VectorStoreRetriever(vectorStore); // searches
																						// all
																						// documents,
																						// useful
																						// only
																						// in
																						// demos
		return new QueryEngine(chatClient, documentRetriever);
	}

}
