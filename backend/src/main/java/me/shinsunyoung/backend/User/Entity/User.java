package me.shinsunyoung.backend.User.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.shinsunyoung.backend.Auth.Entiity.Auth;
import me.shinsunyoung.backend.Board.Entity.Board;
import me.shinsunyoung.backend.Security.Core.Role;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor // 모든 생성자 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true) //unique 옵션을 통해 중복을 허용하지 않음
    private String userid;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // 이 펠드를 DB에 문자열로 저장하라는 의미
    private Role role;

    @OneToOne(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private Auth auth;

    @OneToMany(mappedBy ="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();

}
