package com.findmypet.dataportal.adaptor.client;

import com.findmypet.dataportal.adaptor.dto.shelter.ShelterResponse;
import com.findmypet.dataportal.api.ShelterPort;
import com.findmypet.dataportal.api.model.Shelter;
import com.findmypet.dataportal.core.ApiClient;
import com.findmypet.dataportal.core.EndpointSpecs;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShelterClient implements ShelterPort {
    private final ApiClient api;

    public ShelterClient(ApiClient api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    @Override
    public List<Shelter> getShelters(String uprCd, String orgCd) {
        var params = Map.of(
                "upr_cd", uprCd,
                "org_cd", orgCd
        );
        var dto = api.get(EndpointSpecs.SHELTER, params, ShelterResponse.class).block();
        return dto != null ? dto.toDomain() : List.of();
    }
}
