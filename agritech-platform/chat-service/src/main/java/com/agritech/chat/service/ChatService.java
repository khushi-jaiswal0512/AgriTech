package com.agritech.chat.service;

import com.agritech.chat.dto.request.SendMessageRequest;
import com.agritech.chat.dto.response.ConversationResponse;
import com.agritech.chat.dto.response.MessageResponse;
import com.agritech.chat.enums.MessageStatus;
import com.agritech.common.pagination.PagedResponse;

import java.util.List;

public interface ChatService {
    MessageResponse sendMessage(Long senderId, SendMessageRequest request);
    List<ConversationResponse> getUserConversations(Long userId);
    PagedResponse<MessageResponse> getConversationMessages(Long conversationId, Long userId, int page, int size);
    void updateMessageStatus(List<Long> messageIds, MessageStatus status);
}
