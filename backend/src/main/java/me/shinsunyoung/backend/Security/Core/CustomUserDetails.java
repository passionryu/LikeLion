package me.shinsunyoung.backend.Security.Core;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.User.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    // UserDetails <- 사용자 정보를 담는 인터페이스
    // 로그인 한 사용자의 정보를 담아 두는 역할

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // user의 권한을 반환하는 메서드
        // Collections.singleton <- 이 사용자는 한가지 권한만 갖는다는 의미
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    // 토큰에서 추출한 사용자 정보의 ID를 반환  (테이블의 pk 값)
    // User 엔티티에서 ID 추출
    public Long getId(){
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // User 엔티티에서 password를 반환
    }

    // 중복되지 않는 UserId값 반환
    @Override
    public String getUsername() {
        return user.getUserid();
    }

    // ======================= 아래는 현재 계정 상태를 판단하는 메서드 =========================

    // 현재 계정 상태가 활성화 인지
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 이 계정이 만료된 계정인지
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 이 계정이 잠겨있는 계정인지
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격증명이 만료된 계정인지
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
