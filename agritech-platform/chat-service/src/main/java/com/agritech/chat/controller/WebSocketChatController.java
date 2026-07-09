package com.agritech.chat.controller;

import com.agritech.chat.dto.request.MessageStatusUpdate;
import com.agritech.chat.enums.MessageStatus;
import com.agritech.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.delivered")
    public void markDelivered(@Payload MessageStatusUpdate update) {
        chatService.updateMessageStatus(update.getMessageIds(), MessageStatus.DELIVERED);
        
        // Notify sender that their messages were delivered
        if (update.getSenderId() != null) {
            messagingTemplate.convertAndSendToUser(
                    update.getSenderId().toString(),
                    "/queue/status-updates",
                    update
            );
        }
    }

    @MessageMapping("/chat.read")
    public void markRead(@Payload MessageStatusUpdate update) {
        chatService.updateMessageStatus(update.getMessageIds(), MessageStatus.READ);
        
        // Notify sender that their messages were read
        if (update.getSenderId() != null) {
            messagingTemplate.convertAndSendToUser(
                    update.getSenderId().toString(),
                    "/queue/status-updates",
                    update
            );
        }
    }
}
