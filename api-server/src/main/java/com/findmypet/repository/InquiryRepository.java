package com.findmypet.repository;

import com.findmypet.domain.inquiry.Inquiry;
import com.findmypet.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    Optional<Inquiry> findByIdAndIsDeletedFalse(Long id);

    // 받은 문의 목록
    Page<Inquiry> findAllByReceiverAndIsDeletedFalse(User receiver, Pageable pageable);

    // 보낸 문의 목록
    Page<Inquiry> findAllBySenderAndIsDeletedFalse(User sender, Pageable pageable);

}
