package org.eunkk.apiserver.domain;


import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    private String fileName;

    private int ord;

    // 순번이 0인 애를 불러서 사용할려고
    public void setOrd(int ord){
        this.ord = ord;
    }
}
