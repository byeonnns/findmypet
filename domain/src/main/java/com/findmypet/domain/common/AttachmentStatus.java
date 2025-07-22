package com.findmypet.domain.common;

public enum AttachmentStatus {
    /** 실제 업로드가 시작되어 진행 중인 상태 */
    UPLOADING,

    /** 업로드가 정상적으로 완료된 상태 */
    COMPLETED,

    /** 업로드 중 에러가 발생해 실패한 상태 (재시도 가능) */
    FAILED,

    /** Pre-Signed URL이 만료된 상태*/
    EXPIRED,

    /** 업로드된 파일이 삭제된 상태 */
    DELETED
}
