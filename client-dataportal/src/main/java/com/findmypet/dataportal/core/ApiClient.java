package com.findmypet.dataportal.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper; // JSON Mapper import 추가
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ApiClient {
    private final WebClient client;
    private final DataPortalProperties props;
    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;

    public ApiClient(DataPortalProperties props) {
        this.props = Objects.requireNonNull(props, "props");
        HttpClient http = HttpClient.create()
                .responseTimeout(props.getReadTimeout());

        this.client = WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(http))
                .build();

        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // JSON Mapper 초기화
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 외부 API에 GET 요청을 보냅니다.
     * URI 템플릿을 사용하여 모든 파라미터를 안전하게 인코딩합니다.
     */
    public <T> Mono<T> get(EndpointSpec spec, Map<String, ?> params, Class<T> type) {
        Map<String, Object> q = new LinkedHashMap<>();
        if (params != null) q.putAll(params);
        if (props.isUseJson()) {
            q.put("_type", "json");
        }

        spec.validate(q);

        Map<String, Object> uriVariables = new LinkedHashMap<>();
        uriVariables.put("serviceKey", props.getServiceKey());
        uriVariables.putAll(q);

        return client.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(spec.path());
                    uriVariables.keySet().forEach(key -> uriBuilder.queryParam(key, "{" + key + "}"));
                    return uriBuilder.build(uriVariables);
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "DataPortal HTTP error " + resp.statusCode() + " body=" + body))))
                .bodyToMono(String.class)
                .map(body -> parseBody(body, type));
    }

    /**
     * API 응답 본문을 파싱합니다.
     * 응답이 JSON 형식인지 XML 형식인지 감지하여 적절한 Mapper를 사용합니다.
     */
    private <T> T parseBody(String body, Class<T> type) {
        try {
            // XML 에러 응답인지 먼저 확인
            if (body.contains("<OpenAPI_ServiceResponse>") || body.contains("<cmmMsgHeader>")) {
                throw new RuntimeException("DataPortal API 리턴 에러 : " + body);
            }

            // body의 시작 문자를 확인하여 JSON인지 XML인지 판단
            if (body.trim().startsWith("{")) {
                // JSON 파싱
                return jsonMapper.readValue(body, type);
            } else {
                // XML 파싱
                return xmlMapper.readValue(body, type);
            }
        } catch (Exception e) {
            throw new RuntimeException("API response 파싱 실패 : " + body, e);
        }
    }
}
