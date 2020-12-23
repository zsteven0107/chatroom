package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.*;
import edu.rice.comp504.model.request.BanUserRequest;
import edu.rice.comp504.model.request.GetMsgRequest;
import edu.rice.comp504.model.request.InviteUserRequest;
import edu.rice.comp504.model.request.SendMsgRequest;
import edu.rice.comp504.model.response.*;
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

public class GetMsgCmdTest {
    private ChatRoom chatRoom;
    private User user;

    public GetMsgCmdTest() {
        Session userSession = mock(Session.class, Mockito.RETURNS_DEEP_STUBS);
        user = new User(UserManager.getNextUserID(), "jack", 20, "rice university", new String[]{"swimming", "reading"}, userSession);
        UserManager.getOnly().newUser(user);

        chatRoom = ChatRoomManager.getOnly().addChatRoom("0606", false, user.getUserID());
        chatRoom.addUser(user.getUserID());
    }

    @Test
    public void SuccessfulGetMsgTest() {
        GetMsgRequest getMsgRequest = RequestBuilder.buildGetMsgRequest(chatRoom.getChatRoomID(), user.getUserID());
        RequestDispatcher.handle(user.getSession(), GsonInstance.getGson().toJson(getMsgRequest));
        GetMsgResponse getMsgResponse = ResponseBuilder.buildGetMessageResponse(chatRoom.getChatRoomID());

        try {
            ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
            verify(user.getSession().getRemote(), times(1)).sendString(argument.capture());
            List<String> capturedResponses = argument.getAllValues();
            assertEquals(GsonInstance.getGson().toJson(getMsgResponse), capturedResponses.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
