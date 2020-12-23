package edu.rice.comp504.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponse {
    private String response;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private String reason;
    }
}
