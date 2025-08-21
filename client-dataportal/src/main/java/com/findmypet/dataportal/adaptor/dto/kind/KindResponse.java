package com.findmypet.dataportal.adaptor.dto.kind;

import com.findmypet.dataportal.adaptor.dto.common.CommonResponse;
import com.findmypet.dataportal.api.model.Kind;

import java.util.List;

public class KindResponse extends CommonResponse<KindResponse.Item> {

    public static class Item {
        public String kindCd;
        public String kindNm;
        public String upKindCd;
    }

    public List<Kind> toDomain() {
        var list = this.response.body.items.list;
        return list == null ? List.of() :
                list.stream().map(i -> new Kind(i.kindCd, i.kindNm, i.upKindCd)).toList();
    }
}