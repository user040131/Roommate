package seungjub270.roommate_spring.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentSignUpRequest {
    private String email;
    private String password;
    private String studentName;
    private String studentPhone;
    private int studentNumber;
    private boolean studentGender;
    private String studentAddress;
    private float studentDistance;
    private String schoolName;

    public boolean getStudentGender() {
        return studentGender;
    }
}
