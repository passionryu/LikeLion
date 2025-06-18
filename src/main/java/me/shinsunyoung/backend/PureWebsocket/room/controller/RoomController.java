package me.shinsunyoung.backend.PureWebsocket.room.controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.PureWebsocket.room.entity.ChatRoom;
import me.shinsunyoung.backend.PureWebsocket.room.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public List<ChatRoom> getAllRoom(String roomId){
         return roomService.findAllRooms();
    }

    @PostMapping("/{roomId}")
    public ChatRoom createRoom(@PathVariable String roomId){
        return roomService.createRoom(roomId);
    }
}
