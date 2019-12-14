package dev.hoanghiep.project1.data;

import lombok.Data;

@Data
public class ChatMessage {
    private String senderId;
    private boolean isSender;
    private String content;
    public void who(String user) {
        isSender = user.equals(senderId);
    }
}
