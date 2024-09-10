package org.eunkk.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.domain.Product;
import org.eunkk.apiserver.domain.ProductImage;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.eunkk.apiserver.dto.PageResponseDTO;
import org.eunkk.apiserver.dto.ProductDTO;
import org.eunkk.apiserver.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage()-1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        //Object[] => 0 product, 1 productImage
        // 상품 하나에 들어 있는 이미지와 상품정보들을 맵으로 변경
        List<ProductDTO> dtoList = result.get().map(arr -> {
            ProductDTO productDTO = null;

            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];

            productDTO = ProductDTO.builder()
                    .pno(product.getPno())
                    .pname(product.getPname())
                    .pdesc(product.getPdesc())
                    .price(product.getPrice())
                    .build();

            String imageStr = productImage.getFileName();
            productDTO.setUploadedFileNames(List.of(imageStr));

            return productDTO;
        }).collect(Collectors.toList());
        
        long totalCount = result.getTotalElements();

        return PageResponseDTO.<ProductDTO>withAl()
                .dtoList(dtoList)
                .totalCount(totalCount)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    @Override
    public Long register(ProductDTO productDTO) {

        Product product = dtoToEntity(productDTO);

        log.info("----------------------");
        log.info(product);
        log.info(product.getImageList());

        Long pno = productRepository.save(product).getPno();

        return pno;
    }

    @Override
    public ProductDTO get(Long pno) {

        Optional<Product> result = productRepository.findById(pno);

        // 문제 있으면 던지기
        Product product = result.orElseThrow();

        return entityToDTO(product);
    }

    @Override
    public void modify(ProductDTO productDTO) {
        // 조회
        Optional<Product> result = productRepository.findById(productDTO.getPno());

        Product product = result.orElseThrow();

        // 변경 내용 반영
        product.changePrice(productDTO.getPrice());
        product.changeName(productDTO.getPname());
        product.changeDesc(productDTO.getPdesc());
        product.changeDel(productDTO.isDelFalg());

        // upload File -- clear first
        product.clearList();

        // 이미지 처리 -> 목록을 먼저 비워야 함
        List<String> uploadFileNames = productDTO.getUploadedFileNames();

        if(uploadFileNames != null && !uploadFileNames.isEmpty()){
            uploadFileNames.stream().forEach(uploadName -> {
                product.addImageString(uploadName);
            });
        }

        // 저장
        productRepository.save(product);
    }

    // 원래는 삭제는 없다. delete flag만 삭제해서 로그용으로 남기기
    @Override
    public void remove(Long pno) {

        productRepository.deleteById(pno);
    }

    private ProductDTO entityToDTO(Product product){

        ProductDTO productDTO = ProductDTO.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .pdesc(product.getPdesc())
                .price(product.getPrice())
                .delFalg(product.isDelFlag())
                .build();

        List<ProductImage> imageList = product.getImageList();

        if(imageList == null || imageList.isEmpty()){
            return productDTO;
        }

        // 상품의 이미지를 문자열로 변경해줌, 번호 노필요
        List<String> fileNameList = imageList.stream().map(productImage ->
                productImage.getFileName()).toList();

        productDTO.setUploadedFileNames(fileNameList);

        return productDTO;
    }

    private Product dtoToEntity(ProductDTO productDTO){

        Product product = Product.builder()
                .pno(productDTO.getPno())
                .pname(productDTO.getPname())
                .pdesc(productDTO.getPdesc())
                .price(productDTO.getPrice())
                .build();

        List<String> uploadFileNames = productDTO.getUploadedFileNames();

        if(uploadFileNames == null || uploadFileNames.size() == 0){
            return product;
        }

        uploadFileNames.forEach(fileName -> {
            product.addImageString(fileName);
        });

        return product;
    }
}
