package seungjub270.roommate_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungjub270.roommate_spring.domain.School.School;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findBySchoolName(String name);
}
