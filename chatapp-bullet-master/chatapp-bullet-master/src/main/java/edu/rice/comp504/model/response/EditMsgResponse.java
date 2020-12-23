package edu.rice.comp504.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EditMsgResponse {
    private String response;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private int chatRoomID;
        private int executorID;
        private int senderID;
        private int messageID;
        private String messageType;
        private String timestamp;
        private String content;
    }
}
