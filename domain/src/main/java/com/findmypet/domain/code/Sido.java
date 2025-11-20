package com.findmypet.domain.code;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 시도(Sido) 엔티티
 * - 공공데이터포털 API(sido)를 통해 조회한 전국 시/도 정보를 저장한다.
 * - 예: 서울특별시(6110000), 경기도(6410000)
 * - 시군구(Sigungu)의 상위 행정 단위이며, 변동 주기가 매우 낮다.
 * - 7일에 1번 단위로 스케쥴러를 이용해 최신화하는 작업을 거칠 예정.
**/
@Entity
@Table(name = "sido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 공공데이터포털 상의 시도 코드 */
    @Column(name = "upr_cd", length = 10, nullable = false, unique = true)
    private String uprCd;

    /** 시도 이름 (서울특별시, 경기도 등) */
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
}
