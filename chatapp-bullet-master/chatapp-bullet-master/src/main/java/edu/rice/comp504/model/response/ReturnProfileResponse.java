package edu.rice.comp504.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReturnProfileResponse {
    private String response;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private String userName;
        private int age;
        private String school;
        private String[] interests;
        private int userID;
    }
}
