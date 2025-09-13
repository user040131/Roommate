package seungjub270.roommate_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateRefreshTokenResponse {
    private String refreshToken;
}
