package seungjub270.roommate_spring.domain.School;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import seungjub270.roommate_spring.domain.Account;
import seungjub270.roommate_spring.domain.ProfileChangeRequest;
import seungjub270.roommate_spring.domain.Result;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id")
    private long id;

    @Column
    private String schoolName;

    @Column
    private float schoolX_Coordinate;

    @Column
    private float schoolY_Coordinate;

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dormitory> dormitoryList = new ArrayList<>();

    @OneToOne(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileChangeRequest profileChangeRequest;

    @OneToOne(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Result result;

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    @Builder
    public School(String schoolName, String schoolX_Coordinate, float schoolY_Coordinate, List<Dormitory> dormitoryList) {
        this.schoolName = schoolName;
        this.schoolX_Coordinate = Float.parseFloat(schoolX_Coordinate);
        this.schoolY_Coordinate = schoolY_Coordinate;
        this.dormitoryList = dormitoryList;
    }
}
