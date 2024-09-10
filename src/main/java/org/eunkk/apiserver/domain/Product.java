package org.eunkk.apiserver.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tbl_product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageList") // 이미지 조회 제외 -> 트랜잭션 불필요
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    private String pname;

    private int price;

    private String pdesc;

    private boolean delFlag;

    @ElementCollection
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    public void changePrice(int price){
        this.price = price;
    }

    public void changeDesc(String desc) {
        this.pdesc = desc;
    }

    public void changeName(String name) {
        this.pname = name;
    }

    public void changeDel(boolean delFlag){
        this.delFlag = delFlag;
    }
    
    //이미지 순번 자동화 -> 트랜잭션 용이
    public void addImage(ProductImage image) {
        image.setOrd(imageList.size());
        imageList.add(image);

    }

    // db 저장하는 방법 1. 직접 파일 처리 2. 문자열 처리
    // -> 문자열 처리, 수정하기에도 용이
    public void addImageString(String fileName) {
        ProductImage productImage = ProductImage.builder()
                .fileName(fileName)
                .build();

        addImage(productImage);
    }

    //객체 지향 코드가 디비에 반영되는 게 jpa
    public void clearList(){
        this.imageList.clear();
    }
}
