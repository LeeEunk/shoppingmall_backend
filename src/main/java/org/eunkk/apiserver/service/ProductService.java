package org.eunkk.apiserver.service;

import org.eunkk.apiserver.dto.PageRequestDTO;
import org.eunkk.apiserver.dto.PageResponseDTO;
import org.eunkk.apiserver.dto.ProductDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProductService {

    // 목록 조회
    PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO);

    // 상품 등록
    Long register(ProductDTO productDTO);

    // 상품 조회
    ProductDTO get(Long pno);

    // 상품 수정
    void modify(ProductDTO productDTO);
    
    // 상품 삭제 -> Entity 종속 적이기 때문에 뭉터이로 삭제됨
    void remove(Long pno);
}
