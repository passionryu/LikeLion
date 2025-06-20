package me.shinsunyoung.backend.StompWebSocket.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String message;
    private String from;

    private String to; //귓속말 한 상대
    private String roomId; // 방 ID

    public ChatMessage(String from, String message) {
        this.from = from;
        this.message = message;
    }
}
