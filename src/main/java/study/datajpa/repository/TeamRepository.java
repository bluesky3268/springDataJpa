package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

// JpaRepository를 상속받으면 Spring data jpa가 구현체를 만들어서 주입해준다.
// @Repository도 생략가능
public interface TeamRepository extends JpaRepository<Team, Long> {
}
