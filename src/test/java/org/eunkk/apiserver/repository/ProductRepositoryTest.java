package org.eunkk.apiserver.repository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.domain.Product;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testInsert() {

        for(int i =0; i < 10; i++){

            Product product = Product.builder().pname("Test ").pdesc("Test Desc").price(1000).build();

            product.addImageString(UUID.randomUUID()+"_"+"IMAGE1.jpg");
            product.addImageString(UUID.randomUUID()+"_"+"IMAGE2.jpg");

            productRepository.save(product);
        }


    }


    @Transactional // join을 자기가 알아서 함
    @Test
    public void testRead() { // 쿼리 2번 나감 성능에 문제 있을 수 있음
        Long pno = 1L;

        Optional<Product> result = productRepository.findById(pno);

        Product product = result.orElseThrow();

        log.info(product);

        log.info(product.getImageList());

    }

    @Test
    public void testRead2() { // 쿼리 한번 나감(한번에 조인해서 나감)
        Long pno = 1L;

        Optional<Product> result = productRepository.selectOne(pno);

        Product product = result.orElseThrow();

        log.info(product);

        log.info(product.getImageList());

    }

    @Commit
    @Transactional
    @Test
    public void testDelete() {
        Long pno = 2L;

        productRepository.updateToDelete( 2L, true);
    }

    @Test
    public void testUpdate() { // 엘리먼트 컬렉션은 한번에 같이 변경되는 데이터를 쓰실 때 용이 아니면 매니트원은 양방향이라 어려움
        // 엘리먼트 컬렉션 장점 : 다뤄야하는 엔티티의 개수가 준다
        Product product = productRepository.selectOne(1L).get();

        product.changePrice(3000);

        product.clearList();

        product.addImageString(UUID.randomUUID()+"_"+"PIMAGE1.jpg");
        product.addImageString(UUID.randomUUID()+"_"+"PIMAGE2.jpg");
        product.addImageString(UUID.randomUUID()+"_"+"PIMAGE3.jpg");

        productRepository.save(product);
    }

    @Test
    public void testList() {
        Pageable pageable = PageRequest.of(0,10, Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        result.getContent().forEach(arr -> log.info(Arrays.toString(arr)));

    }

    @Test
    public void testSearch() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();

        productRepository.searchList(pageRequestDTO);
    }
}