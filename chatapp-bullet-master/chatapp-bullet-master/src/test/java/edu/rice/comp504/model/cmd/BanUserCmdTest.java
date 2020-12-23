package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.request.BanUserRequest;
import edu.rice.comp504.model.request.InviteUserRequest;
import edu.rice.comp504.model.response.ErrorResponse;
import edu.rice.comp504.model.response.UpdateChatRoomListResponse;
import edu.rice.comp504.model.response.UpdateUserListResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.RequestBuilder;
import edu.rice.comp504.model.utilities.ResponseBuilder;
import edu.rice.comp504.model.utilities.TestUtilities;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BanUserCmdTest {
    //this is admin
    private User admin;
    //this is a user1
    private User user1;

    private ChatRoom chatRoom;

    public BanUserCmdTest(){
        ChatRoomManager.getOnly().reset();
        UserManager.getOnly().reset();

        Session adminSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        Session user1Session = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);

        admin = new User(UserManager.getNextUserID(), "sia", 20, "rice university", new String[]{"swimming", "reading"}, adminSession);
        user1 = new User(UserManager.getNextUserID(), "calvin", 20, "rice university", new String[]{"swimming", "reading"}, user1Session);

        UserManager.getOnly().newUser(admin);
        UserManager.getOnly().newUser(user1);

        /*create a chat room with two users(one of them is admin)*/
        chatRoom = ChatRoomManager.getOnly().addChatRoom("0606", false, admin.getUserID());
        chatRoom.addUser(user1.getUserID());
    }

    @Test
    public void NoRightToBanTest(){
        //user1 ban admin
        BanUserRequest banUserRequest = RequestBuilder.buildBanUserRequest(chatRoom.getChatRoomID(), admin.getUserID(), user1.getUserID());
        RequestDispatcher.handle(user1.getSession(), GsonInstance.getGson().toJson(banUserRequest));

        //expected response(error)
        ErrorResponse errorResponse = ResponseBuilder.buildErrorResponse("You are not allowed to ban other users.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(user1.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(errorResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InviteSuccessTest(){
        //admin ban user1
        BanUserRequest banUserRequest = RequestBuilder.buildBanUserRequest(chatRoom.getChatRoomID(), user1.getUserID(), admin.getUserID());
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(banUserRequest));

        //expected responses
        //add message response
        String content = "user " + user1.getUsername() + " is banned";
        Message newMsg = new Message(chatRoom.getNextMsgID() - 1, "system", new Timestamp(System.currentTimeMillis()),0, 0, content);
        String addMsgResponse = GsonInstance.getGson().toJson(ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, "system", "everyone"));

        //update user list response
        UpdateUserListResponse.Data.UserInfo[] userInfos =
                new UpdateUserListResponse.Data.UserInfo[2];
        userInfos[0] = UpdateUserListResponse.Data.UserInfo.builder()
                .userName(admin.getUsername())
                .userID(admin.getUserID())
                .isBanned(false)
                .isAdmin(true)
                .build();
        userInfos[1] = UpdateUserListResponse.Data.UserInfo.builder()
                .userName(user1.getUsername())
                .userID(user1.getUserID())
                .isBanned(true)
                .isAdmin(false)
                .build();
        UpdateUserListResponse updateUserListResponse = UpdateUserListResponse.builder()
                .response("update_user_list")
                .data(UpdateUserListResponse.Data.builder().userList(userInfos).build())
                .build();
        String updateUserListResponseStr = GsonInstance.getGson().toJson(updateUserListResponse);

        //expected response(error)
        String errorResponseStr = GsonInstance.getGson().toJson(ResponseBuilder.buildErrorResponse("You have been banned."));

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            List<String> capturedResponses;
            //for admin, receive add msg response and update user list response
            verify(admin.getSession().getRemote(), times(2)).sendString(argument.capture());
            capturedResponses = argument.getAllValues();
            assertEquals(true, TestUtilities.verifyMessage(addMsgResponse, capturedResponses.get(0)));
            assertEquals(updateUserListResponseStr, capturedResponses.get(1));

            //for user1, receive add msg response and update user list response
            verify(user1.getSession().getRemote(), times(3)).sendString(argument.capture());
            capturedResponses = argument.getAllValues();
            assertEquals(true, TestUtilities.verifyMessage(addMsgResponse, capturedResponses.get(2)));
            assertEquals(updateUserListResponseStr, capturedResponses.get(3));
            assertEquals(errorResponseStr,  capturedResponses.get(4));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}