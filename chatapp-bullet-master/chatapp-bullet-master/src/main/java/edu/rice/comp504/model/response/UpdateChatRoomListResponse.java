package edu.rice.comp504.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateChatRoomListResponse {
    private String response;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private String listType;
        private ChatRoomInfo[] chatRoomList;

        @Getter
        @Setter
        @Builder
        public static class ChatRoomInfo{
            private String chatRoomName;
            private int chatRoomID;
        }
    }
}
