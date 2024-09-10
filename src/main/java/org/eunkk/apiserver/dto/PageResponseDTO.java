package org.eunkk.apiserver.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Data //getter, setter

public class PageResponseDTO<E> {


    // dto 목록 조회값
    private List<E> dtoList;
    
    // 페이징 처리
    private List<Integer> pageNumList;
    
    // 검색 조건 현재 페이지
    private PageRequestDTO pageRequestDTO;
    
    // 이전, 다음 페이지
    private boolean prev, next;

    // 현재 총 몇 페이지 조회값
    private int totalCount, prevPage, nextPage, totalPage, current;

    @Builder(builderMethodName = "withAl")
    public PageResponseDTO(List<E> dtoList, PageRequestDTO pageRequestDTO, long totalCount){
        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int)totalCount;

        // 끝페이지 end
        // Math.ceil = 올림
        int end = (int) (Math.ceil(pageRequestDTO.getPage() / 10.0)) * 10;

        int start = end - 9;

        // 진짜 마지막 페이지
        int last = (int) (Math.ceil(totalCount/(double)pageRequestDTO.getSize()));

        end = end > last ? last : end;

        this.prev = start > 1;
        this.next = totalCount > end * pageRequestDTO.getSize();

        this.pageNumList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

        this.prevPage = prev ? start - 1 : 0;

        this.nextPage = next ? end + 1 : 0;
    }
}
