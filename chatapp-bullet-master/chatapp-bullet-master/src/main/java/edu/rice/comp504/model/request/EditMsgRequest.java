package edu.rice.comp504.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class EditMsgRequest extends IRequest {
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private Integer chatRoomID;
        private Integer executorID;
        private Integer senderID;
        private Integer messageID;
        private String newContent;
    }
}