package org.eunkk.apiserver.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.domain.Product;
import org.eunkk.apiserver.domain.QProduct;
import org.eunkk.apiserver.domain.QProductImage;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.eunkk.apiserver.dto.PageResponseDTO;
import org.eunkk.apiserver.dto.ProductDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Objects;

@Log4j2
public class ProductSearchImpl extends QuerydslRepositorySupport implements ProductSearch {

    public ProductSearchImpl() {
        super(Product.class);
    }


    @Override
    public PageResponseDTO<ProductDTO> searchList(PageRequestDTO pageRequestDTO) {

        log.info("------------------searchList------------------------------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() -1, // 0부터 시작하니깐
                pageRequestDTO.getSize(),
                Sort.by("pno").descending());

        QProduct product =QProduct.product;
        QProductImage productImage = QProductImage.productImage;

        //from 절까지 만듦
        JPQLQuery<Product> query = from(product);
        // product.imageList를 productImage로 간주한다
        query.leftJoin(product.imageList, productImage);

        query.where(productImage.ord.eq(0));

        Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);

        List<Product> productList = query.fetch();

        List<Tuple> productList2 = query.select(product, productImage).fetch();

        long count = query.fetchCount();

        log.info("============================================");
        log.info(productList);
        log.info("============================================");
        log.info(productList2);
        
        return null;
    }
}
