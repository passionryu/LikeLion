package me.shinsunyoung.backend.StompWebSocket.Gpt;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class GPTService {

    //json문자열 <-> 자바객체, json객체
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public String gptMessage(String message) throws Exception {

        //API 호출을 위한 본문 작성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("input", message);

        //http 요청 작성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody))) //본문 삽입
                .build();

        //요청 전송 및 응답 수신
        HttpClient client = HttpClient.newHttpClient();

        /* GPT 사용량이 초과되면 오류를 일으킬 위험이 있음 */
        //HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response;
        /* GPT 사용량이 초과되는 상황에 대한 예외처리 */
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 429) {
                throw new RuntimeException("Gpt 사용량 초과로 임시 오류");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Gpt 사용량 초과로 임시 오류", e);
        }

        //응답을 Json으로 파싱
        JsonNode jsonNode = mapper.readTree(response.body());
        System.out.println("gpt 응답 : " + jsonNode);

        //메세지 부분만 추출하여 반환
        String gptMessageResponse = jsonNode
                .get("output")
                .get(0)
                .get("content")
                .get(0)
                .get("text")
                .asText();
        return gptMessageResponse;

    }

}