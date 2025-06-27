package me.shinsunyoung.backend.User.Controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.Security.Core.CustomUserDetails;
import me.shinsunyoung.backend.User.DTO.UserDTO;
import me.shinsunyoung.backend.User.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

//    @Value("${PROJECT_NAME:web Server}")
//    private String instansName;
//
//    @GetMapping
//    public String test(){
//        return instansName;
//    }

    private final UserService userService;

    // 내 정보 보기
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long id = userDetails.getId();
        return ResponseEntity.ok(userService.getMyInfo(id));
    }

    // 유저 정보 수정
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserDTO userDTO)  {
        Long id = userDetails.getId();
        UserDTO updated = userService.updateUser(id,userDTO);
        return ResponseEntity.ok(updated);
    }

    //아래는 순환참조가 되는  예제
//    @GetMapping("/profile/{profileId}")
//    public User getProfile2(@PathVariable Long profileId)  {
//        return userService.getProfile2(profileId);
//    }

}
