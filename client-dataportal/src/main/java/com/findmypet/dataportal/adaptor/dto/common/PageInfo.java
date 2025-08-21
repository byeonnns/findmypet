package com.findmypet.dataportal.adaptor.dto.common;

public record PageInfo(
        int pageNo,
        int numOfRows,
        int totalCount
) {}