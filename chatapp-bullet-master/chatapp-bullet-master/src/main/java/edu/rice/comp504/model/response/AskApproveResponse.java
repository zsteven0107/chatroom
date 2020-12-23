package edu.rice.comp504.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AskApproveResponse {
    private String response;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private int chatRoomID;
        private String chatRoomName;
        private int userID;
        private String userName;
    }
}
