package com.findmypet.storage;

public interface PresignedUrlGenerator {

    /**
     * 업로드용 PUT presigned URL 생성
     * @param fileKey     S3 객체 키
     * @param contentType 파일의 Content-Type
     * @return presigned URL 문자열
     */
    String generatePutUrl(String fileKey, String contentType);

    /**
     * 최종 접근 가능한 URL 생성 (실제 S3 URL)
     * @param fileKey S3 객체 키
     * @return 완전한 URL 문자열
     */
    String buildFinalUrl(String fileKey);

}
