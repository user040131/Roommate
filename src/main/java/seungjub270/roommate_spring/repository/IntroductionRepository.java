package seungjub270.roommate_spring.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import seungjub270.roommate_spring.domain.Introduction;

public interface IntroductionRepository extends JpaRepository<Introduction, Integer> {
    //만들기는 했는데 쓸모는 없을 듯
}
