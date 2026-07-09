package com.agritech.notification.service;

import com.agritech.common.pagination.PagedResponse;
import com.agritech.notification.dto.response.NotificationResponse;
import com.agritech.notification.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    void createNotification(Long userId, NotificationType type, String title, String message, String refId, String refType);
    PagedResponse<NotificationResponse> getUserNotifications(Long userId, int page, int size);
    Long getUnreadCount(Long userId);
    void markAsRead(Long userId, List<Long> notificationIds);
    void markAllAsRead(Long userId);
}
