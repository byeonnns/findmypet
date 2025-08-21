package com.findmypet.dataportal.api.model;

public record Sigungu(
        String orgCd,
        String orgdownNm,
        String uprCd     // 상위 시도 코드
) {}