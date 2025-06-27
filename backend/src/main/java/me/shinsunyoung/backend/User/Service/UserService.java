package me.shinsunyoung.backend.User.Service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.User.DTO.UserDTO;
import me.shinsunyoung.backend.User.DTO.UserProfileDTO;
import me.shinsunyoung.backend.User.Entity.User;
import me.shinsunyoung.backend.User.Entity.UserProfile;
import me.shinsunyoung.backend.User.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final EntityManager em;

    private final UserRepository userRepository;
    //private final UserProfilerRepository userProfilerRepository;

    // 내 정보 조회 메서드
    @Transactional(readOnly = true)
    public UserDTO getMyInfo(Long id){

        System.out.println("서비스 진입");

        System.out.println("회원 정보 유효성 검사 진입");
        User user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        System.out.println("회원 정보 유효성 검사 완료");

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserid(user.getUserid());
        System.out.println("UserDTO 정상 생성 여부 확인 : " + dto);

        UserProfile profile = user.getUserProfile();
        System.out.println("유저 프로필 정상 반환 여부 확인 : " + profile);

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUsername(profile.getUsername());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        dto.setProfile(profileDTO);


        return dto;
    }

    // 유저 정보 수정
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        UserProfile profile = user.getUserProfile();

        if(profile != null && userDTO.getProfile() != null){
            UserProfileDTO dtoProfile = userDTO.getProfile();

            if(dtoProfile.getUsername() != null) profile.setUsername(dtoProfile.getUsername());
            if(dtoProfile.getEmail() != null) profile.setEmail(dtoProfile.getEmail());
            if(dtoProfile.getPhone() != null) profile.setPhone(dtoProfile.getPhone());
            if(dtoProfile.getAddress() != null) profile.setAddress(dtoProfile.getAddress());

        }

        // JPA에서 findById()로 가져온 엔티티는 영속 상태임.
        // 필드 값을 바꾸면 JPA가 트랜잭션 커밋할 때 자동으로 update 쿼리를 날림

        //아래는 변경된 내용을 프론트에 던져주기 위해 생성합니다.
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserid(user.getUserid());
        UserProfileDTO profileDTO = new UserProfileDTO();
        // assert profile != null; // 아래의 Username에서 null가능성이 있어서 임시 추가...
        profileDTO.setUsername(profile.getUsername());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        dto.setProfile(profileDTO);
        return dto;
    }

//    //dto로 순환참조 방지
//    public UserDTO getProfile(Long profileId)  {
//        UserProfile profile = userProfilerRepository.findById(profileId)
//                .orElseThrow(()->new RuntimeException("프로필 없음"));
//
//        User user =profile.getUser();
//        if (user==null) throw new RuntimeException("연결된 유저 없음");
//
//        UserProfileDTO profileDTO = new UserProfileDTO(
//                profile.getUsername(),
//                profile.getEmail(),
//                profile.getPhone(),
//                profile.getAddress()
//        );
//
//        UserDTO userDTO = new UserDTO(
//                user.getId(),
//                user.getUserid(),
//                profileDTO
//        );
//
//        return userDTO;
//    }



//    @Transactional
//    public void saveAllUsers(List<User> users) {
//        long start = System.currentTimeMillis();
//
//        for (int i = 0; i<users.size(); i++) {
//            em.persist(users.get(i));
//            if (i % 1000 == 0){
//                em.flush();
//                em.clear();
//            }
//        }
//
//        long end = System.currentTimeMillis();
//        System.out.println("JPA saveAll 저장 소요 시간(ms): " + (end - start));
//    }
    }

