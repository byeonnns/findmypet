package com.findmypet.dataportal.adaptor.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

public class CommonResponse<T> {

    public Response<T> response;

    public static class Response<T> {
        public Header header;
        public Body<T> body;
    }

    public static class Header {
        public String resultCode; // "00" 이 정상
        public String resultMsg;
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Body<T> {
        public Items<T> items;
        public Integer numOfRows;
        public Integer pageNo;
        public Integer totalCount;
    }

    public static class Items<T> {
        @JsonProperty("item")
        public List<T> list;
    }
}