package com.findmypet.dataportal.adaptor.client;

import com.findmypet.dataportal.adaptor.dto.shelter.ShelterResponse;
import com.findmypet.dataportal.api.ShelterPort;
import com.findmypet.dataportal.api.model.Shelter;
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
public class ShelterClient implements ShelterPort {
    private final ApiClient api;

    public ShelterClient(ApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @CircuitBreaker(name = "dataportalApi", fallbackMethod = "fallbackGetShelters")
    @Retry(name = "dataportalApi")
    @Override
    public List<Shelter> getShelters(String uprCd, String orgCd) {
        var params = Map.of(
                "upr_cd", uprCd,
                "org_cd", orgCd
        );
        var dto = api.get(EndpointSpecs.SHELTER, params, ShelterResponse.class).block();
        return dto != null ? dto.toDomain() : List.of();
    }

    private List<Shelter> fallbackGetShelters(Map<String, String> params, Throwable t) {
        log.warn("⚠️ [Fallback] Shelter API 실패: {}", t.toString());
        return Collections.emptyList();
    }
}
