package com.example.rag.spring.engine;

import org.springframework.ai.chat.messages.UserMessage;

@FunctionalInterface
public interface Engine {

// Maybe extends EngineOperation<UserMessage, EngineResponse>

	default EngineResponse call(String message) {
		UserMessage userMessage = new UserMessage(message);
		return call(userMessage);

	}

	EngineResponse call(UserMessage message); // for multimodal inputs

}
