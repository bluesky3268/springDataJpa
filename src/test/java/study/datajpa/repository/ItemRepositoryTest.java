package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    /**
     * SimpleJpaRepository에서 isNew() : null일 경우 새로운 엔티티라고 판단함
     * @Id값을 @GeneratedValue를 사용하지 않고 직접 만들어서 사용하는 경우 Pk값이 이미 있기 떄문에 save()가 호출이 안됨
     *  -> merge()호출됨(DB에 select를 먼저 해서 값이 있는지 확인한 후에 없으면 새로운 엔티티로 판단하고 persist()를 진행함)
     * Persistable 인터페이스을 구현하여 사용할 수 있다.
     * 근데 실무에서 id값만 가지고 새로 만든 엔티티다 아니다를 판단하기 어렵다
     * 그래서 좋은 방법이 createdDate를 이용하는 것이다.
     *
     *
     */
    @Test
    void test() {
        Item item = new Item("itemA");
        itemRepository.save(item);
    }
}