package com.findmypet.dataportal.adaptor.client;

import com.findmypet.dataportal.adaptor.dto.abandonment.AbandonmentResponse;
import com.findmypet.dataportal.api.AbandonmentPort;
import com.findmypet.dataportal.api.model.Animal;
import com.findmypet.dataportal.api.model.PageResult;
import com.findmypet.dataportal.core.ApiClient;
import com.findmypet.dataportal.core.EndpointSpecs;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * abandonmentPublic_v2 API 클라이언트
 */
public class AbandonmentClient implements AbandonmentPort {
    private final ApiClient api;

    public AbandonmentClient(ApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @Override
    public PageResult<Animal> getAbandonments(Map<String, String> params) {
        var dto = api.get(EndpointSpecs.ABANDONMENT, params, AbandonmentResponse.class).block();
        return dto != null
                ? dto.toDomain()
                : new PageResult<>(List.of(), 1, 0, 0);
    }
}
