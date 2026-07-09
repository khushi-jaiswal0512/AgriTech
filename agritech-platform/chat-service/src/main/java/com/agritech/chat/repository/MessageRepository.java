package com.agritech.chat.repository;

import com.agritech.chat.entity.Message;
import com.agritech.chat.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByConversationId(Long conversationId, Pageable pageable);

    @Modifying
    @Query("UPDATE Message m SET m.messageStatus = :status WHERE m.id IN :messageIds")
    void updateMessageStatus(List<Long> messageIds, MessageStatus status);
}
