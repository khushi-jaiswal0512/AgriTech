package com.agritech.common.constants;

/**
 * Chat message delivery status.
 * Supports WhatsApp-like delivery semantics.
 */
public enum MessageStatus {
    /** Message persisted in database */
    SENT,
    /** Recipient's client received the message */
    DELIVERED,
    /** Recipient opened and read the message */
    READ
}
