package me.shinsunyoung.backend.Auth.DTO;

import lombok.*;
import me.shinsunyoung.backend.Auth.Entiity.Auth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long userId;

    @Builder
    public LoginResponseDTO(Auth auth) {
        this.tokenType = auth.getTokenType();
        this.accessToken = auth.getAccessToken();
        this.refreshToken = auth.getRefreshToken();
        this.userId = auth.getId();
    }
}
