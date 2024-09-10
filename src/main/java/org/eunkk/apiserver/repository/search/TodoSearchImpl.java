package org.eunkk.apiserver.repository.search;

import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.domain.QTodo;
import org.eunkk.apiserver.domain.Todo;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch{

    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<Todo> search1(PageRequestDTO pageRequestDTO) {

        log.info("search1.................");

        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = from(todo);

//        query.where(todo.title.contains("1"));


        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("tno").descending());

        this.getQuerydsl().applyPagination(pageable, query);



//      쿼리 실행
        List<Todo> list = query.fetch(); // 목록 데이터 조회
        long total = query.fetchCount(); // long타입으로 출력됨
        return new PageImpl<>(list, pageable, total);
    }
}

