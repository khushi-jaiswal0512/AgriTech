package com.agritech.chat.controller;

import com.agritech.chat.dto.request.SendMessageRequest;
import com.agritech.chat.dto.response.ConversationResponse;
import com.agritech.chat.dto.response.MessageResponse;
import com.agritech.chat.service.ChatService;
import com.agritech.common.pagination.PagedResponse;
import com.agritech.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Real-time Messaging APIs")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/conversations")
    @Operation(summary = "Start a conversation or send a message")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Message sent successfully",
                chatService.sendMessage(userId, request)
        ));
    }

    @GetMapping("/conversations")
    @Operation(summary = "Get user conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversations(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(ApiResponse.success(
                chatService.getUserConversations(userId)
        ));
    }

    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "Get conversation messages")
    public ResponseEntity<ApiResponse<PagedResponse<MessageResponse>>> getMessages(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                chatService.getConversationMessages(id, userId, page, size)
        ));
    }
}
