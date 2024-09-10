package org.eunkk.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.eunkk.apiserver.dto.PageResponseDTO;
import org.eunkk.apiserver.dto.TodoDTO;
import org.eunkk.apiserver.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 화면이 없기 때문에 api 역할만 json으로 보여짐
//  restcontrolleradvice -> 경로 오류 시, 예외 처리 페이지로 이동할 때 적용
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoControlloer {

    private final TodoService todoService;

    @GetMapping("/{tno}")
    public TodoDTO get(@PathVariable("tno") Long tno) {
        return todoService.get(tno);
    }

    // 조회
    @GetMapping("/list")
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO){
        log.info("list..............." + pageRequestDTO);

        return todoService.getList(pageRequestDTO);
        
    }

    // 등록
    @PostMapping("/")
    public Map<String, Long> register(@RequestBody TodoDTO dto) {

        log.info("todoDTO" + dto);

        Long tno = todoService.register(dto);

        return Map.of("TNO", tno);
    }

    // 수정
    @PutMapping("/{tno}")
    public Map<String, String> modify(@PathVariable("tno") Long tno,
                                      @RequestBody TodoDTO todoDTO){

        todoDTO.setTno(tno);
        todoService.modify(todoDTO);

        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{tno}")
    public Map<String, String> remove(@PathVariable("tno") Long tno){

        todoService.remove(tno);

        return Map.of("RESULT", "SUCCESS");
    }
}
