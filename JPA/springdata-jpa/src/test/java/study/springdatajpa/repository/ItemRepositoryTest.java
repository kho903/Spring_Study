package study.springdatajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.springdatajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void saveTest() throws Exception {
        // given
        Item item = new Item("A");
        System.out.println("item.getId() = " + item.getId()); // null
        itemRepository.save(item);

        // when

        // then
    }
}