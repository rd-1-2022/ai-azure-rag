package com.example.rag.spring.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentRetriever;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class QueryEngine implements Engine {

	private static final Logger logger = LoggerFactory.getLogger(QueryEngine.class);

	private final ChatClient chatClient;

	private final DocumentRetriever documentRetriever;

	private SystemPromptTemplate systemPromptTemplate;

	private PromptTemplate promptTemplate;

	// Note, the best practice seems to be to put the retrieved documents into the system
	// context.
	// This is the default user message is there isn't a system prompt provided.
	private static final String DEFAULT_USER_PROMPT_TEXT = """
			   "Context information is below.\\n"
			   "---------------------\\n"
			   "{context}\\n"
			   "---------------------\\n"
			   "Given the context information and not prior knowledge, "
			   "answer the question. If the answer is not in the context, inform "
			   "the user that you can't answer the question.\\n"
			   "Question: {question}\\n"
			   "Answer: "
			""";

	public QueryEngine(ChatClient chatClient, DocumentRetriever documentRetriever) {
		this(chatClient, documentRetriever, new PromptTemplate(DEFAULT_USER_PROMPT_TEXT), null);
	}

	public QueryEngine(ChatClient chatClient, DocumentRetriever documentRetriever, PromptTemplate promptTemplate,
			SystemPromptTemplate systemPromptTemplate) {
		Objects.requireNonNull(chatClient, "ChatClient must not be null");
		Objects.requireNonNull(documentRetriever, "DocumentRetriever must not be null");
		Objects.requireNonNull(promptTemplate, "PromptTemplate must not be null");
		this.chatClient = chatClient;
		this.documentRetriever = documentRetriever;
		this.promptTemplate = promptTemplate;
		this.systemPromptTemplate = systemPromptTemplate;
	}

	@Override
	public EngineResponse call(String question) {
		List<Document> similarDocuments = this.documentRetriever.retrieve(question);
		String context = doCreateContext(similarDocuments);
		Map<String, Object> contextMap = doCreateContextMap(question, context);
		Prompt prompt = createPrompt(contextMap);
		ChatResponse chatResponse = chatClient.call(prompt);
		return new EngineResponse(chatResponse, similarDocuments);
	}

	protected Prompt createPrompt(Map<String, Object> contextMap) {
		Message userMessage = createUserMessage(contextMap);
		Prompt prompt;
		if (this.systemPromptTemplate != null) {
			Message systemMessage = createSystemMessage(contextMap);
			prompt = new Prompt(List.of(systemMessage, userMessage));
		}
		else {
			prompt = new Prompt(userMessage);
		}
		return prompt;
	}

	protected Message createSystemMessage(Map<String, Object> contextMap) {
		Message systemMessage = systemPromptTemplate.createMessage(contextMap);
		return systemMessage;
	}

	private Message createUserMessage(Map<String, Object> contextMap) {
		Message userMessage = promptTemplate.createMessage(contextMap);
		return userMessage;
	}

	protected Map<String, Object> doCreateContextMap(String question, String context) {
		Map<String, Object> contextMap = Map.of("context", context, "question", question);
		return contextMap;
	}

	protected String doCreateContext(List<Document> similarDocuments) {
		return similarDocuments.stream().map(entry -> entry.getContent()).collect(Collectors.joining("\n"));
	}

	@Override
	public EngineResponse call(UserMessage userMessage) {
		// TODO how to handle multimodal requests?
		return call(userMessage.getContent());
	}

}