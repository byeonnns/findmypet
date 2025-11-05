package com.findmypet.dataportal.adaptor.client;

import com.findmypet.dataportal.adaptor.dto.sido.SidoResponse;
import com.findmypet.dataportal.adaptor.dto.sigungu.SigunguResponse;
import com.findmypet.dataportal.api.LocalCodePort;
import com.findmypet.dataportal.api.model.Sido;
import com.findmypet.dataportal.api.model.Sigungu;
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
public class LocalCodeClient implements LocalCodePort {
    private final ApiClient api;

    public LocalCodeClient(ApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @CircuitBreaker(name = "dataportalApi", fallbackMethod = "fallbackGetSido")
    @Retry(name = "dataportalApi")
    @Override
    public List<Sido> getSido() {
        var dto = api.get(EndpointSpecs.SIDO, Map.of(), SidoResponse.class).block();
        return dto != null ? dto.toDomain() : Collections.emptyList();
    }

    private List<Sido> fallbackGetSido(Throwable t) {
        log.warn("[Fallback] Sido API 실패: {}", t.toString());
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "dataportalApi", fallbackMethod = "fallbackGetSigungu")
    @Retry(name = "dataportalApi")
    @Override
    public List<Sigungu> getSigungu(String uprCd) {
        var dto = api.get(EndpointSpecs.SIGUNGU, Map.of("upr_cd", uprCd), SigunguResponse.class).block();
        return dto != null ? dto.toDomain() : Collections.emptyList();
    }

    private List<Sigungu> fallbackGetSigungu(String uprCd, Throwable t) {
        log.warn("[Fallback] Sigungu API 실패: {}", t.toString());
        return Collections.emptyList();
    }
}