package me.shinsunyoung.backend.PureWebsocket.room.service;


import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.PureWebsocket.room.entity.ChatRoom;
import me.shinsunyoung.backend.PureWebsocket.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public ChatRoom createRoom(String roomId){
        return roomRepository.findByRoomId(roomId)
                .orElseGet(()->{
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setRoomId(roomId);
                    return roomRepository.save(newRoom);
                });
    }

    public List<ChatRoom> findAllRooms(){
        return roomRepository.findAll();
    }
}
