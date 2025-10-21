package seungjub270.roommate_spring.dto;

import lombok.Data;

@Data
public class BulkStudentDto {
    String email;
    String password;
    String name;
    String phone;
    String studentNo;
    String gender;
    String address;
}
