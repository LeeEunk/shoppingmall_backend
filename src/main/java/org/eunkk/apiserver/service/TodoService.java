package org.eunkk.apiserver.service;

import jakarta.transaction.Transactional;
import org.eunkk.apiserver.domain.Todo;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.eunkk.apiserver.dto.PageResponseDTO;
import org.eunkk.apiserver.dto.TodoDTO;

// interface는 모두 public
@Transactional
public interface TodoService {
    TodoDTO get(Long tno);

    // 오토 인크리먼트를 PK로 사용하는 경우, 값을 받기 위함
    // 생성
    Long register(TodoDTO dto);

    //수정
    void modify(TodoDTO dto);
    
    //삭제
    void remove(Long tno);

    PageResponseDTO<TodoDTO> getList(PageRequestDTO pageRequestDTO);


    // entity = database
    // 엔티티를 dto로 변환
    default TodoDTO entityToDTO(Todo todo){
        return  TodoDTO.builder()
                        .tno(todo.getTno())
                        .title(todo.getTitle())
                        .content(todo.getContent())
                        .complete(todo.isComplete())
                        .dueDate(todo.getDueDate())
                        .build();
    }

    // dto를 엔티티로 변환
    default Todo dtoToEntity(TodoDTO todoDTO){
        return  Todo.builder()
                .tno(todoDTO.getTno())
                .title(todoDTO.getTitle())
                .content(todoDTO.getContent())
                .complete(todoDTO.isComplete())
                .dueDate(todoDTO.getDueDate())
                .build();
    }
}
