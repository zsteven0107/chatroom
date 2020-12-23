package edu.rice.comp504.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CloseConnectionRequest {
    private String request;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private Integer userID;
    }
}
