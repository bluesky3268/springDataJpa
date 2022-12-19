package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 기본적으로 롤백을 시킨다.
@Transactional
//@Rollback(false) // 롤백을 하지 않겠다. -> 실무에서는 테스트 할 때는 쓰면 안된다.
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {
        // given
        Member member = Member.builder()
                .username("memberA")
                .build();

        // when
        Member savedMember = memberJpaRepository.save(member);

        // then
        Member findMember = memberJpaRepository.find(savedMember.getMemberNo());

        assertThat(findMember.getMemberNo()).isEqualTo(savedMember.getMemberNo());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    public void CRUDTest() {
        Member member1 = Member.builder()
                .username("member1")
                .build();
        Member member2 = Member.builder()
                .username("member2")
                .build();

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getMemberNo()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getMemberNo()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 목록 조회
        List<Member> findMembers = memberJpaRepository.findAll();
        assertThat(findMembers.size()).isEqualTo(2);

        // 카운트
        long memberCount1 = memberJpaRepository.count();
        assertThat(memberCount1).isEqualTo(2);

        // 삭제
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        // 카운트
        long afterDeleteCount = memberJpaRepository.count();
        assertThat(afterDeleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member member1 = Member.builder()
                .username("member1")
                .age(20)
                .build();
        Member member2 = Member.builder()
                .username("member1")
                .age(10)
                .build();
        Member member3 = Member.builder()
                .username("member1")
                .age(18)
                .build();

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);

        List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("member1", 15);

        assertThat(members.size()).isEqualTo(2);
        assertThat(members.get(0).getUsername()).isEqualTo("member1");

    }

    @Test
    public void paging() {
        // given
        memberJpaRepository.save(Member.builder()
                .username("member1")
                .age(18)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member2")
                .age(10)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member3")
                .age(10)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member4")
                .age(10)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member5")
                .age(10)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member6")
                .age(20)
                .build());

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        //then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(4);
    }

    @Test
    public void bulkUpdateTest() {
        // given
        memberJpaRepository.save(Member.builder()
                .username("member1")
                .age(10)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member2")
                .age(15)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member3")
                .age(20)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member4")
                .age(25)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member5")
                .age(30)
                .build());
        memberJpaRepository.save(Member.builder()
                .username("member6")
                .age(35)
                .build());

        // when
        int result = memberJpaRepository.bulkAgePlus(20);

        // then
        assertThat(result).isEqualTo(4);
    }
}