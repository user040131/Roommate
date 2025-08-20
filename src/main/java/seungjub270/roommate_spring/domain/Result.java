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
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @Column
    private String dormitoryName;

    @Column
    private String roomName;

    @Column
    private String studentNumber;

    @Builder
    public Result(Long resultId, String dormitoryName, String roomName, String studentNumber) {
        this.resultId = resultId;
        this.dormitoryName = dormitoryName;
        this.roomName = roomName;
        this.studentNumber = studentNumber;
    }

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
}
