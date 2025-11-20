package com.findmypet.domain.code;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 시군구(Sigungu) 엔티티
 * - 공공데이터포털 API(sigungu)를 통해 조회한 시/도 하위 행정구역 정보를 저장한다.
 * - 예: 강남구(3220000), 수원시(4111000)
 * - 상위 시도(Sido) 코드(uprCd)를 참조하며, 변동 주기가 매우 낮다.
 * - 7일에 1번 단위로 스케쥴러를 이용해 최신화하는 작업을 거칠 예정.
 */

@Entity
@Table(name = "sigungu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sigungu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 상위 시도 코드 */
    @Column(name = "upr_cd", length = 10, nullable = false)
    private String uprCd;

    /** 시군구 코드 */
    @Column(name = "org_cd", length = 10, nullable = false, unique = true)
    private String orgCd;

    /** 시군구 이름 */
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
}
