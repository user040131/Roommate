package seungjub270.roommate_spring.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Characteristic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int characteristicId;

    @Column
    private int sleepTime;
    //22시 이전, 22~24시, 24시 이후

    @Column
    private int wakeUpTime;
    //6, 6~8, 8

    @Column
    private int studyLocation;
    //indoor, outdoor

    @Column
    private int freeTimeLocation;
    //indoor, outdoor

    @Column
    private int cleanlinessLevel;
    //sensitive, insensitive

    @Column
    private int smellTolerance;
    //sensitive, insensitive

    @Column
    private boolean smokingHabit;
    //피면 true, 안 피면 false

    @Column
    private int noiseTolerance;
    //sensitive, insensitive

    @Column
    private boolean extroversion;
    //혼자만의 시간 필수 x -> true
    //아니면 false

    @Builder
    public Characteristic(int sleepTime, int wakeUpTime, int studyLocation, int freeTimeLocation,
                          int cleanlinessLevel, int smellTolerance, boolean smokingHabit,
                          int noiseTolerance, boolean extroversion) {
        this.sleepTime = sleepTime;
        this.wakeUpTime = wakeUpTime;
        this.studyLocation = studyLocation;
        this.freeTimeLocation = freeTimeLocation;
        this.cleanlinessLevel = cleanlinessLevel;
        this.smellTolerance = smellTolerance;
        this.smokingHabit = smokingHabit;
        this.noiseTolerance = noiseTolerance;
        this.extroversion = extroversion;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    public void appointStudent(Student student) {
        this.student = student;
    }
}
