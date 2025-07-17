package com.findmypet.repository;

import com.findmypet.domain.message.MessageThread;
import com.findmypet.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 특정 문의에 속한 메시지를 생성 순으로 조회
    List<Message> findAllByMessageThreadOrderByCreatedAtAsc(MessageThread messageThread);
}
