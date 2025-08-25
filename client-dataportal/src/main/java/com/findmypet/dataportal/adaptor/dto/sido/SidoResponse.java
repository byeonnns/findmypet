package com.findmypet.dataportal.adaptor.dto.sido;

import com.findmypet.dataportal.adaptor.dto.common.CommonResponse;
import com.findmypet.dataportal.adaptor.dto.common.Converters;
import com.findmypet.dataportal.api.model.Sido;

import java.util.List;

public class SidoResponse extends CommonResponse<SidoResponse.Item> {

    public static class Item {
        public String orgCd;
        public String orgdownNm;
    }

    public List<Sido> toDomain() {
        return Converters.toList(this, items ->
                items.stream()
                        .map(i -> new Sido(i.orgCd, i.orgdownNm))
                        .toList()
        );
    }
}
