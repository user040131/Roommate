package seungjub270.roommate_spring.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seungjub270.roommate_spring.domain.School.School;

import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProfileChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String request;

    @Column
    private Date date;

    @Column
    private boolean accepted;

    @Builder
    public ProfileChangeRequest(String request, Date date, boolean accepted) {
        this.request = request;
        this.date = date;
        this.accepted = accepted;
    }

    @Builder
    public ProfileChangeRequest(boolean accepted) {
        this.accepted = accepted;
    }

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    public void appointSchool(School school) {
        this.school = school;
    }
}
