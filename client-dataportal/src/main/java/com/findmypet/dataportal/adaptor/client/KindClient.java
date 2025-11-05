package com.findmypet.dataportal.adaptor.client;

import com.findmypet.dataportal.adaptor.dto.kind.KindResponse;
import com.findmypet.dataportal.api.KindPort;
import com.findmypet.dataportal.api.model.Kind;
import com.findmypet.dataportal.core.ApiClient;
import com.findmypet.dataportal.core.EndpointSpecs;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class KindClient implements KindPort {
    private final ApiClient api;

    public KindClient(ApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @CircuitBreaker(name = "dataportalApi", fallbackMethod = "fallbackGetKinds")
    @Retry(name = "dataportalApi")
    @Override
    public List<Kind> getKinds(String upKindCd) {
        var dto = api.get(EndpointSpecs.KIND, Map.of("up_kind_cd", upKindCd), KindResponse.class).block();
        return dto != null ? dto.toDomain() : List.of();
    }

    private List<Kind> fallbackGetKinds(Map<String, String> params, Throwable t) {
        log.warn("[Fallback] Kind API 실패: {}", t.toString());
        return Collections.emptyList();
    }
}