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

    @Column
    private int onetoone;
    @Column
    private int onetotwo;
    @Column
    private int onetothree;
    @Column
    private int onetofour;
    @Column
    private int twotoone;
    @Column
    private int twototwo;
    @Column
    private int twotothree;
    @Column
    private int twotofour;
    @Column
    private int twotofive;
    @Column
    private int threetoone;
    @Column
    private int threetotwo;

    @Builder
    public Introduction(int onetoone, int onetotwo, int onetothree, int onetofour,
                        int twotoone, int twototwo, int twotothree, int twotofour, int twotofive,
                        int threetoone, int threetotwo) {
        this.onetoone = onetoone;
        this.onetotwo = onetotwo;
        this.onetothree = onetothree;
        this.onetofour = onetofour;
        this.twotoone = twotoone;
        this.twototwo = twototwo;
        this.twotothree = twotothree;
        this.twotofour = twotofour;
        this.twotofive = twotofive;
        this.threetoone = threetoone;
        this.threetotwo = threetotwo;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
}
