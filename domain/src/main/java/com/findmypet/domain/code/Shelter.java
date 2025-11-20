package com.findmypet.domain.code;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 보호소(Shelter) 엔티티
 * - 공공데이터포털 API(shelter)를 통해 조회한 동물 보호소 정보를 저장한다.
 * - 예: 서울동물보호센터, 수원유기동물보호소
 * - 시군구(orgCd)를 기준으로 소속 관계를 갖는다.
 * - 보호소는 신설/폐쇄될 수 있으므로 갱신이 필요해질 수 있다.
 * - 7일에 1번 단위로 스케쥴러를 이용해 최신화하는 작업을 거칠 예정.
 */

@Entity
@Table(name = "shelter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 소속 시군구 코드 */
    @Column(name = "org_cd", length = 10, nullable = false)
    private String orgCd;

    /** 보호소 등록번호 */
    @Column(name = "care_reg_no", length = 20, nullable = false, unique = true)
    private String careRegNo;

    /** 보호소 이름 */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /** 주소 */
    @Column(name = "address", length = 255)
    private String address;

    /** 전화번호 */
    @Column(name = "tel", length = 50)
    private String tel;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateInfo(String name, String address, String tel) {
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.updatedAt = LocalDateTime.now();
    }
}
