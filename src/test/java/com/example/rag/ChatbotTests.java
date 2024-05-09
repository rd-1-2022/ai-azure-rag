package com.example.rag;

import com.example.rag.data.DataController;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.chatbot.ChatBot;
import org.springframework.ai.chat.chatbot.ChatBotResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.transformer.PromptContext;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ChatbotTests {


    @Autowired
    private ChatBot chatBot;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private DataController dataController;

    @Test
    void testEvaluation() {

        dataController.delete();
        dataController.load();

        var prompt = new Prompt(new UserMessage("What is the purpose of Carina?"));
        ChatBotResponse chatBotResponse = chatBot.call(new PromptContext(prompt));

        var relevancyEvaluator = new RelevancyEvaluator(this.chatClient);
        EvaluationRequest evaluationRequest = new EvaluationRequest(chatBotResponse);
        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
        assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");

    }
}
