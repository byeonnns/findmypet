package com.findmypet.repository;

import com.findmypet.domain.message.MessageThread;
import com.findmypet.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {

    Optional<MessageThread> findByIdAndIsDeletedFalse(Long id);

    // 받은 문의 목록
    Page<MessageThread> findAllByReceiverAndIsDeletedFalse(User receiver, Pageable pageable);

    // 보낸 문의 목록
    Page<MessageThread> findAllBySenderAndIsDeletedFalse(User sender, Pageable pageable);

}
