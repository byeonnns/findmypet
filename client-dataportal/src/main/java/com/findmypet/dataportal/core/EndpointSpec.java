package com.findmypet.dataportal.core;

import java.util.*;

public final class EndpointSpec {
    private final String path;              // ex) /1543061/.../sigungu_v2
    private final Set<String> required;     // serviceKey 제외(클라이언트가 자동 주입)

    public EndpointSpec(String path, Set<String> required) {
        this.path = Objects.requireNonNull(path, "path");
        this.required = Collections.unmodifiableSet(new LinkedHashSet<>(required));
    }

    public String path() { return path; }
    public Set<String> required() { return required; }

    /** 필수 파라미터 누락 검증 */
    public void validate(Map<String, ?> params) {
        for (String key : required) {
            Object v = params.get(key);
            if (v == null || (v instanceof String s && s.isBlank())) {
                throw new IllegalArgumentException("Missing required param: " + key);
            }
        }
    }
}
