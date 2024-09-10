package org.eunkk.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.domain.Product;
import org.eunkk.apiserver.dto.PageRequestDTO;
import org.eunkk.apiserver.dto.PageResponseDTO;
import org.eunkk.apiserver.dto.ProductDTO;
import org.eunkk.apiserver.service.ProductService;
import org.eunkk.apiserver.utill.CustomFileUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController // Api server
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    // 외부 생성했을 때 자동 주입할 수 있게 final로 설정
    private final CustomFileUtil fileUtil;

    private final ProductService productService;

//    @PostMapping("/")
//    public Map<String, String> register(ProductDTO productDTO) {
//
//        log.info("register: " + productDTO);
//
//        List<MultipartFile> files = productDTO.getFiles();
//
//        List<String> uploadedFileNames = fileUtil.saveFiles(files);
//
//        productDTO.setUploadedFileNames(uploadedFileNames);
//
//        log.info(uploadedFileNames);
//
//        return Map.of("RESULT", "SUCCESS");
//    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET (@PathVariable("fileName") String fileName) {

        return fileUtil.getFile(fileName);

    }

    @PreAuthorize("hasAnyRole('ROLE_USER',)")
    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {

        log.info("list................" + pageRequestDTO);

        try{
            Thread.sleep(2000); //상품 데이터 2초 후에 전달
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return productService.getList(pageRequestDTO);

    }

    @PostMapping("/")
    public Map<String, Long> register(ProductDTO productDTO){
        List<MultipartFile> files = productDTO.getFiles();

        List<String> uploadFileNames = fileUtil.saveFiles(files);

        productDTO.setUploadedFileNames(uploadFileNames);

        log.info(uploadFileNames);

        Long pno = productService.register(productDTO);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Map.of("result", pno);
    }
    
    // 상품 조회
    @GetMapping("/{pno}")
    public ProductDTO read(@PathVariable("pno") Long pno){
        try{
            Thread.sleep(2000); //상품 데이터 2초 후에 전달
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return productService.get(pno);
    }

    // 상픔 수정
    @PutMapping("/{pno}")
    public Map<String,String> modify(@PathVariable("pno") Long pno, ProductDTO productDTO) {

        // pno 값 고정
        productDTO.setPno(pno);

        //old product Database saved Product 값 가져오기 -> 어떤 파일들이 지워졌나 로그 확인용
        ProductDTO oldProductDTO = productService.get(pno);

        // file uplaod -> 새로 업로드 해야 하는 파일들
        List<MultipartFile> files = productDTO.getFiles();
        
        //새로 업로드되어서 만들어진 파일 이름들
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);

        // keep files String -> 이미 유지되고 있는 기존 파일들 => 유지되어야 함
        List<String> uploadedFileNames = productDTO.getUploadedFileNames();

        // 유지되는 파일들 + 새로 업로드된 파일 이름들이 저장해야 하는 파일 목록이 됨
        if(currentUploadFileNames != null && !currentUploadFileNames.isEmpty()){
            uploadedFileNames.addAll(currentUploadFileNames);
        }

        // 업로드 저장 (기존 이미지 교체하므로) -> 수정 작업
        productService.modify(productDTO);
        
        // 기존 저장되고 데이터베이스에도 존재 -> 지워야 함, 수정 과정에서 삭제되었을 수 있음
        List<String> oldFileNames = oldProductDTO.getUploadedFileNames();
        
        if(oldFileNames != null && oldFileNames.size() > 0){
            // 사라져버린 파일들, 예전 파일들 중에서 지워져야 하는 파일 이름들
            // 지워야 하는 파일 목록 찾기
            List<String> removeFiles = oldFileNames
                    .stream()
                    .filter(fileName -> uploadedFileNames.indexOf(fileName) == -1).collect(Collectors.toList());
        
            // 파일 삭제
            fileUtil.deleteFiles(removeFiles);
        }// end if


        
        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{pno}")
    public Map<String, String> remove(@PathVariable Long pno){

        List<String> oldFileNames = productService.get(pno).getUploadedFileNames();

        productService.remove(pno);

        fileUtil.deleteFiles(oldFileNames);

        return Map.of("RESULT", "SUCCESS");
    }
}
