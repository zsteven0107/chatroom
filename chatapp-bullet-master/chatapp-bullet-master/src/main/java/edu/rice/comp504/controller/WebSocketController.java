package edu.rice.comp504.controller;

import com.google.gson.Gson;
import edu.rice.comp504.model.RequestDispatcher;
import edu.rice.comp504.model.object.UserManager;
import edu.rice.comp504.model.request.CloseConnectionRequest;
import edu.rice.comp504.model.request.IRequest;
import edu.rice.comp504.model.utilities.RequestBuilder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create a web socket for the server.
 */
@WebSocket
public class WebSocketController {
    //static Map<Integer, Session> sessionToIDMap = new ConcurrentHashMap<>();
    //static int nextUserId = 1;

    /**
     * Open user's session.
     *
     * @param session The user whose session is opened.
     */
    @OnWebSocketConnect
    public void onConnect(Session session) {
//        Integer userID = WebSocketController.nextUserId++;
//        sessionToIDMap.put(userID, session);
        //do nothing
    }

    /**
     * Close the user's session.
     *
     * @param session The use whose session is closed.
     */
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Integer userID = UserManager.getOnly().getUserID(session);
        CloseConnectionRequest closeConnectionRequest = RequestBuilder.buildCloseConnectionRequest(userID);
        Gson gson = new Gson();
        String message = gson.toJson(closeConnectionRequest);
        RequestDispatcher.handle(session, message);
    }

    /**
     * Send a message.
     *
     * @param session    The session user sending the message.
     * @param message The message to be sent.
     */
    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        RequestDispatcher.handle(session, message);
    }
}
