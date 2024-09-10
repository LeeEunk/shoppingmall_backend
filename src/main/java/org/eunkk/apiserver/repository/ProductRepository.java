package org.eunkk.apiserver.repository;

import org.eunkk.apiserver.domain.Product;
import org.eunkk.apiserver.repository.search.ProductSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// 동적처리 시, queryDSL 필요성 대두


public interface ProductRepository extends JpaRepository<Product, Long>, ProductSearch {

    @EntityGraph(attributePaths = "imageList") // 구체적인 엘리먼트 지정
    @Query("select p from Product p where p.pno = :pno") // 파라미터 앞에 : 하기
    Optional<Product> selectOne(@Param("pno") Long pno);

    @Modifying // update + delete 사용
    @Query("update Product p set p.delFlag = :delFlag where p.pno = :pno ")
    void updateToDelete(@Param("pno") Long pno, @Param("delFlag") boolean flag );

    //페이징 처리
    @Query("select p, pi from Product p left join p.imageList pi where pi.ord = 0 and p.delFlag = false ")
    Page<Object[]> selectList(Pageable pageable);


}
