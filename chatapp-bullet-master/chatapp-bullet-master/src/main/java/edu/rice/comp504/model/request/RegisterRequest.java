package edu.rice.comp504.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class RegisterRequest extends IRequest {
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private String userName;
        private int age;
        private String school;
        private String[] interests;
    }
}
