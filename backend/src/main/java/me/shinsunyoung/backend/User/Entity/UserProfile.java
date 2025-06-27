package me.shinsunyoung.backend.User.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor // 모든 생성자 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    /* 프로필은 유저에 속하기 때문에 연관관계 세팅 */
    @OneToOne
    @JoinColumn(name="user_id") //참조키
    private User user;

}
