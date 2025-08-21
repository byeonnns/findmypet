package com.findmypet.dataportal.adaptor.dto.abandonment;

import com.findmypet.dataportal.adaptor.dto.common.CommonResponse;
import com.findmypet.dataportal.adaptor.dto.common.Converters;
import com.findmypet.dataportal.api.model.Animal;
import com.findmypet.dataportal.api.model.PageResult;

import java.util.List;

public class AbandonmentResponse extends CommonResponse<AbandonmentResponse.Item> {

    public static class Item {
        public String desertionNo;
        public String filename;
        public String popfile;

        public String happenDt;
        public String happenPlace;

        public String kindCd;
        public String upKindCd;
        public String colorCd;
        public String age;
        public String weight;

        public String noticeNo;
        public String noticeSdt;
        public String noticeEdt;

        public String sexCd;
        public String neuterYn;
        public String specialMark;

        public String processState;

        public String careNm;
        public String careTel;
        public String careAddr;

        public String orgNm;
        public String officetel;
    }

    public PageResult<Animal> toDomainPage() {
        return Converters.toPage(this, items -> {
            if (items == null) return List.of();
            return items.stream().map(i ->
                    new Animal(
                            i.desertionNo, i.popfile, i.happenDt, i.happenPlace,
                            i.kindCd, i.upKindCd, i.sexCd, i.neuterYn,
                            i.age, i.weight, i.colorCd,
                            i.noticeNo, i.noticeSdt, i.noticeEdt, i.processState,
                            i.careNm, i.careTel, i.careAddr, i.orgNm, i.officetel,
                            i.specialMark
                    )
            ).toList();
        });
    }
}