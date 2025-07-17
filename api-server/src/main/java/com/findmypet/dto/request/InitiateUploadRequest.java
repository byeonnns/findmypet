package com.findmypet.dto.request;

import lombok.Getter;

@Getter
public class InitiateUploadRequest {
    private String filename;
    private long size;
}