package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.object.ChatRoom;
import edu.rice.comp504.model.object.ChatRoomManager;
import edu.rice.comp504.model.object.User;
import edu.rice.comp504.model.object.UserManager;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;

public class HeartBeatCmd implements ICommand{
    /**
     * Constructor.
     */
    HeartBeatCmd() {
    }

    /**
     * Execute the command.
     */
    public void execute() {
    }
}
