package me.shinsunyoung.backend.PureWebsocket.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.shinsunyoung.backend.PureWebsocket.DTO.ChatMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* STOMP 융합이 아닌, 순수 Websocket 처리기 */
public class ChatWebSocketHandler extends TextWebSocketHandler {

    /**
     * 현재 접속 중인 클라이언트 세션들을 저장하는 스레드 안전한 Set입니다.
     * 이 세션 목록을 통해 메시지를 브로드캐스트하거나 특정 유저에게 보낼 수 있습니다.
     *
     * -> 유저의 중복을 허용하면 안되기 때문에, List가 아니라 Set을 사용함
     */
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    /**
     * 문자열 - 객체 변환기
     *
     * 웹소켓에서 메시지를 주고받을 때 JSON 포맷으로 데이터를 보내는 것이 일반적이기 때문에,
     * 이 ObjectMapper는 문자열(JSON) ↔ Java 객체 변환을 담당
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    //private final Map<String,WebSocketSession> rooms = new ConcurrentHashMap<>();
    // 수정
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    /**
     * WebSocket 연결이 처음 접속했을 때 호출
     *
     * @param session 클라이언트의 ID
     * @throws Exception
     *
     * super 호출만 하고 있으므로 현재는 아무 동작도 하지 않음.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        // session은 아이디 - > 등록
        sessions.add(session);

        System.out.println("접속된 클라이언트의 세션 ID =" + session.getId());
    }

    /**
     * 클라이언트가 텍스트 메시지를 보냈을 때 호출
     *
     * @param session 클라이언트의 ID
     * @param message 클라이언트가 보낸 텍스트 메시지
     * @throws Exception
     *
     * TextMessage는 메시지 본문을 담고 있음.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        String roomID = chatMessage.getRoomId();

        if(!rooms.containsKey(roomID)){
            rooms.put(roomID, ConcurrentHashMap.newKeySet());
        }
        rooms.get(roomID).add(session);

        for(WebSocketSession s : rooms.get(roomID)){
        // for(WebSocketSession s : sessions){
            /* 세션이 연결되어 있으면 */
            if(s.isOpen()){
                /* 자바 객체 -> Json 문자열 */
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));

                System.out.println("전송 메시지 : " + chatMessage.getMessage());
            }
        }
    }

    /**
     * 클라이언트가 WebSocket을 종료했을 때 호출
     *
     * @param session 클라이언트의 ID
     * @param status 상태
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        /* 세션 삭제로 불필요한 리소스 낭비 방지 */
        sessions.remove(session);

        for(Set<WebSocketSession> room : rooms.values()){
            room.remove(session);
        }
    }
}
