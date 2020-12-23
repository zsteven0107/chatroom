package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.request.CreateRoomRequest;
import edu.rice.comp504.model.request.InviteUserRequest;
import edu.rice.comp504.model.response.ErrorResponse;
import edu.rice.comp504.model.response.UpdateChatRoomListResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.RequestBuilder;
import edu.rice.comp504.model.utilities.ResponseBuilder;
import edu.rice.comp504.model.utilities.TestUtilities;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CreateRoomCmdTest {

    //this is admin
    private User admin;
    private User user1;
    private ChatRoom chatRoom;


    public CreateRoomCmdTest(){
        ChatRoomManager.getOnly().reset();
        UserManager.getOnly().reset();
        Session adminSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        Session user1Session = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        admin = new User(UserManager.getNextUserID(), "sia", 20, "rice university", new String[]{"swimming", "reading"}, adminSession);
        user1 = new User(UserManager.getNextUserID(), "calvin", 20, "rice university", new String[]{"swimming", "reading"}, user1Session);
        UserManager.getOnly().newUser(admin);
        UserManager.getOnly().newUser(user1);

    }

    @Test
    public void CreatePublicRoomTest(){
        CreateRoomRequest createRoomRequest = RequestBuilder.buildCreateRoomRequest("GoodRoom", admin.getUserID(), false);
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(createRoomRequest));
        chatRoom = ChatRoomManager.getOnly().getChatRoom(1);

        //update user1's public chat room list response
        UpdateChatRoomListResponse.Data.ChatRoomInfo[] chatRoomInfos = new UpdateChatRoomListResponse.Data.ChatRoomInfo[1];
        chatRoomInfos[0] = UpdateChatRoomListResponse.Data.ChatRoomInfo.builder()
                .chatRoomName(chatRoom.getChatRoomName())
                .chatRoomID(chatRoom.getChatRoomID())
                .build();
        UpdateChatRoomListResponse.Data updateChatRoomListResponseData = UpdateChatRoomListResponse.Data.builder()
                .listType("public")
                .chatRoomList(chatRoomInfos)
                .build();
        UpdateChatRoomListResponse updateChatRoomListResponse = UpdateChatRoomListResponse.builder()
                .response("update_chatroom_list")
                .data(updateChatRoomListResponseData)
                .build();
        String updateChatRoomListResponseStr = GsonInstance.getGson().toJson(updateChatRoomListResponse);

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(user1.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(updateChatRoomListResponseStr, capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //update admin's public chat room list response
        chatRoomInfos = new UpdateChatRoomListResponse.Data.ChatRoomInfo[0];
        updateChatRoomListResponseData = UpdateChatRoomListResponse.Data.builder()
                .listType("public")
                .chatRoomList(chatRoomInfos)
                .build();
        updateChatRoomListResponse = UpdateChatRoomListResponse.builder()
                .response("update_chatroom_list")
                .data(updateChatRoomListResponseData)
                .build();
        updateChatRoomListResponseStr = GsonInstance.getGson().toJson(updateChatRoomListResponse);

        //update admin's joined chat room list response
        chatRoomInfos = new UpdateChatRoomListResponse.Data.ChatRoomInfo[1];
        chatRoomInfos[0] = UpdateChatRoomListResponse.Data.ChatRoomInfo.builder()
                .chatRoomName(chatRoom.getChatRoomName())
                .chatRoomID(chatRoom.getChatRoomID())
                .build();
        updateChatRoomListResponseData = UpdateChatRoomListResponse.Data.builder()
                .listType("own")
                .chatRoomList(chatRoomInfos)
                .build();
        updateChatRoomListResponse = UpdateChatRoomListResponse.builder()
                .response("update_chatroom_list")
                .data(updateChatRoomListResponseData)
                .build();
        String updateAdminJoinedChatRoomListResponseStr = GsonInstance.getGson().toJson(updateChatRoomListResponse);
        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(admin.getSession().getRemote(), times(2)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(updateChatRoomListResponseStr, capturedResponses.get(0));
            assertEquals(updateAdminJoinedChatRoomListResponseStr, capturedResponses.get(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void CreatePrivateRoomTest(){
        CreateRoomRequest createRoomRequest = RequestBuilder.buildCreateRoomRequest("GoodRoom", admin.getUserID(), true);
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(createRoomRequest));
        chatRoom = ChatRoomManager.getOnly().getChatRoom(1);

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(user1.getSession().getRemote(), times(0)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //update admin's joined chat room list response
        UpdateChatRoomListResponse.Data.ChatRoomInfo[] chatRoomInfos = new UpdateChatRoomListResponse.Data.ChatRoomInfo[1];
        chatRoomInfos[0] = UpdateChatRoomListResponse.Data.ChatRoomInfo.builder()
                .chatRoomName(chatRoom.getChatRoomName())
                .chatRoomID(chatRoom.getChatRoomID())
                .build();
        UpdateChatRoomListResponse.Data updateChatRoomListResponseData = UpdateChatRoomListResponse.Data.builder()
                .listType("own")
                .chatRoomList(chatRoomInfos)
                .build();
        UpdateChatRoomListResponse updateChatRoomListResponse = UpdateChatRoomListResponse.builder()
                .response("update_chatroom_list")
                .data(updateChatRoomListResponseData)
                .build();
        String updateAdminJoinedChatRoomListResponseStr = GsonInstance.getGson().toJson(updateChatRoomListResponse);
        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(admin.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(updateAdminJoinedChatRoomListResponseStr, capturedResponses.get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}