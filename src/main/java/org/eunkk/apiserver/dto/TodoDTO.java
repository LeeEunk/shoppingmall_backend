package org.eunkk.apiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eunkk.apiserver.domain.Todo;

import java.time.LocalDate;

// DTO는 아무렇게나 만들어도 됨, Bean 등록이 별도로 필요 없음
// entity - JPA와 연관
// 가끔 DTO -> entity 혹은 그 반대의 상황도 생김
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // json 변환하려면 비어있는 생성자도 필요하므로

public class TodoDTO {

    private Long tno;

    private String title;

    private String content;

    private boolean complete;

    private LocalDate dueDate;

}
