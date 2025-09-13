package seungjub270.roommate_spring.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerSignUpRequest {
    private String email;
    private String password;
    private int managerNumber;
    private String schoolName;
}
