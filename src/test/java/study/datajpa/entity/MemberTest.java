package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = Member.builder()
                .username("memberA")
                .age(10)
                .team(teamA)
                .build();
        Member memberB = Member.builder()
                .username("memberB")
                .age(20)
                .team(teamB)
                .build();

        em.persist(memberA);
        em.persist(memberB);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        for (Member member : members) {
            System.out.println("=== member : " + member);
            System.out.println("=== --> member.team : " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws InterruptedException {
        // given
        Member memberA = Member.builder()
                .username("memberA")
                .age(20)
                .build();
        memberRepository.save(memberA);

        Thread.sleep(100);
        memberA.changeUsername("memberAAA");

        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findById(memberA.getMemberNo()).get();

        // then
        System.out.println("createdDate : " + findMember.getCreatedDate());
        System.out.println("updatedDate : " + findMember.getLastModifiedDate());
        System.out.println("createdBy : " + findMember.getCreatedBy());
        System.out.println("lastModifiedBy : " + findMember.getUpdatedBy());
    }

}