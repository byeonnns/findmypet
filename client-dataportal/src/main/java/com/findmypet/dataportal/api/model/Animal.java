package com.findmypet.dataportal.api.model;

public record Animal(

        // 식별/미디어
        String desertionNo,   // 유기번호
        String imageUrl,      // popfile

        // 발생
        String happenDt,      // yyyyMMdd
        String happenPlace,

        // 분류
        String kindCd,        // 예: "[개] 말티즈"
        String sexCd,         // M/F/Q
        String neuterYn,      // Y/N/U
        String age,
        String weight,
        String colorCd,

        // 공고/처리
        String noticeNo,
        String noticeSdt,     // yyyyMMdd
        String noticeEdt,     // yyyyMMdd
        String processState,  // 공고중/보호중/종료 등

        // 보호 기관
        String careNm,
        String careTel,
        String careAddr,
        String orgNm,
        String officetel,

        // 기타
        String specialMark
) {}
