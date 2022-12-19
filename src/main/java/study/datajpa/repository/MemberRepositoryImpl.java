package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

//@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    /**
     * List<Member> findByUsername(String username)를 사용하기 위해서
     * MemberRepository를 구현하면
     * JpaRepository, PagingAndSortRepository, CrudRepository의 메서드를
     * 다 구현해야 한다. -> 따로 커스텀해서 사용해야 함
     *
     * 근데 쿼리메서드 기능을 사용하면 메서드 이름으로 쿼리를 생성할 수도 있고
     * 커스텀해서 사용할 수도 있다.
     */

    /**
     * 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하게 되면 구현해야할 기능이 너무 많다..
     * 사용자 정의 리포지토리를 구현할 수 있게 해뒀다.
     * - JPA 직접 사용
     * - JDBC Template 사용
     * - MyBatis 사용
     * - 데이터 베이스 커넥션 직접 사용
     * - QueryDsl 사용
     *
     * 1. 사용자 정의 리포지토리 인터페이스를 만든다. (MemberRepositoryCustom)
     * 2. 내가 직접 구현할 메서드를 만든다.
     * 3. 사용자 정의 리포지토리를 구현할 클래스를 만들어 오버라이딩 해서 사용한다. (MemberRepositoryImpl)
     * 4. MemberRepository 인터페이스에 상속을 추가해준다(2번째에)
     * 단. 사용자 정의 리포지토리 인터페이스를 구현한 클래스의 이름은 기존 리포지토리 인터페이스 이름 + Impl
     * -> MemberRepository + Impl 이렇게 사용해야 한다.
     *
     * 싫다면??
     * xml의 경우
     * <repositories base-package="study.datajpa.repository" repository-impl-postfix="Impl"/>
     *
     * java config
     * @EnableJpaRepositories(basePackages="study.datajpa.repository", repositoryImplementationPostfix="Impl")
     */

    @PersistenceContext
    private final EntityManager em;

    public MemberRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
