package com.findmypet.repository;

import com.findmypet.domain.inquiry.Inquiry;
import com.findmypet.domain.inquiry.InquiryMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryMessageRepository extends JpaRepository<InquiryMessage, Long> {

    // 특정 문의에 속한 메시지를 생성 순으로 조회
    List<InquiryMessage> findAllByInquiryOrderByCreatedAtAsc(Inquiry inquiry);
}
