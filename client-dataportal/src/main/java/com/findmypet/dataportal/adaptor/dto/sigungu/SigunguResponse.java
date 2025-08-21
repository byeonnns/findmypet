package com.findmypet.dataportal.adaptor.dto.sigungu;

import com.findmypet.dataportal.adaptor.dto.common.CommonResponse;
import com.findmypet.dataportal.api.model.Sigungu;

import java.util.List;

public class SigunguResponse extends CommonResponse<SigunguResponse.Item> {

    public static class Item {
        public String orgCd;
        public String orgdownNm;
        public String uprCd;
    }

    public List<Sigungu> toDomain() {
        var list = this.response.body.items.list;
        return list == null ? List.of() :
                list.stream().map(i -> new Sigungu(i.orgCd, i.orgdownNm, i.uprCd)).toList();
    }
}
