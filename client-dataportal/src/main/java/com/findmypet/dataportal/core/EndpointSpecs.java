package com.findmypet.dataportal.core;

import java.util.Set;

public class EndpointSpecs {
    private EndpointSpecs() {}

    /** 공공데이터포털 Base Path (호스트는 DataPortalProperties.baseUrl) */
    public static final String BASE_PATH = "/1543061/abandonmentPublicService_v2";

    /** /abandonmentPublic_v2 — 구조동물 조회
     *  필수: serviceKey(자동 주입)만. 나머지 검색조건은 선택.
     */
    public static final EndpointSpec ABANDONMENT = new EndpointSpec(BASE_PATH + "/abandonmentPublic_v2", Set.of());

    /** /sigungu_v2 — 시군구 조회
     *  필수: upr_cd
     */
    public static final EndpointSpec SIGUNGU = new EndpointSpec(BASE_PATH + "/sigungu_v2", Set.of("upr_cd"));

    /** /sido_v2 — 시도 조회
     *  필수: (없음)  // serviceKey 자동 주입
     */
    public static final EndpointSpec SIDO = new EndpointSpec(BASE_PATH + "/sido_v2", Set.of());

    /** /shelter_v2 — 보호소 조회
     *  필수: upr_cd, org_cd
     */
    public static final EndpointSpec SHELTER = new EndpointSpec(BASE_PATH + "/shelter_v2", Set.of("upr_cd", "org_cd"));

    /** /kind_v2 — 품종 조회
     *  필수: up_kind_cd (개:417000, 고양이:422400, 기타:429900)
     */
    public static final EndpointSpec KIND = new EndpointSpec(BASE_PATH + "/kind_v2", Set.of("up_kind_cd"));
}
