package edu.rice.comp504.model.cmd;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.request.InviteUserRequest;
import edu.rice.comp504.model.response.AddMsgResponse;
import edu.rice.comp504.model.response.ErrorResponse;
import edu.rice.comp504.model.response.UpdateChatRoomListResponse;
import edu.rice.comp504.model.response.UpdateUserListResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.RequestBuilder;
import edu.rice.comp504.model.utilities.ResponseBuilder;
import edu.rice.comp504.model.utilities.TestUtilities;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.List;

public class InviteUserCmdTest {

    //this is admin
    private User admin;
    //this is a user1
    private User user1;
    //this is a user2
    private User user2;

    private ChatRoom chatRoom;

    public InviteUserCmdTest(){
        ChatRoomManager.getOnly().reset();
        UserManager.getOnly().reset();

        Session adminSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        Session user1Session = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        Session user2Session = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);

        admin = new User(UserManager.getNextUserID(), "sia", 20, "rice university", new String[]{"swimming", "reading"}, adminSession);
        user1 = new User(UserManager.getNextUserID(), "calvin", 20, "rice university", new String[]{"swimming", "reading"}, user1Session);
        user2 = new User(UserManager.getNextUserID(), "debbie", 20, "rice university", new String[]{"swimming", "reading"}, user2Session);

        UserManager.getOnly().newUser(admin);
        UserManager.getOnly().newUser(user1);
        UserManager.getOnly().newUser(user2);

        /*create a chat room with two users(one of them is admin)*/
        chatRoom = ChatRoomManager.getOnly().addChatRoom("0606", false, admin.getUserID());
        chatRoom.addUser(user1.getUserID());
    }

    @Test
    public void NoRightToInviteTest(){
        //user1 invite user2 to chatRoom
        InviteUserRequest inviteUserRequest = RequestBuilder.buildInviteUserRequest(chatRoom.getChatRoomID(), user2.getUserID(), user1.getUserID());
        RequestDispatcher.handle(user1.getSession(), GsonInstance.getGson().toJson(inviteUserRequest));

        //expected response(error)
        ErrorResponse errorResponse = ResponseBuilder.buildErrorResponse("You are not allowed to invite other users.");

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
    public void UserNotExistsTest(){
        //admin invite non-existing user to chatRoom
        InviteUserRequest inviteUserRequest = RequestBuilder.buildInviteUserRequest(chatRoom.getChatRoomID(), 1000, admin.getUserID());
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(inviteUserRequest));

        //expected response(error)
        ErrorResponse errorResponse = ResponseBuilder.buildErrorResponse("User doesn't exists.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(admin.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(errorResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void UserAlreadyInChatroomTest(){
        //admin invite user already inside to chatRoom
        InviteUserRequest inviteUserRequest = RequestBuilder.buildInviteUserRequest(chatRoom.getChatRoomID(), user1.getUserID(), admin.getUserID());
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(inviteUserRequest));

        //expected response(error)
        ErrorResponse errorResponse = ResponseBuilder.buildErrorResponse("User is already in this chat room.");

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(admin.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(errorResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InviteSuccessTest(){
        //admin invite user already inside to chatRoom
        InviteUserRequest inviteUserRequest = RequestBuilder.buildInviteUserRequest(chatRoom.getChatRoomID(), user2.getUserID(), admin.getUserID());
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(inviteUserRequest));

        //expected responses
        //add message response
        String content = "user " + user2.getUsername() + " joins";
        Message newMsg = new Message(chatRoom.getNextMsgID() - 1, "system", new Timestamp(System.currentTimeMillis()),0, 0, content);
        String addMsgResponseStr = GsonInstance.getGson().toJson(ResponseBuilder.buildAddMessageResponse(chatRoom.getChatRoomID(), newMsg, "system", "everyone"));

        //update user list response
        UpdateUserListResponse.Data.UserInfo[] userInfos =
                new UpdateUserListResponse.Data.UserInfo[3];
        userInfos[0] = UpdateUserListResponse.Data.UserInfo.builder()
                .userName(admin.getUsername())
                .userID(admin.getUserID())
                .isBanned(false)
                .isAdmin(true)
                .build();
        userInfos[1] = UpdateUserListResponse.Data.UserInfo.builder()
                .userName(user1.getUsername())
                .userID(user1.getUserID())
                .isBanned(false)
                .isAdmin(false)
                .build();
        userInfos[2] = UpdateUserListResponse.Data.UserInfo.builder()
                .userName(user2.getUsername())
                .userID(user2.getUserID())
                .isBanned(false)
                .isAdmin(false)
                .build();
        UpdateUserListResponse updateUserListResponse = UpdateUserListResponse.builder()
                .response("update_user_list")
                .data(UpdateUserListResponse.Data.builder().userList(userInfos).build())
                .build();
        String updateUserListResponseStr = GsonInstance.getGson().toJson(updateUserListResponse);

        //update user's own chat room list response
        UpdateChatRoomListResponse.Data.ChatRoomInfo[] chatRoomInfos = new UpdateChatRoomListResponse.Data.ChatRoomInfo[1];
        chatRoomInfos[0] = UpdateChatRoomListResponse.Data.ChatRoomInfo.builder()
                .chatRoomName(this.chatRoom.getChatRoomName())
                .chatRoomID(this.chatRoom.getChatRoomID())
                .build();
        UpdateChatRoomListResponse.Data updateChatRoomListResponseData = UpdateChatRoomListResponse.Data.builder()
                .listType("own")
                .chatRoomList(chatRoomInfos)
                .build();
        UpdateChatRoomListResponse updateChatRoomListResponse = UpdateChatRoomListResponse.builder()
                .response("update_chatroom_list")
                .data(updateChatRoomListResponseData)
                .build();
        String updateChatRoomListResponseStr = GsonInstance.getGson().toJson(updateChatRoomListResponse);

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            List<String> capturedResponses;
            //for admin, receive add msg response and update user list response
            verify(admin.getSession().getRemote(), times(2)).sendString(argument.capture());
            capturedResponses = argument.getAllValues();
            assertEquals(true, TestUtilities.verifyMessage(addMsgResponseStr, capturedResponses.get(0)));
            assertEquals(updateUserListResponseStr, capturedResponses.get(1));

            //for user1, receive add msg response and update user list response
            verify(user1.getSession().getRemote(), times(2)).sendString(argument.capture());
            capturedResponses = argument.getAllValues();
            assertEquals(true, TestUtilities.verifyMessage(addMsgResponseStr, capturedResponses.get(2)));
            assertEquals(updateUserListResponseStr, capturedResponses.get(3));

            //for user2, receive add msg response, update user list response and update own chat room list response
            verify(user2.getSession().getRemote(), times(3)).sendString(argument.capture());
            capturedResponses = argument.getAllValues();
            assertEquals(true, TestUtilities.verifyMessage(addMsgResponseStr, capturedResponses.get(4)));
            assertEquals(updateUserListResponseStr, capturedResponses.get(5));
            assertEquals(updateChatRoomListResponseStr, capturedResponses.get(6));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}