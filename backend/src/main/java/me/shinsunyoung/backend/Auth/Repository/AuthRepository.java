package me.shinsunyoung.backend.Auth.Repository;

import me.shinsunyoung.backend.Auth.Entiity.Auth;
import me.shinsunyoung.backend.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth,Long> {

    boolean existsByUser(User user);

    Optional<Auth> findByRefreshToken(String refreshToken);

    Optional<Auth> findByUser(User user);

}
