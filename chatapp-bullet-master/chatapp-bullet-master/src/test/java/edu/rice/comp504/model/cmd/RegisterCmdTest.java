package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.ChatRoomManager;
import edu.rice.comp504.model.object.User;
import edu.rice.comp504.model.object.UserManager;
import edu.rice.comp504.model.request.RegisterRequest;
import edu.rice.comp504.model.response.ReturnProfileResponse;
import edu.rice.comp504.model.response.UpdateChatRoomListResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.RequestBuilder;
import edu.rice.comp504.model.utilities.TestUtilities;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RegisterCmdTest {

    private User user;


    public RegisterCmdTest(){

    }
    @Test
    public void RegisterSuccessTest(){

        ChatRoomManager.getOnly().reset();
        UserManager.getOnly().reset();

        Session userSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        RegisterRequest registerRequest = RequestBuilder.buildRegisterRequest("sia", 20, "rice university", new String[]{"swimming", "reading"});
        RequestDispatcher.handle(userSession, GsonInstance.getGson().toJson(registerRequest));

        //expected responses
        //return profile response
        ReturnProfileResponse.Data data = ReturnProfileResponse.Data.builder()
                .age(20)
                .userName("sia")
                .school("rice university")
                .userID(1)
                .interests(new String[]{"swimming", "reading"})
                .build();

        ReturnProfileResponse returnProfileResponse = ReturnProfileResponse.builder()
                .response("return_profile")
                .data(data)
                .build();
        String returnProfileResponseStr = GsonInstance.getGson().toJson(returnProfileResponse);

        //update user's own chat room list response
        UpdateChatRoomListResponse.Data.ChatRoomInfo[] chatRoomInfos = new UpdateChatRoomListResponse.Data.ChatRoomInfo[0];
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
            List<String> capturedResponses;
            //for admin, receive add msg response and update user list response
            verify(userSession.getRemote(), times(2)).sendString(argument.capture());
            capturedResponses = argument.getAllValues();
            //assertEquals(updateChatRoomListResponseStr, capturedResponses.get(0));
            assertEquals(returnProfileResponseStr, capturedResponses.get(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}