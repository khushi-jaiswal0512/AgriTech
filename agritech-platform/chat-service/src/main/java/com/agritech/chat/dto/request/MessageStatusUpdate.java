package com.agritech.chat.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class MessageStatusUpdate {
    private Long senderId; // To notify the original sender that their messages were updated
    private List<Long> messageIds;
}
