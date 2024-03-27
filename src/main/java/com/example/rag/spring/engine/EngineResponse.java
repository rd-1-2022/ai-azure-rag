package com.example.rag.spring.engine;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Objects;

public class EngineResponse {

	private final ChatResponse chatResponse;

	private final List<Document> retrievedDocuments;

	public EngineResponse(ChatResponse chatResponse, List<Document> documents) {
		this.chatResponse = chatResponse;
		this.retrievedDocuments = documents;
	}

	public ChatResponse getChatResponse() {
		return chatResponse;
	}

	public List<Document> getRetrievedDocuments() {
		return retrievedDocuments;
	}

	@Override
	public String toString() {
		return "EngineResponse{" + "chatResponse=" + chatResponse + ", documents=" + retrievedDocuments + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof EngineResponse that))
			return false;
		return Objects.equals(chatResponse, that.chatResponse) && Objects.equals(retrievedDocuments, that.retrievedDocuments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(chatResponse, retrievedDocuments);
	}

}
