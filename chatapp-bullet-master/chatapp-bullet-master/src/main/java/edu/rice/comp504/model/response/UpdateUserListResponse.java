package edu.rice.comp504.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateUserListResponse {
    private String response;
    private Data data;

    @Getter
    @Setter
    @Builder
    public static class Data{
        private UserInfo[] userList;

        @Getter
        @Setter
        @Builder
        public static class UserInfo{
            private String userName;
            private int userID;
            private Boolean isBanned;
            private Boolean isAdmin;
        }
    }
}
