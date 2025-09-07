package seungjub270.roommate_spring.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Introduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long introductionId;

    @Column private Integer onetoone;
    @Column private Integer onetotwo;
    @Column private Integer onetothree;
    @Column private Integer onetofour;

    @Column private Integer twotoone;
    @Column private Integer twototwo;

    @Column private Integer threetoone;
    @Column private Integer threetotwo;

    @Column private Integer lastIntroAnalyze;

    @Builder
    public Introduction(Integer onetoone, Integer onetotwo, Integer onetothree, Integer onetofour,
                        Integer twotoone, Integer twototwo,
                        Integer threetoone, Integer threetotwo,
                        Integer lastIntroAnalyze){
        this.onetoone = onetoone;
        this.onetotwo = onetotwo;
        this.onetothree = onetothree;
        this.onetofour = onetofour;
        this.twotoone = twotoone;
        this.twototwo = twototwo;
        this.threetoone = threetoone;
        this.threetotwo = threetotwo;
        this.lastIntroAnalyze = lastIntroAnalyze;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    public void appointStudent(Student student){
        this.student = student;
    }
}
