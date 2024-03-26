package com.example.rag.spring.engine;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Objects;

public class EngineResponse {

	private final ChatResponse chatResponse;

	private final List<Document> documents;

	public EngineResponse(ChatResponse chatResponse, List<Document> documents) {
		this.chatResponse = chatResponse;
		this.documents = documents;
	}

	public ChatResponse getChatResponse() {
		return chatResponse;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	@Override
	public String toString() {
		return "EngineResponse{" + "chatResponse=" + chatResponse + ", documents=" + documents + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof EngineResponse that))
			return false;
		return Objects.equals(chatResponse, that.chatResponse) && Objects.equals(documents, that.documents);
	}

	@Override
	public int hashCode() {
		return Objects.hash(chatResponse, documents);
	}

}
