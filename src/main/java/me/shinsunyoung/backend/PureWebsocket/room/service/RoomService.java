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

    /**
     * 방 생성 메서드
     *
     * @param roomId 생성할 RoomId
     * @return 방 생성 후 생성한 방 번호
     */
    public ChatRoom createRoom(String roomId){
        return roomRepository.findByRoomId(roomId)
                .orElseGet(()->{
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setRoomId(roomId);
                    return roomRepository.save(newRoom);
                });
    }

    /**
     * 생성된 방 찾기 메서드
     *
     * @return DB에 있는 모든 방 찾기
     */
    public List<ChatRoom> findAllRooms(){
        return roomRepository.findAll();
    }
}
