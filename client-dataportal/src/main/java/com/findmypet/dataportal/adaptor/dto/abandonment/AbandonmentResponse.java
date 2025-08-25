package com.findmypet.dataportal.adaptor.dto.abandonment;

import com.findmypet.dataportal.adaptor.dto.common.CommonResponse;
import com.findmypet.dataportal.adaptor.dto.common.Converters;
import com.findmypet.dataportal.api.model.Animal;
import com.findmypet.dataportal.api.model.PageResult;

/**
 * 공공데이터포털 abandonmentPublic_v2 응답 DTO
 */
public class AbandonmentResponse extends CommonResponse<AbandonmentResponse.Item> {

    public static class Item {
        public String desertionNo;
        public String popfile;       // 이미지 URL
        public String happenDt;
        public String happenPlace;
        public String kindCd;
        public String sexCd;
        public String neuterYn;
        public String age;
        public String weight;
        public String colorCd;
        public String noticeNo;
        public String noticeSdt;
        public String noticeEdt;
        public String processState;
        public String careNm;
        public String careTel;
        public String careAddr;
        public String orgNm;
        public String officetel;
        public String specialMark;
    }

    /** DTO → Domain 변환 */
    public PageResult<Animal> toDomain() {
        return Converters.toPage(this, items ->
                items.stream()
                        .map(i -> new Animal(
                                i.desertionNo,
                                i.popfile,        // imageUrl 매핑
                                i.happenDt,
                                i.happenPlace,
                                i.kindCd,
                                i.sexCd,
                                i.neuterYn,
                                i.age,
                                i.weight,
                                i.colorCd,
                                i.noticeNo,
                                i.noticeSdt,
                                i.noticeEdt,
                                i.processState,
                                i.careNm,
                                i.careTel,
                                i.careAddr,
                                i.orgNm,
                                i.officetel,
                                i.specialMark
                        ))
                        .toList()
        );
    }
}
