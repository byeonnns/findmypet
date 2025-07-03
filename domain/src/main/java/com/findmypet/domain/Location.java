package com.findmypet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Location {

    @Id
    @Column(name = "code", length = 10)
    private String code; // API의 ‘orgCd’ (시군구 코드)

    @Column(nullable = false, length = 20)
    private String sidoName; // API의 ‘uprCdNm’ (시도명)

    @Column(nullable = false, length = 30)
    private String sigunguName; // API의 ‘orgdownNm’ (시군구명)

}
