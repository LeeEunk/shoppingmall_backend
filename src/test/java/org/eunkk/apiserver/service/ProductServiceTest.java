package org.eunkk.apiserver.service;

import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.eunkk.apiserver.dto.PageResponseDTO;
import org.eunkk.apiserver.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testList() {

        //page 없으면 그냥 리페이징
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();

        PageResponseDTO<ProductDTO> responseDTO = productService.getList(pageRequestDTO);

        log.info(responseDTO.getDtoList());
    }

    @Test
    public void testRegister() {
        ProductDTO productDTO = ProductDTO.builder()
                .pname("새로운 상품")
                .pdesc("신규 추가 상품입니다.")
                .price(1000)
                .build();

        // uuid가 있어야 함
        productDTO.setUploadedFileNames(
                java.util.List.of(
                        UUID.randomUUID()+"_"+"Test1.jpg",
                        UUID.randomUUID()+"_"+"Test2.jpg"
                )
        );

        productService.register(productDTO);
    }
}