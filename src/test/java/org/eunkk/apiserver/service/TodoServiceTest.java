package org.eunkk.apiserver.service;

import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.eunkk.apiserver.dto.TodoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
@Log4j2
class TodoServiceTest {

    @Autowired // 자동 AOP
    TodoService todoService;

    @Test
    public void testGet() {
        Long tno = 50L;

        log.info(todoService.get(tno));
    }

    @Test
    public void testRegister() {
        TodoDTO todoDTO = TodoDTO.builder()
                .title("Title...")
                .content("Content.....")
                .dueDate(LocalDate.of(2024,12,31))
                .build();

        log.info(todoService.register(todoDTO));
    }

    @Test
    public void testGetList() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(11).build();

        log.info(todoService.getList(pageRequestDTO));

    }
}