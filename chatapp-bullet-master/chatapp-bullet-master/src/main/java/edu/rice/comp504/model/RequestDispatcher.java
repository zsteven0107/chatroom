package edu.rice.comp504.model;

import edu.rice.comp504.model.cmd.CommandFactory;
import edu.rice.comp504.model.cmd.ICommand;
import org.eclipse.jetty.websocket.api.Session;

public class RequestDispatcher {
    /**
     * Handle the request.
     */
    public static void handle(Session session, String message) {
        System.out.println(message);
        ICommand cmd = CommandFactory.getCommand(session, message);
        cmd.execute();
    }
}
