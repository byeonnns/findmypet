package com.findmypet.dataportal.adaptor.client;

import com.findmypet.dataportal.adaptor.dto.abandonment.AbandonmentResponse;
import com.findmypet.dataportal.api.AbandonmentPort;
import com.findmypet.dataportal.api.model.Animal;
import com.findmypet.dataportal.api.model.PageResult;
import com.findmypet.dataportal.core.ApiClient;
import com.findmypet.dataportal.core.EndpointSpecs;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * abandonmentPublic_v2 API 클라이언트
 */
@Slf4j
public class AbandonmentClient implements AbandonmentPort {
    private final ApiClient api;

    public AbandonmentClient(ApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    /**
     * 공공데이터포털 API 호출 시 서킷브레이커 + 재시도 정책 적용
     */
    @CircuitBreaker(name = "dataportalApi", fallbackMethod = "fallbackGetAbandonments")
    @Retry(name = "dataportalApi")
    @Override
    public PageResult<Animal> getAbandonments(Map<String, String> params) {
        var dto = api.get(EndpointSpecs.ABANDONMENT, params, AbandonmentResponse.class).block();
        return dto != null
                ? dto.toDomain()
                : new PageResult<>(List.of(), 1, 0, 0);
    }

    /**
     * fallback 메서드: 외부 API 실패 시 안전하게 빈 결과 반환
     */
    private PageResult<Animal> fallbackGetAbandonments(Map<String, String> params, Throwable t) {
        log.warn("[Fallback] 데이터포털 API 호출 실패 → {}", t.toString());
        return new PageResult<>(List.of(), 1, 0, 0);
    }
}
