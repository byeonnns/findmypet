package com.findmypet.dataportal.adaptor.dto.common;

import com.findmypet.dataportal.api.model.PageResult;

import java.util.List;
import java.util.function.Function;

/**
 * 공용 변환 유틸 클래스
 * CommonResponse<T> → PageResult<U> 변환
 */
public final class Converters {
    private Converters() {}

    /**
     * CommonResponse<S>를 받아 PageResult<T>로 변환
     *
     * @param src    공공데이터포털 공통 응답
     * @param mapper 응답 item 리스트를 도메인 리스트로 매핑하는 함수
     */
    public static <S, T> PageResult<T> toPage(
            CommonResponse<S> src,
            Function<List<S>, List<T>> mapper
    ) {
        var body = src.response != null ? src.response.body : null;
        var items = (body != null && body.items != null) ? body.items.list : List.<S>of();

        var mapped = mapper.apply(items);

        int pageNo = body != null && body.pageNo != null ? body.pageNo : 1;
        int numOfRows = body != null && body.numOfRows != null ? body.numOfRows : mapped.size();
        int totalCount = body != null && body.totalCount != null ? body.totalCount : mapped.size();

        return new PageResult<>(mapped, pageNo, numOfRows, totalCount);
    }

    /** CommonResponse<S> -> List<T> (null-safe) */
    public static <S, T> List<T> toList(
            CommonResponse<S> src,
            Function<List<S>, List<T>> mapper
    ) {
        if (src == null || src.response == null) return List.of();
        var body = src.response.body;
        var items = (body != null && body.items != null && body.items.list != null)
                ? body.items.list
                : List.<S>of();
        var mapped = mapper.apply(items);
        return mapped != null ? mapped : List.of();
    }
}