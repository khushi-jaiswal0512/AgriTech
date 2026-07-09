package com.agritech.chat.repository;

import com.agritech.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    @Query("SELECT c FROM Conversation c WHERE (c.participantOne = :user1 AND c.participantTwo = :user2) OR (c.participantOne = :user2 AND c.participantTwo = :user1)")
    Optional<Conversation> findByParticipants(Long user1, Long user2);

    @Query("SELECT c FROM Conversation c WHERE c.participantOne = :userId OR c.participantTwo = :userId ORDER BY c.lastMessageAt DESC")
    List<Conversation> findByUserIdOrderByLastMessageAtDesc(Long userId);
}
