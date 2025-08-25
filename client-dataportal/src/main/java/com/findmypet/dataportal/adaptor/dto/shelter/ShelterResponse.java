package com.findmypet.dataportal.adaptor.dto.shelter;

import com.findmypet.dataportal.adaptor.dto.common.CommonResponse;
import com.findmypet.dataportal.adaptor.dto.common.Converters;
import com.findmypet.dataportal.api.model.Shelter;

import java.util.List;

public class ShelterResponse extends CommonResponse<ShelterResponse.Item> {

    public static class Item {
        public String careRegNo;
        public String careNm;
        public String orgCd;
        public String uprCd;
        public String careAddr;
        public String careTel;
    }

    public List<Shelter> toDomain() {
        return Converters.toList(this, items ->
                items.stream()
                        .map(i -> new Shelter(i.careRegNo, i.careNm, i.orgCd, i.uprCd, i.careAddr, i.careTel))
                        .toList()
        );
    }
}