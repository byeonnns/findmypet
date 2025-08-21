package com.findmypet.dataportal.api.model;

public record Kind(
        String kindCd,
        String kindNm,
        String upKindCd  // 축종 코드 (개:417000, 고양이:422400, 기타:429900)
) {}