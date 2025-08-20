package seungjub270.roommate_spring.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seungjub270.roommate_spring.domain.School.School;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column
    private String studentName;

    @Column
    private String studentEmail;

    @Column
    private String studentPhone;

    @Column
    private int studentNumber; //학번

    @Column
    private String studentPassword;

    @Column
    private boolean studentGender;

    @Column
    private String studentAddress;

    @Column
    private float studentDistance;

    @Builder
    public Student(String studentName, String studentEmail, String studentPhone, int studentNumber, String studentPassword, boolean studentGender, String studentAddress, float studentDistance) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.studentNumber = studentNumber;
        this.studentPassword = studentPassword;
        this.studentGender = studentGender;
        this.studentAddress = studentAddress;
        this.studentDistance = studentDistance;
    } //제일 처음 생성할 때 씀

    @Builder
    public Student(String studentName, String studentEmail, String studentPhone, int studentNumber, boolean studentGender, String studentAddress, float studentDistance) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.studentNumber = studentNumber;
        this.studentPassword = studentPassword;
        this.studentGender = studentGender;
        this.studentAddress = studentAddress;
        this.studentDistance = studentDistance;
    } //개인정보 요청 변경이 오면 사용

    @Builder
    public Student(String studentPassword){
        this.studentPassword = studentPassword;
    } //학생 개인이 비밀번호 변경할 때 사용

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToOne(mappedBy = "student", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Characteristic characteristic;

    @OneToOne(mappedBy = "student", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Introduction introduction;

}
