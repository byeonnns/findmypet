package com.findmypet.domain.code;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 품종(Kind) 엔티티
 * <p>
 * - 공공데이터포털 API(kind)를 통해 조회한 동물 품종 정보를 저장한다.
 * - 예: 말티즈(417000 → 000054), 코숏(422400 → 000116)
 * - 상위 동물 구분(upKindCd: 개/고양이/기타)에 속한다.
 * - 간헐적으로 품종이 추가될 수 있으므로 정기적 업데이트가 필요.
 * - 7일에 1번 단위로 스케쥴러를 이용해 최신화하는 작업을 거칠 예정.
 */

@Entity
@Table(name = "kind")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Kind {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 상위 동물 코드 (개/고양이 등) */
    @Column(name = "up_kind_cd", length = 10, nullable = false)
    private String upKindCd;

    /** 품종 코드 */
    @Column(name = "kind_cd", length = 10, nullable = false, unique = true)
    private String kindCd;

    /** 품종명 (말티즈, 코숏 등) */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
}
