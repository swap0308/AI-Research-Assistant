package com.research.research_assist.Service;

import com.research.research_assist.Request.ResearchRequest;
import com.research.research_assist.Response.Candidate;
import com.research.research_assist.Response.Content;
import com.research.research_assist.Response.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class ResearchService {

    @Value("${gemini.api.url}")
    private String geminiUrl;


    @Value("${gemini.api.key}")
    private String geminiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ResearchService(WebClient.Builder webClient, ObjectMapper objectMapper) {
        this.webClient = webClient.build();
        this.objectMapper = objectMapper;
    }


    public String processContent(ResearchRequest researchRequest) {

        String prompt = processRequest(researchRequest);

        Map<String,Object> requestBody = getRequestBody(prompt);

        System.out.println("The request body is :" + requestBody);

        GeminiResponse response = webClient.post()
                .uri(geminiUrl + geminiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        r -> r.bodyToMono(String.class)
                                .map(err -> new RuntimeException("Gemini error: " + err))
                )
                .bodyToMono(GeminiResponse.class)
                .block();

        System.out.println(response);
        System.out.println("\n\n");
        System.out.println("This is processed");

        return extractTextFromResponse(response);
    }

    private String extractTextFromResponse(GeminiResponse response){

        try {

            if(response != null && !response.getCandidates().isEmpty()){
                Candidate candidate = response.getCandidates().getFirst();

                if(candidate.getContent()!= null){
                    Content content = candidate.getContent();

                    if(content.getParts()!=null && !content.getParts().isEmpty()){
                        String res = content.getParts().getFirst().getText();
                        return  res;
                    }
                }
            }

            return "Some error occured.......";

        }catch (Exception e){
            return "Error parsing " + e.getMessage();
        }
    }

    private Map<String,Object> getRequestBody(String prompt){

        return Map.of("contents" , List.of(
                Map.of("parts",List.of(
                        Map.of("text",prompt)))

        ));
    }

    private String processRequest(ResearchRequest request){

        StringBuilder prompt = new StringBuilder();

        switch(request.getOperation()){

            case "summarize":
                prompt.append("Provide a clear and concise summary of the following text in few sentences\n\n");
                break;

            case "suggest":
                prompt.append("Based on the following content: suggest related topics and further reading. Provide the information in bullet points \n \n");
                break;

            default:
                throw new IllegalArgumentException("Unknown operation: "+ request.getOperation());
        }

        prompt.append(request.getContent());
        return prompt.toString();
    }
}
