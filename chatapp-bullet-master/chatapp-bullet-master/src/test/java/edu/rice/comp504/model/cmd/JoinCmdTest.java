package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.ChatRoom;
import edu.rice.comp504.model.object.ChatRoomManager;
import edu.rice.comp504.model.object.User;
import edu.rice.comp504.model.object.UserManager;
import edu.rice.comp504.model.request.CreateRoomRequest;
import edu.rice.comp504.model.request.JoinRequest;
import edu.rice.comp504.model.response.AskApproveResponse;
import edu.rice.comp504.model.response.ErrorResponse;
import edu.rice.comp504.model.response.UpdateChatRoomListResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.RequestBuilder;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class JoinCmdTest {

    //this is admin
    private User admin;
    private User user1;
    private User user2;
    private ChatRoom chatRoom;


    public JoinCmdTest(){
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
    }

    @Test
    public void NormalJoinCmdTest(){
        JoinRequest joinRequest = RequestBuilder.buildJoinRequest(chatRoom.getChatRoomID(), user1.getUserID());
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(joinRequest));

        AskApproveResponse askApproveResponse = AskApproveResponse.builder()
                .response("ask_approve")
                .data(AskApproveResponse.Data.builder()
                        .chatRoomName("0606")
                        .chatRoomID(chatRoom.getChatRoomID())
                        .userID(user1.getUserID())
                        .userName("calvin")
                        .build())
                .build();
        String askApproveResponseStr = GsonInstance.getGson().toJson(askApproveResponse);

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(admin.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(askApproveResponseStr, capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void BannedUserJoinCmdTest(){
        UserManager.getOnly().banUser(user2);
        JoinRequest createRoomRequest = RequestBuilder.buildJoinRequest(chatRoom.getChatRoomID(), user2.getUserID());
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(createRoomRequest));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .response("error")
                .data(ErrorResponse.Data.builder()
                        .reason("sorry, you have been banned and cannot join chat rooms")
                        .build())
                .build();
        String errorResponseStr = GsonInstance.getGson().toJson(errorResponse);

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(user2.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(errorResponseStr, capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}