package com.findmypet.dataportal.adaptor.client;

import com.findmypet.dataportal.adaptor.dto.kind.KindResponse;
import com.findmypet.dataportal.api.KindPort;
import com.findmypet.dataportal.api.model.Kind;
import com.findmypet.dataportal.core.ApiClient;
import com.findmypet.dataportal.core.EndpointSpecs;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KindClient implements KindPort {
    private final ApiClient api;

    public KindClient(ApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @Override
    public List<Kind> getKinds(String upKindCd) {
        var dto = api.get(EndpointSpecs.KIND, Map.of("up_kind_cd", upKindCd), KindResponse.class).block();
        return dto != null ? dto.toDomain() : List.of();
    }
}