package me.shinsunyoung.backend.PureWebsocket.DTO;

import lombok.Getter;

@Getter
public class ChatMessage {

    private String roomId;
    //  메시지
    private String message;
    // 누가 보냈는지?
    private String from;
}
