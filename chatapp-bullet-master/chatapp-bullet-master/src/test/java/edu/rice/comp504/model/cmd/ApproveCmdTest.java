package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.ChatRoom;
import edu.rice.comp504.model.object.ChatRoomManager;
import edu.rice.comp504.model.object.User;
import edu.rice.comp504.model.object.UserManager;
import edu.rice.comp504.model.request.ApproveRequest;
import edu.rice.comp504.model.request.JoinRequest;
import edu.rice.comp504.model.response.AskApproveResponse;
import edu.rice.comp504.model.response.ErrorResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.RequestBuilder;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ApproveCmdTest {

    //this is admin
    private User admin;
    private User user1;
    private User user2;
    private ChatRoom chatRoom;


    public ApproveCmdTest(){
        ChatRoomManager.getOnly().getChatRoom(0);
        UserManager.getOnly().getUser(0);
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
    public void ApproveCmdTest(){
        ApproveRequest approveRequest = RequestBuilder.buildApproveRequest(chatRoom.getChatRoomID(), user1.getUserID(), true);
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(approveRequest));

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(admin.getSession().getRemote(), times(2)).sendString(argument.capture());
            verify(user1.getSession().getRemote(), times(4)).sendString(argument.capture());
            verify(user2.getSession().getRemote(), times(0)).sendString(argument.capture());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserManager.getOnly().broadcastResponse("empty");
    }

    @Test
    public void DisapproveCmdTest(){
        ApproveRequest approveRequest = RequestBuilder.buildApproveRequest(chatRoom.getChatRoomID(), user1.getUserID(), false);
        RequestDispatcher.handle(admin.getSession(), GsonInstance.getGson().toJson(approveRequest));

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(admin.getSession().getRemote(), times(0)).sendString(argument.capture());
            verify(user1.getSession().getRemote(), times(1)).sendString(argument.capture());
            verify(user2.getSession().getRemote(), times(0)).sendString(argument.capture());
        } catch (Exception e) {
            e.printStackTrace();
        }
        chatRoom.getMsg(-1);
    }
}