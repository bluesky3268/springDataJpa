package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

// JpaRepository를 상속받으면 Spring data jpa가 구현체를 만들어서 주입해준다.
// @Repository도 생략가능

/**
 * 스프링 데이터 JPA의 구현체는 SimpleJpaRepository이다
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    /**
     * spring-data-commons : 공통적인 기능들을 제공(spring data redis, spring data jpa ... 에서 상속한다)
     *  PagingAndSortingRepository -> CrudRepository -> Repository
     *
     * spring-data-jpa : JPA에 특화된 공통 기능들을 제공
     *  JpaRepository -> PagingAndSortingRepository
     */

    /**
     * 제네릭 타입
     * T : 엔티티
     * ID : 엔티티의 식별자 타입
     * S : 엔티티와 그 자식 타입
     */

    List<Member> findByUsername(String username);

    /**
     * 메서드 명명 규칙은
     * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
     * 여기 참고
     *
     *  + 간단한 쿼리만 사용하는 게 좋다.
     *  + 필드명이 바뀌면 메서드명도 필드명과 동일하게 바꿔줘야 한다.
     *
     *  복잡해지면 @Query를 사용해서 직접 정의해서 사용하면 된다.(동적쿼리는 QueryDSL을 사용하는게 깔끔하고 유지보수에도 좋다.)
     */
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3ByOrderByAgeDesc();

    @Query("select m from Member m where m.username = :username and m.age = :age")
    // @Query(Named Query)를 사용할 때 오타가 나면 컴파일 에러가 난다는 장점이 있다.
    List<Member> findMember(@Param("username") String username, @Param("age") int age);

    // 위의 findMember는 엔티티를 조회하는 것이였는데
    // 간단한 값이나 DTO는 어떻게 조회를 할까??

    // 간단한 유저명만 조회
    @Query("select m.username from Member m")
    List<String> findUsernames();

    // dto로 조회(new를 꼭 써야 한다. 패키지명도 다 적어줘야 함)
    @Query("select new study.datajpa.dto.MemberDto(m.memberNo, m.username, t.name) from Member m left outer join m.team t where m.team.name = :teamName")
    List<MemberDto> findMemberDtoByTeamName(@Param("teamName") String teamName);

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    /**
     * 반환타입은 유연하게 아무거나 사용할 수 있다.
     */
    List<Member> findListByUsername(String username); // Collection(컬렉션은 null 체크 안해도 됨, 빈 컬렉션을 반환하기 때문에)

    Member findMemberByUsername(String username); // 단건 조회 (데이터가 있을 지 없을 지 몰라서 null값을 걱정해야 한다면 Optional을 쓰자...)

    Optional<Member> findMemberOptionalByUsername(String username); //단건 조회(Optional)

    /**
     * 스프링 데이터 JPA를 이용한 페이징과 정렬
     * org.springframework.data.domain.Sort
     * org.springframework.data.domain.Pageable 를 사용하여 페이징과 정렬을 한다.
     * <p>
     * 특별한 반환 타입
     * org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
     * org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능
     *  -> 더보기 기능을 사용할 때 주로 사용한다. (내부적으로 limit + 1해서 조회한다)
     * List : 추가 count 쿼리 없이 결과만 반환
     */

    // 카운트 쿼리를 따로 써야 할 때가 있다. 대신 쿼리를 짜야 한다
    @Query(value = "select m from Member m left join m.team t where m.age = :age", countQuery = "select count(m.username) from Member m where m.age = :age")
    Page<Member> findByAge(@Param("age")int age, Pageable pageable);
//    Slice<Member> findByAge(int age, Pageable pageable);
//    List<Member> findByAge(int age, Pageable pageable);

    /**
     * 이런 벌크성 쿼리를 사용할 때는 영속성 컨텍스트를 잘 고려해야 한다.
     * 쿼리가 나가면서 DB의 데이터는 변경되었지만 영속성 컨텍스트는 변경된 걸 모른다.
     * 그렇기 때문에 벌크성 쿼리를 사용하고 나면 영속성 컨텍스트를 다 날려줘야 한다.
     * 참고. JPQL이 실행되기 전에 모든 쿼리를 flush를 하고 JPQL이 실행된다.
     * em.flush()
     * em.clear()
     * 근데 스프링 데이터 JPA에서는 @Modifying에 옵션을 넣어주면 굳이 내가 flush(), clear()를 하지 않아도 된다.
     */
    @Modifying(clearAutomatically = true) // 순수 JPA에서 executeUpdate와 같은 역할을 한다. 이 어노테이션을 안붙여주면 getResultList 혹은 getSingleResult를 호출하게 된다
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * MemberRepositoryTest에서 findMemberLazy()를 봤을 때
     * 지연로딩을 안쓰면 N+1 문제가 발생할 수도 있다.
     * 왜냐.
     * 나는 Member객체 하나를 조회했는데 거기에 연관된 부가정보들 때문에 쿼리가 여러번 더 나갈 수 있다.
     * -> 네트워크를 여러번 타기 떄문에 성능에 안좋다.
     * -> JPA에서는 이걸 fetchJoin으로 해결한다.
     * fetchJoin을 하면 Member를 조회할 떄 같이 연관된 애들을 한방 쿼리로 한번에 싹 다 가져온다.
     * fetchJoin의 기본은 left outer join
     */
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    /**
     * 근데 fetchJoin을 할 때마다 JPQL을 사용하기 너무 귀찮기도 하고
     * 메서드 이름으로(findByUsername)처럼 간단하게 조회하는데 fetchJoin까지 하고 싶을 때를 위해서
     * 스프링 데이터 JPA는 EntityGraph라는 걸 제공한다.
     * 예) findAll();
     * findAll()을 오버라이드 한 다음에 @EntityGraph를 붙여주면 된다.
     */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @Query("select m from Member m")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberWithEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);


    /**
     * JPA힌트 : SQL힌트가 아니라 JPA구현체인 하이버네이트에게 알려주는 힌트
     * -> readOnly옵션이 true인 경우에는 스냅샷이 없고 변경감지 체크를 하지 않기 때문에
     * 쿼리로 가져온 데이터를 딱히 수정할 필요가 없는 경우에는 성능최적화에 도움이 된다.
     * 근데 딱히..?
     */
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value="true"))
    Member findReadOnlyByUsername(String username);

    /**
     * 락
     * SELECT FOR UPATE -> 비관적 락 : 실제로 테이블에 락을 거는 방법
     * 낙관적 락 : 실제로 테이블에 락을 걸지 않고 version을 이용하는 방법
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    /**
     * Projection
     */
//    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);
//    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);
    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    /**
     * 네이티브 쿼리 => 안쓰는 게 좋다
     * 반환타입 지원이 몇가지 안됨(Object[], tuple, dto(스피링 데이터 인터페이스 Projection지원))
     * 동적 쿼리 불가
     * JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가
     *
     * -> 네이티브 SQL을 이용하여 DTO를 조회할 때는 Mybatis나 JDBC Template, jooq를 사용하는 것이 좋다.
     */
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    // 네이티브 SQL을 이용하여 DTO를 조회하고 싶은데 동적쿼리는 아닐 때 사용하면 좋음
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "       from Member m" +
            "       left join team t"
            , countQuery = "select count(*) from member"
            , nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
