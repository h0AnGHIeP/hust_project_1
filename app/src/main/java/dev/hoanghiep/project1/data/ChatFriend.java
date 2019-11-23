package dev.hoanghiep.project1.data;

import lombok.Data;

@Data
public class ChatFriend {
    private String id;
    private String conversationId;
    private String name;
    private boolean isAccepted;

}
