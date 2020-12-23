package edu.rice.comp504.model.cmd;

import com.google.gson.Gson;
import edu.rice.comp504.model.object.ChatRoom;
import edu.rice.comp504.model.object.ChatRoomManager;
import edu.rice.comp504.model.object.User;
import edu.rice.comp504.model.object.UserManager;
import edu.rice.comp504.model.request.IRequest;
import edu.rice.comp504.model.response.ReturnProfileResponse;
import edu.rice.comp504.model.response.UpdateChatRoomListResponse;
import edu.rice.comp504.model.utilities.GsonInstance;
import edu.rice.comp504.model.utilities.ResponseBuilder;
import org.eclipse.jetty.websocket.api.Session;

/**
 * User registration.
 */
public class RegisterCmd  implements ICommand {
    private String userName;
    private Integer age;
    private String school;
    private String[] interests;
    private Session session;

    /**
     * Constructor.
     */
    public RegisterCmd(String userName, Integer age, String school, String[] interests, Session session) {
        this.userName = userName;
        this.age = age;
        this.school = school;
        this.interests = interests;
        this.session = session;
    }

    /**
     * Execute the command.
     */
    public void execute() {
        User user = new User(UserManager.getNextUserID(),
                this.userName,
                this.age,
                this.school,
                this.interests,
                this.session
                );
        UserManager.getOnly().newUser(user);


        //send public chat room list back to the user
        user.sendResponse(GsonInstance.getGson().toJson(ResponseBuilder.buildUpdateChatRoomListResponse(user)));

        //send user's profile(containing user id) back to the user
        user.sendResponse(GsonInstance.getGson().toJson(ResponseBuilder.buildProfileResponse(user)));
    }
}
