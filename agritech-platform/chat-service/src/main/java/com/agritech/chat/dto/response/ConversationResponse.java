package com.agritech.chat.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConversationResponse {
    private Long id;
    private Long participantOne;
    private Long participantTwo;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
}
