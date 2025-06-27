package me.shinsunyoung.backend.Auth.Service;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.Auth.DTO.LoginRequestDTO;
import me.shinsunyoung.backend.Auth.DTO.LoginResponseDTO;
import me.shinsunyoung.backend.Auth.DTO.SignUpRequestDTO;
import me.shinsunyoung.backend.Auth.Entiity.Auth;
import me.shinsunyoung.backend.Auth.Repository.AuthRepository;
import me.shinsunyoung.backend.Security.Core.CustomUserDetails;
import me.shinsunyoung.backend.Security.Core.Role;
import me.shinsunyoung.backend.Security.JWT.JwtTokenProvider;
import me.shinsunyoung.backend.User.DTO.UserDTO;
import me.shinsunyoung.backend.User.DTO.UserProfileDTO;
import me.shinsunyoung.backend.User.Entity.User;
import me.shinsunyoung.backend.User.Entity.UserProfile;
import me.shinsunyoung.backend.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.accessTokenExpirationTime}")
    private Long jwtAccessTokenExpirationTime;
    @Value("${jwt.refreshTokenExpirationTime}")
    private Long jwtRefreshTokenExpirationTime;

    @Transactional
    public void signUp(SignUpRequestDTO dto) {

        // isPresent를 통해 Optional 객체에 값이 있냐 없냐를 확인함
        if(userRepository.findByUserid(dto.getUserid()).isPresent()) {
            throw new RuntimeException("사용자가 이미 존재합니다.");
        }

        User user = new User();
        user.setUserid(dto.getUserid());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // 비밀번호를 암호화 하여 저장
        user.setRole(Role.ROLE_USER); // 일반 사용자로 회원 가입

        UserProfile profile = new UserProfile();
        profile.setUsername(dto.getUsername());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());

        /* 연관관계 설정 */
        profile.setUser(user);
        user.setUserProfile(profile);

        userRepository.save(user);

    }

    // 로그인
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){

        User user = userRepository.findByUserid(loginRequestDTO.getUserid())
                .orElseThrow(()->new RuntimeException("해당 유저를 찾을 수 없습니다."));



        // 입력한 비밀번호가 암호회된 비밀번호와 일치하는지 확인
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치 하지 않습니다.");
            // 시큐리티 로그인 과정에서 비밀번호가 일치하지 않으면 던져주는 예외
        }

        // 위 비밀번호가 일치하면 기존 토큰 정보를 비교하고 토큰이 있으면 업데이트, 없으면 새로 발급
        String accessToken = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user)
                        ,user.getPassword()),jwtAccessTokenExpirationTime);

        String refreshToken = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user)
                        ,user.getPassword()),jwtRefreshTokenExpirationTime);

        // 현재 로그인 한 사람이 DB에 있는지 확인히고 있으면 토큰을 DB에 저장하고 로그인 처리
        if(authRepository.existsByUser(user)){
            Auth auth = user.getAuth();
            auth.setAccessToken(accessToken);
            auth.setRefreshToken(refreshToken);
            authRepository.save(auth);
            return new LoginResponseDTO(auth);
        }

        // 위에서 DB에 사용자 정보가 없으면 아래 새로 생성해서 로그인 처리
        Auth auth = new Auth(user,refreshToken,accessToken,"Bearer");
        authRepository.save(auth);


        return new LoginResponseDTO(auth);
    }

    //리프레시 토큰을 받아서 새로운 엑세스 토큰을 발급해주는 서비스
    @Transactional
    public String refreshToken(String refreshToken) {
        //리프레시 토큰 유효성 검사
        if (jwtTokenProvider.validateToken(refreshToken)) {
            //DB에서 리프레시토큰을 가진 사용자가 있는지 확인
            Auth auth = authRepository.findByRefreshToken(refreshToken).orElseThrow(
                    () -> new IllegalArgumentException("해당 REFRESH_TOKEN 을 찾을 수 없습니다.\nREFRESH_TOKEN = " + refreshToken));

            //있으면 인증객체를 만ㄷ르어서 새로운 토큰 발급
            String newAccessToken = jwtTokenProvider.generateToken(
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(auth.getUser()), auth.getUser().getPassword()),jwtAccessTokenExpirationTime); //엑세스 토큰 만료시간으로 설정 중요!

            auth.updateAccessToken(newAccessToken); //토큰 업데이트
            authRepository.save(auth); // DB에 반영

            return newAccessToken;
        }
        else {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
    }


}
