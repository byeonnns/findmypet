package com.findmypet.dataportal.adaptor.client;

import com.findmypet.dataportal.adaptor.dto.sido.SidoResponse;
import com.findmypet.dataportal.adaptor.dto.sigungu.SigunguResponse;
import com.findmypet.dataportal.api.LocalCodePort;
import com.findmypet.dataportal.api.model.Sido;
import com.findmypet.dataportal.api.model.Sigungu;
import com.findmypet.dataportal.core.ApiClient;
import com.findmypet.dataportal.core.EndpointSpecs;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LocalCodeClient implements LocalCodePort {
    private final ApiClient api;

    public LocalCodeClient(ApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @Override
    public List<Sido> getSido() {
        var dto = api.get(EndpointSpecs.SIDO, Map.of(), SidoResponse.class).block();
        return dto != null ? dto.toDomain() : List.of();
    }

    @Override
    public List<Sigungu> getSigungu(String uprCd) {
        var dto = api.get(EndpointSpecs.SIGUNGU, Map.of("upr_cd", uprCd), SigunguResponse.class).block();
        return dto != null ? dto.toDomain() : List.of();
    }
}