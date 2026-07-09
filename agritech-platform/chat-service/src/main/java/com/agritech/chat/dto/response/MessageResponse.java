package com.agritech.chat.dto.response;

import com.agritech.chat.enums.MessageStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String content;
    private MessageStatus status;
    private LocalDateTime createdAt;
}
