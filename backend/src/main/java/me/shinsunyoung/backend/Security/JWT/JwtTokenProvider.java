package me.shinsunyoung.backend.Security.JWT;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.Security.Core.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    /** Jwt 토큰 생성 및 추출 검증하는 클래스 **/

    // 토큰을 만들때 서명하는 키
    private final SecretKey secretKey;

    /**
     * 현재 로그인이 완료된 사용자 정보를 기반으로 access, refresh token 발급 하는 메서드
     *
     * @param authentication Spring Security의 Authentication 객체로, 현재 인증된 사용자 정보 포함
     * @param expirationMillis 만료 시간
     * @return JWT 토큰 반환
     */
    public String generateToken(Authentication authentication, Long expirationMillis) {

        // 인증된 사용자 정보에서 인증된 주체(Object)를 반환해서 (CustomUserDetails)로 명시적 형 변환을 실시한다.
        CustomUserDetails customUserDetails =(CustomUserDetails) authentication.getPrincipal();

        //토큰 만료시간 생성 (밀리초 단위 까지)
        Date expiryDate = new Date(new Date().getTime() + expirationMillis);

        // Jwts는 Java JWT 라이브러리(jjwt)의 클래스이다.
        Claims claims = Jwts.claims();
        // Payload의 클레임은 key/value 형식으로 저장이 된다.
        claims.put("user-id", customUserDetails.getId());
        claims.put("user-name", customUserDetails.getUsername());

        // Payload 부분을 빌더 패턴으로 반환
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername()) // JWT > Payload > subject를 유저 네임으로 지정
                .setClaims(claims) // Jwt > Payload > Claims를 위에서 지정한 Claims 객체로 저장
                .setIssuedAt(new Date()) // Jwt > Payload > 토큰 발급 시간
                .setExpiration(expiryDate) // Jwt > Payload > 토큰 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS512) // Jwt > Signature > 암호화 서명 (사용한 알고리즘 : SignatureAlgorithm.HS512 )
                .compact(); //에서 저장한 정보들을 최종적으로 문자열로 만들어주는 메서드
    }

    /**
     * Jwt 토큰에서 사용자 ID를 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return UserId
     */
    public Long getUserIdFromToken(String token){
        return Jwts
                .parserBuilder() //Jwt 토큰을 해석하겠다고 선언
                .setSigningKey(secretKey) // 토큰을 검증하기 위해 비밀키 사용
                .build() // 해석할 준비 완료
                .parseClaimsJws(token) // 전달을 받은 토큰을 파싱
                .getBody() //파싱한 토큰의 payload 부분을 추출
                .get("user-id", Long.class); // user-id를 반환
    }

    /**
     * 토큰 유효성 검사 메서드
     *
     * @param token Jwt 토큰
     * @return T&F
     */
    public Boolean validateToken(String token){

        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch(MalformedJwtException e){

            // 토큰의 형식이 잘못되었을 때
            return false;
        }
        catch(ExpiredJwtException e){

            // 토큰 만료
            return false;
        }
        catch(UnsupportedJwtException e){

            // 지원하지 않는 토큰
            return false;
        }
        /*catch(IllegalAccessException e){
            // 토큰의 문자열이 비어있거나 이상할 때
            return false;
        }*/
        catch(JwtException e){

            // 기타 예외
            return false;
        }

    }

}
