package com.agritech.chat.service.impl;

import com.agritech.chat.dto.request.SendMessageRequest;
import com.agritech.chat.dto.response.ConversationResponse;
import com.agritech.chat.dto.response.MessageResponse;
import com.agritech.chat.entity.Conversation;
import com.agritech.chat.entity.Message;
import com.agritech.chat.enums.MessageStatus;
import com.agritech.chat.repository.ConversationRepository;
import com.agritech.chat.repository.MessageRepository;
import com.agritech.chat.service.ChatService;
import com.agritech.common.exception.ForbiddenException;
import com.agritech.common.exception.ResourceNotFoundException;
import com.agritech.common.pagination.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public MessageResponse sendMessage(Long senderId, SendMessageRequest request) {
        Conversation conversation = conversationRepository.findByParticipants(senderId, request.getRecipientId())
                .orElseGet(() -> {
                    Conversation newConv = Conversation.builder()
                            .participantOne(senderId)
                            .participantTwo(request.getRecipientId())
                            .build();
                    return conversationRepository.save(newConv);
                });

        Message message = Message.builder()
                .conversation(conversation)
                .senderId(senderId)
                .content(request.getContent())
                .messageStatus(MessageStatus.SENT)
                .build();

        message = messageRepository.save(message);

        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        MessageResponse response = mapToResponse(message);

        // Push real-time message to recipient via WebSocket
        messagingTemplate.convertAndSendToUser(
                request.getRecipientId().toString(),
                "/queue/messages",
                response
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations(Long userId) {
        return conversationRepository.findByUserIdOrderByLastMessageAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MessageResponse> getConversationMessages(Long conversationId, Long userId, int page, int size) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

        if (!conversation.getParticipantOne().equals(userId) && !conversation.getParticipantTwo().equals(userId)) {
            throw new ForbiddenException("You are not a participant in this conversation");
        }

        Page<Message> messages = messageRepository.findByConversationId(
                conversationId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        return PagedResponse.from(messages.map(this::mapToResponse));
    }

    @Override
    @Transactional
    public void updateMessageStatus(List<Long> messageIds, MessageStatus status) {
        if (messageIds != null && !messageIds.isEmpty()) {
            messageRepository.updateMessageStatus(messageIds, status);
        }
    }

    private ConversationResponse mapToResponse(Conversation conversation) {
        return ConversationResponse.builder()
                .id(conversation.getId())
                .participantOne(conversation.getParticipantOne())
                .participantTwo(conversation.getParticipantTwo())
                .lastMessageAt(conversation.getLastMessageAt())
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .status(message.getMessageStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
