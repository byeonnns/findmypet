package com.findmypet.dataportal.api.model;

public record Animal(
        // 식별/미디어
        String desertionNo,
        String imageUrl, // popfile (대표 이미지)

        // 발생
        String happenDt, // yyyyMMdd
        String happenPlace,

        // 분류
        String kindCd, // ex) "[개] 말티즈"
        String upKindCd, // 417000/422400/429900
        String sexCd, // M/F/Q(미상)
        String neuterYn, // Y/N/U
        String age,
        String weight,
        String colorCd,

        // 공고/처리
        String noticeNo,
        String noticeSdt, // 공고 시작일 yyyyMMdd
        String noticeEdt, // 공고 종료일 yyyyMMdd
        String processState, // 상태

        // 보호 기관
        String careNm,
        String careTel,
        String careAddr,
        String orgNm,
        String officetel,

        // 기타
        String specialMark
) {}