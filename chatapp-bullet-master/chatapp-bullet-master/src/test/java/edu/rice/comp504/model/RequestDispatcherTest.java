package edu.rice.comp504.model;

import com.google.gson.Gson;
import edu.rice.comp504.model.object.ChatRoom;
import edu.rice.comp504.model.object.ChatRoomManager;
import edu.rice.comp504.model.object.UserManager;
import edu.rice.comp504.model.response.ReturnProfileResponse;
import edu.rice.comp504.model.response.UpdateChatRoomListResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RequestDispatcherTest {

    @Test
    public void parseTest() {
        ChatRoomManager.getOnly().reset();
        UserManager.getOnly().reset();
        Session session = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        String str = "{\"request\":\"register\",\"data\":{\"userName\":\"Siyu\",\"age\":24,\"school\":\"Rice University\",\"interests\":[\"swimming\",\"reading\"]}}";
        RequestDispatcher.handle(session, str);

        //build expected update chatroom list response
        ChatRoom[] publicChatRooms = ChatRoomManager.getOnly().getPublicChatRooms();
        UpdateChatRoomListResponse.Data.ChatRoomInfo[] chatRoomInfos =
                new UpdateChatRoomListResponse.Data.ChatRoomInfo[publicChatRooms.length];
        for(int i = 0; i < publicChatRooms.length; i++){
            ChatRoom chatRoom = publicChatRooms[i];
            chatRoomInfos[i] = UpdateChatRoomListResponse.Data.ChatRoomInfo.builder()
                    .chatRoomID(chatRoom.getChatRoomID())
                    .chatRoomName(chatRoom.getChatRoomName())
                    .build();
        }
        UpdateChatRoomListResponse updateChatRoomListResponse = UpdateChatRoomListResponse.builder()
                .response("update_chatroom_list")
                .data(UpdateChatRoomListResponse.Data.builder()
                        .listType("public")
                        .chatRoomList(new UpdateChatRoomListResponse.Data.ChatRoomInfo[0]).build())
                .build();

        //build expected return profile response
        ReturnProfileResponse.Data data = ReturnProfileResponse.Data.builder()
                .age(24)
                .userName("Siyu")
                .school("Rice University")
                .userID(1)
                .interests(new String[]{"swimming", "reading"})
                .build();

        ReturnProfileResponse returnProfileResponse = ReturnProfileResponse.builder()
                .response("return_profile")
                .data(data)
                .build();

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(session.getRemote(), times(2)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(updateChatRoomListResponse), capturedResponses.get(0));
            assertEquals(GsonInstance.getGson().toJson(returnProfileResponse), capturedResponses.get(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}