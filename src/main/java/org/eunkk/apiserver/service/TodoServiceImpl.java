package org.eunkk.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.domain.Todo;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.eunkk.apiserver.dto.PageResponseDTO;
import org.eunkk.apiserver.dto.TodoDTO;
import org.eunkk.apiserver.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 실제 일을 하는 서비스 객체
// 트랜잭선 선언 3군데 1. 인터페이스 2. 클래스 3. 메소드
@Service
@Log4j2
@RequiredArgsConstructor // 생성자 자동 주입
public class TodoServiceImpl implements TodoService {

    // 자동 의존성 주입 대상은 final로
    private final TodoRepository todoRepository;

    // 조회
    @Override
    public TodoDTO get(Long tno) {

        Optional<Todo> result = todoRepository.findById(tno);
        
        // 문제가 생기면 끄집어내줘야 함
        Todo todo = result.orElseThrow();
        
        return entityToDTO(todo);
    }

    //생성
    @Override
    public Long register(TodoDTO dto) {

        Todo todo = dtoToEntity(dto);

        Todo result = todoRepository.save(todo);

        return result.getTno();
    }

    // 수정
    @Override
    public void modify(TodoDTO dto) {
        Optional<Todo> result = todoRepository.findById(dto.getTno());

        // 문제가 생기면 끄집어내줘야 함
        Todo todo = result.orElseThrow();

        todo.changeTitle(dto.getTitle());
        todo.changeContent(dto.getContent());
        todo.changeComplete(dto.isComplete());
        todo.changeDueDate(dto.getDueDate());

        todoRepository.save(todo);

    }

    // 삭제
    @Override
    public void remove(Long tno) {

        todoRepository.deleteById(tno);
    }

    @Override
    public PageResponseDTO<TodoDTO> getList(PageRequestDTO pageRequestDTO) {

        //JPA
        Page<Todo> result = todoRepository.search1(pageRequestDTO);

        //Todo List => TodoDTO List
        List<TodoDTO> dtoList = result
                .get()
                .map(todo -> entityToDTO(todo)).collect(Collectors.toList());

        PageResponseDTO<TodoDTO> responseDTO =
                PageResponseDTO.<TodoDTO>withAl()
                        .dtoList(dtoList)
                        .pageRequestDTO(pageRequestDTO)
                        .totalCount(result.getTotalElements())
                        .build();
        return responseDTO;
    }
}
