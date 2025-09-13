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
    private String studentPhone;

    @Column
    private int studentNumber; //학번

    @Column
    private boolean studentGender;

    @Column
    private String studentAddress;

    @Column
    private float studentDistance;

    public Student(String studentName, String studentPhone, int studentNumber,
                           boolean studentGender, String studentAddress, float studentDistance) {
        this.studentName = studentName;
        this.studentPhone = studentPhone;
        this.studentNumber = studentNumber;
        this.studentGender = studentGender;
        this.studentAddress = studentAddress;
        this.studentDistance = studentDistance;
    }

    public static Student newStudent(String studentName, String studentPhone, int studentNumber,
                                     boolean studentGender, String studentAddress, float studentDistance) {
        Student student = new Student(studentName, studentPhone, studentNumber, studentGender, studentAddress,
                studentDistance);
        return student;
    }

    //    @Builder
//    public Student(String studentName, String studentEmail, String studentPhone, int studentNumber, String studentPassword, boolean studentGender, String studentAddress, float studentDistance) {
//        this.studentName = studentName;
//        this.studentEmail = studentEmail;
//        this.studentPhone = studentPhone;
//        this.studentNumber = studentNumber;
//        this.studentPassword = studentPassword;
//        this.studentGender = studentGender;
//        this.studentAddress = studentAddress;
//        this.studentDistance = studentDistance;
//    } //제일 처음 생성할 때 씀
//
//    @Builder
//    public Student(String studentName, String studentEmail, String studentPhone, int studentNumber, boolean studentGender, String studentAddress, float studentDistance) {
//        this.studentName = studentName;
//        this.studentEmail = studentEmail;
//        this.studentPhone = studentPhone;
//        this.studentNumber = studentNumber;
//        this.studentPassword = studentPassword;
//        this.studentGender = studentGender;
//        this.studentAddress = studentAddress;
//        this.studentDistance = studentDistance;
//    } //개인정보 요청 변경이 오면 사용

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public void appointAccount(Account account){
        this.account = account;
    }

    @OneToOne(mappedBy = "student", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Characteristic characteristic;

    public void appointCharacteristic(Characteristic characteristic){
        this.characteristic = characteristic;
        characteristic.appointStudent(this);
    }

    @OneToOne(mappedBy = "student", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private Introduction introduction;

    public void appointIntroduction(Introduction introduction){
        this.introduction = introduction;
        introduction.appointStudent(this);
    }

    public Introduction getIntroduction(){ return this.introduction; }

    public static Student newStudent(String studentName, String studentPhone, int studentNumber, String studentAddress
    , float studentDistance, boolean studentGender, Account account) {
        Student s = new Student();
        s.studentName = studentName;
        s.studentPhone = studentPhone;
        s.studentNumber = studentNumber;
        s.studentGender = studentGender;
        s.studentAddress = studentAddress;
        s.studentDistance = studentDistance;
        s.appointAccount(account);
        return s;
    }

}
