package com.findmypet.dataportal.api.model;

public record Shelter(
        String careRegNo, // 보호소 번호(없을 수도 있음)
        String name,      // careNm
        String orgCd,     // 시군구 코드
        String uprCd,     // 시도 코드
        String addr,      // careAddr
        String tel        // careTel
) {}