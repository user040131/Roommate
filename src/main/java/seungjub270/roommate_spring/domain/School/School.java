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

    public void appointDormitoryList(Dormitory dormitory){
        if(!dormitoryList.contains(dormitory)){
            dormitoryList.add(dormitory);
        }
        dormitory.appointSchool(this);
    }

    @OneToOne(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileChangeRequest profileChangeRequest;

    public void appointProfileChangeRequest(ProfileChangeRequest profileChangeRequest){
        this.profileChangeRequest = profileChangeRequest;
        profileChangeRequest.appointSchool(this);
    }

    @OneToOne(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Result result;

    public void appointResult(Result result){
        this.result = result;
        result.appointSchool(this);
    }

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    public void appointAccounts(Account account){
        if(!accounts.contains(account)){
            accounts.add(account);
        }
        account.appointSchool(this);
    }

    @Builder
    public School(String schoolName, String schoolX_Coordinate, float schoolY_Coordinate, List<Dormitory> dormitoryList) {
        this.schoolName = schoolName;
        this.schoolX_Coordinate = Float.parseFloat(schoolX_Coordinate);
        this.schoolY_Coordinate = schoolY_Coordinate;
        this.dormitoryList = dormitoryList;
    }
}
