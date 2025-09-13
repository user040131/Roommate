package seungjub270.roommate_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungjub270.roommate_spring.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
