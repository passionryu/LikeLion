package me.shinsunyoung.backend.PureWebsocket.room.repository;

import me.shinsunyoung.backend.PureWebsocket.DTO.ChatMessage;
import me.shinsunyoung.backend.PureWebsocket.room.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomId(String roomId);
}
