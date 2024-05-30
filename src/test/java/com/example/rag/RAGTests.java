package com.example.rag;

import com.example.rag.data.DataController;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.model.Content;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RAGTests {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private DataController dataController;

    @Autowired
    private VectorStore vectorStore;

    @Test
    void noContext() {
        var response = ChatClient.builder(chatModel).build()
                .prompt()
                .user("What is Carina?")
                .call()
                .content();
        System.out.println(response);
    }


    @Test
    void stuffPrompt() {
        String context = """
                What is Carina?
                Founded in 2016, Carina is a technology nonprofit that provides a safe, easy-to-use, online
                location-based care matching service. We serve individuals and families searching for home
                care or child care and care professionals who are looking for good jobs. Carina is committed to
                building community and prioritizing people over profit. Through our partnerships with unions
                and social service agencies, we build online tools to bring good jobs to care workers, so they
                can focus on their passion — caring for others.
                Our vision is a care economy that strengthens our communities by respecting and supporting
                workers, individuals and families. We offer a care matching platform where verified care
                providers can connect with individuals and families who need care.
                """;

        var response = ChatClient.builder(chatModel).build()
                .prompt()
                .user("What is Carina? Please consider the following context when answering the question: " + context)
                .call()
                .content();
        System.out.println(response);
    }


    @Test
    void loadData() {
        dataController.delete();
        dataController.load();
    }
    @Test
    void purposeQuestion() {

        String userText = "How do I sign up to use Carina?";


        var response = ChatClient.builder(chatModel)
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()))
                .user(userText)
                .call()
                .chatResponse();


        System.out.println("Response: " + response.getResult().getOutput().getContent());

        // evaluate(userText, response);
    }

    private void evaluate(String userText, ChatResponse response) {
        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText,
                (List<Content>) response.getMetadata().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS), response);

        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");
        System.out.println("Evaluation Test: " + evaluationResponse.isPass());
    }
}
