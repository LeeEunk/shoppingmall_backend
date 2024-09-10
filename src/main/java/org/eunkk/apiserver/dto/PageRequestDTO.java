package org.eunkk.apiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    // 페이지 번호
    @Builder.Default
    private int page = 1;

    // 페이지 사이즈
    @Builder.Default
    private int size = 10;

}
