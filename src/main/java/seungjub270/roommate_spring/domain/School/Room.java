package seungjub270.roommate_spring.domain.School;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String roomNum;

    @Column
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dormitory_id", nullable = false)
    private Dormitory dormitory;

    public void appointDormitory(Dormitory dormitory) {
        this.dormitory = dormitory;
    }
}
