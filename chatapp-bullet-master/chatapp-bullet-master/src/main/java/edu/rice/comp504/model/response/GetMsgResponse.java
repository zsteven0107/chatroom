package edu.rice.comp504.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetMsgResponse {
    private String response;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private MessageInfo[] messageList;

        @Getter
        @Setter
        @Builder
        public static class MessageInfo{
            private int chatRoomID;
            private int userID;
            private int messageID;
            private String messageType;
            private String timestamp;
            private int receiverID;
            private String content;
        }
    }
}
