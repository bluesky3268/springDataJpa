package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        // 구현체는 내가 만든 적이 없고 인터페이스만 있는데 어떻게 동작하냐..
        System.out.println("memberRepository : " + memberRepository.getClass());
        // 스프링을 통해서 spring data jpa가 구현 클래스(프록시 객체)를 만들어서 주입한다.

        // given
        Member member = Member.builder()
                .username("memberA")
                .build();

        // when
        Member savedMember = memberRepository.save(member);

        // then
        Member findMember = memberRepository.findById(savedMember.getMemberNo()).get();

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

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getMemberNo()).get();
        Member findMember2 = memberRepository.findById(member2.getMemberNo()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 목록 조회
        List<Member> findMembers = memberRepository.findAll();
        assertThat(findMembers.size()).isEqualTo(2);

        // 카운트
        long memberCount1 = memberRepository.count();
        assertThat(memberCount1).isEqualTo(2);

        // 삭제
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        // 카운트
        long afterDeleteCount = memberRepository.count();
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

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("member1", 15);

        assertThat(members.size()).isEqualTo(2);
        assertThat(members.get(0).getUsername()).isEqualTo("member1");

    }

    @Test
    public void findTop3ByAge() {
        Member member1 = Member.builder()
                .username("member1")
                .age(20)
                .build();
        Member member2 = Member.builder()
                .username("member2")
                .age(10)
                .build();
        Member member3 = Member.builder()
                .username("member3")
                .age(18)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        List<Member> top3ByAge = memberRepository.findTop3ByOrderByAgeDesc();
        for (Member member : top3ByAge) {
            System.out.println("========== " + member.toString());
        }
    }

    @Test
    public void testQuery() {
        Member member1 = Member.builder()
                .username("member1")
                .age(20)
                .build();
        Member member2 = Member.builder()
                .username("member2")
                .age(10)
                .build();
        Member member3 = Member.builder()
                .username("member3")
                .age(18)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        List<Member> members = memberRepository.findMember("member1", 20);

        Member findMember = members.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void findUsernames() {
        Member member1 = Member.builder()
                .username("member1")
                .age(20)
                .build();
        Member member2 = Member.builder()
                .username("member2")
                .age(10)
                .build();
        Member member3 = Member.builder()
                .username("member3")
                .age(18)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        List<String> usernames = memberRepository.findUsernames();
        for (String username : usernames) {
            System.out.println("=== username : " + username);
        }
    }

    @Test
    public void findMemberDto() {
        Team teamA = Team.builder()
                .name("TeamA")
                .build();
        teamRepository.save(teamA);

        Member member1 = Member.builder()
                .username("member1")
                .age(20)
                .team(teamA)
                .build();

        Member member2 = Member.builder()
                .username("member2")
                .age(30)
                .team(teamA)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);


        List<MemberDto> members = memberRepository.findMemberDtoByTeamName("TeamA");
        for (MemberDto member : members) {
            System.out.println("===== memberDto : " + member.toString());
        }
    }

    @Test
    public void findByNames() {
        Member member1 = Member.builder()
                .username("member1")
                .age(20)
                .build();

        Member member2 = Member.builder()
                .username("member2")
                .age(30)
                .build();

        Member member3 = Member.builder()
                .username("member3")
                .age(20)
                .build();

        Member member4 = Member.builder()
                .username("member4")
                .age(30)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<Member> members = memberRepository.findByNames(Arrays.asList("member1, member2"));
        for (Member member : members) {
            System.out.println("========== member : " + member.toString());
        }
    }

    @Test
    public void returnTypeTest() throws Exception {
        Member member1 = Member.builder()
                .username("member1")
                .age(20)
                .build();

        Member member2 = Member.builder()
                .username("member2")
                .age(30)
                .build();

        Member member3 = Member.builder()
                .username("member3")
                .age(20)
                .build();

        Member member4 = Member.builder()
                .username("member4")
                .age(30)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        List<Member> findMembers = memberRepository.findListByUsername("member1");
        Member findMember = memberRepository.findMemberByUsername("member1");
        Optional<Member> optionalMember = memberRepository.findMemberOptionalByUsername("member1");

        System.out.println("findMembers : " + findMembers.toString());
        System.out.println("findMember : " + findMember.toString());
        System.out.println("optionalMember : " + optionalMember.orElseThrow(() -> new Exception("null")));

    }

    @Test
    public void paging() {
        // given
        memberRepository.save(Member.builder()
                .username("member1")
                .age(18)
                .build());
        memberRepository.save(Member.builder()
                .username("member2")
                .age(10)
                .build());
        memberRepository.save(Member.builder()
                .username("member3")
                .age(10)
                .build());
        memberRepository.save(Member.builder()
                .username("member4")
                .age(10)
                .build());
        memberRepository.save(Member.builder()
                .username("member5")
                .age(10)
                .build());
        memberRepository.save(Member.builder()
                .username("member6")
                .age(20)
                .build());

        int age = 10;
        // 스프링 데이터 JPA의 페이징은 0페이지부터 시작한다.
        // Pageable 인터페이스를 구현한 구현체로는 PageRequest를 보통 많이 사용한다.
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> members = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> memberDto = members.map(m -> new MemberDto(m.getMemberNo(), m.getUsername(), m.getTeam().getName()));

        //then
        List<Member> content = members.getContent(); // getContent() : 데이터를 꺼낸다.
        for (Member member : content) {
            System.out.println("member : " + member.toString());
        }
        System.out.println("totalElement : " + members.getTotalElements()); // 총 카운트
        System.out.println("totalPages : " + members.getTotalPages()); // 총 페이지 수
        System.out.println("count : " + members.stream().count()); // 현재 조회된 페이지의 데이터 수

        assertThat(content.size()).isEqualTo(3);
        assertThat(members.getTotalElements()).isEqualTo(4);
        assertThat(members.getNumber()).isEqualTo(0);
        assertThat(members.getTotalPages()).isEqualTo(2);
        assertThat(members.isFirst()).isTrue();
        assertThat(members.hasNext()).isTrue();
        assertThat(members.isLast()).isFalse();
    }

    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(Member.builder()
                .username("member1")
                .age(10)
                .build());
        memberRepository.save(Member.builder()
                .username("member2")
                .age(15)
                .build());
        memberRepository.save(Member.builder()
                .username("member3")
                .age(20)
                .build());
        memberRepository.save(Member.builder()
                .username("member4")
                .age(25)
                .build());
        memberRepository.save(Member.builder()
                .username("member5")
                .age(30)
                .build());
        memberRepository.save(Member.builder()
                .username("member6")
                .age(35)
                .build());

        // when
        int result = memberRepository.bulkAgePlus(20);
        em.flush();
        em.clear();

        // then
        assertThat(result).isEqualTo(4);
    }

    @Test
    public void findMemberLazy() {
        //given
        Team teamA = Team.builder()
                .name("teamA")
                .build();
        Team teamB = Team.builder()
                .name("teamB")
                .build();
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(Member.builder()
                .username("member1")
                .age(30)
                .team(teamA)
                .build());
        memberRepository.save(Member.builder()
                .username("member2")
                .age(35)
                .team(teamB)
                .build());

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member : " + member.getUsername());
            System.out.println("member.team.class : " + member.getTeam().getClass()); // Member만 가지고 온 시점에서 Team의 데이터를 사용하지 않는 시점에서는 프록시(가짜)객체를 넣어둔다.
            System.out.println("member.team :" + member.getTeam().getName()); // 실제 호출이 되는 시점에 쿼리를 실제 호출한다.
        }
        // @EntityGraph를 쓰면 JPQL없이도 fetchJoin을 할 수 있다.

    }

    @Test
    public void findMemberFetchJoin() {
        //given
        Team teamA = Team.builder()
                .name("teamA")
                .build();
        Team teamB = Team.builder()
                .name("teamB")
                .build();
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(Member.builder()
                .username("member1")
                .age(30)
                .team(teamA)
                .build());
        memberRepository.save(Member.builder()
                .username("member2")
                .age(35)
                .team(teamB)
                .build());

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member : " + member.getUsername());
            System.out.println("member.team.class : " + member.getTeam().getClass());
            System.out.println("member.team :" + member.getTeam().getName());
        }
    }

    @Test
    public void findMemberWithEntityGraph() {
        //given
        Team teamA = Team.builder()
                .name("teamA")
                .build();
        Team teamB = Team.builder()
                .name("teamB")
                .build();
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(Member.builder()
                .username("member1")
                .age(30)
                .team(teamA)
                .build());
        memberRepository.save(Member.builder()
                .username("member2")
                .age(35)
                .team(teamB)
                .build());

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findMemberWithEntityGraph();

        for (Member member : members) {
            System.out.println("member : " + member.getUsername());
            System.out.println("member.team.class : " + member.getTeam().getClass());
            System.out.println("member.team :" + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint() {
        memberRepository.save(Member.builder()
                .username("member2")
                .age(35)
                .build());
        em.flush();
        em.clear();

//        List<Member> findMembers = memberRepository.findByUsername("member2");
//        Member member = findMembers.get(0);
//        member.changeUsername("member2222");

        Member findMember = memberRepository.findReadOnlyByUsername("member2");
        findMember.changeUsername("member123123");

        em.flush();
    }

    @Test
    public void lock() {
        memberRepository.save(Member.builder()
                .username("member2")
                .age(35)
                .build());
        em.flush();
        em.clear();

        List<Member> members = memberRepository.findLockByUsername("member2");
        Member findMember = members.get(0);
    }

    @Test
    public void projections() {
        Team teamA = Team.builder()
                .name("teamA")
                .build();
        em.persist(teamA);

        Member userA = Member.builder()
                .username("userA")
                .age(10)
                .build();
        Member userB = Member.builder()
                .username("userB")
                .age(10)
                .build();
        em.persist(userA);
        em.persist(userB);

        em.flush();
        em.clear();

        // when
//        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("userA");
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("userA");
        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("userA", UsernameOnlyDto.class);

        // then
        for (UsernameOnlyDto usernameOnly : result) {
            System.out.println("usernameOnly : " + usernameOnly.getUsername());
        }
    }

    @Test
    public void NestedProjections() {
        Team teamA = Team.builder()
                .name("teamA")
                .build();
        em.persist(teamA);

        Member userA = Member.builder()
                .username("userA")
                .age(10)
                .team(teamA)
                .build();
        Member userB = Member.builder()
                .username("userB")
                .age(10)
                .build();
        em.persist(userA);
        em.persist(userB);

        em.flush();
        em.clear();

        // when
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("userA", NestedClosedProjections.class);

        // then
        for (NestedClosedProjections nestedClosedProjections : result) {
            System.out.println("nestedClosedProjections : " + nestedClosedProjections.getUsername() + ", " + nestedClosedProjections.getTeam().getName());
        }
    }

}
