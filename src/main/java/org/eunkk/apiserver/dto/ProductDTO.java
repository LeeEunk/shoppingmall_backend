package org.eunkk.apiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

//상품 등록/조회
// 데이터베이스의 성능을 위해 파일을 직접 넣지 않고 파일 경로 이름만 저장함
public class ProductDTO {

    private Long pno;

    private String pname;

    private int price;

    private String pdesc;
    
    // 외래키로 인해 관련 모든 구매 기록이 지워질까봐 이렇게 적용
    // 실제 삭제가 아닌 삭제 플래그 삭제 컬럼을 만들어서 그 값만 업데이트 -> 소프트 딜리트
    private boolean delFalg;

    // upload 전, null check annotaion 적용
    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>(); //업데이트때 가져오는 파일 데이터명
    // upload 후
    @Builder.Default
    private List<String> uploadedFileNames = new ArrayList<>(); // 데이터베이스에 있는 이미지 파일
}
