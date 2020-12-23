'use strict';
let userID;
let currentChatRoomID = 999;
let webSocket;
/**
 * Entry point into chat room
 */
window.onload = function () {
     webSocket = new WebSocket("wss://" + location.hostname + ":" + location.port + "/chatapp");

    if (localStorage.ifRegistered == null) {
        location.href = "/index.html"
    }

    webSocket.onclose = () => {
        alert("WebSocket connection closed");
        localStorage.clear();
    }

    webSocket.onopen = () => {
        console.log("Websocket opened.");
        sendRegisterInfo();
        console.log("Registration request sent");
        setInterval(function(){
            webSocket.send(JSON.stringify({
                request: "heart_beat",
            }));
        }, 30000);
    }

    webSocket.onmessage = (msg) => {
        let message = JSON.parse(msg.data);
        let data = message.data;
        console.log(message);
        if (message.response == "return_profile") {
            userID = data.userID;
        }
        if (message.response == "update_chatroom_list") {
            if (data.listType == "own") {
                updateJoinedChatRoomList(data.chatRoomList);
            }
            if (data.listType == "public") {
                updatePublicChatRoomList(data.chatRoomList)
            }
        }
        else if (message.response == "ask_approve") {
            updateJoinedRequest(data);
        }
        else if (message.response == "update_user_list") {
            updateChatRoomUserList(data.userList);
        }
        else if (message.response == "return_msgs") {
            updateChatRoomMessages(data);
        }
        else if (message.response == "add_msg") {
            updateChatRoomMessage(data);
        }
        else if (message.response == "notify_receive") {
            showNotify(data);
        }
        else if (message.response == "recall_msg") {
            recallMessage(data);
        }
        else if (message.response == "edit_msg") {
            editMessage(data);
        }
    }
    $(".emoji").click(function () {
        $("#message").val($("#message").val() + $(this).text());
    })

    $(".emojiEdit").click(function () {
        $("#messageEdit").val($("#messageEdit").val() + $(this).text());
    })

    $("#btn-msg").click(() => sendChattingMessage($("#message").val()));
    $("#invite").click(() => sendInviteMessage());
    $("#createRoom").click(() => sendCreateRoomRequest());
    $("#registerbutton").click(() => sendRegister());


    document.getElementById("profileusername").innerHTML = localStorage.username;
    document.getElementById("profileschool").innerHTML = localStorage.school;
    document.getElementById("profileage").innerHTML = localStorage.age;
    document.getElementById("profileinterest1").innerHTML = localStorage.interest1;
    document.getElementById("profileinterest2").innerHTML = localStorage.interest2;


};

/**
 * Send a create_room_request to the server.
 */
function sendCreateRoomRequest() {
    var msg = $("#chattingRoomNameInput").val();
    let isPrivate = document.getElementById("createRoomType");
    let index = isPrivate.selectedIndex;
    if (msg !== "") {
        webSocket.send(JSON.stringify({
            request: "create_room",
            data: {
                chatRoomName: msg,
                userID: userID,
                isPrivate: isPrivate.options[index].value == "private"
            }
        }));
        $("#chattingRoomNameInput").val("");
    }
}

/**
 * Send a create_room_request to the server.
 */
function sendInviteMessage() {
    var msg = $("#inviteUserToChatRoomInput").val();
    if (msg !== "") {
        webSocket.send(JSON.stringify({
            request: "invite",
            data: {
                chatRoomID: currentChatRoomID,
                userID: parseInt(msg),
                executorID: userID
            }
        }));
        $("#chattingRoomNameInput").val("");
    }
}

/**
 * Send a register request to the server.
 */
function sendRegisterRequest() {
    webSocket.send(JSON.stringify({
        request: "register",
        data: {
            userName: "Siyu",
            age: 24,
            school: "Rice University",
            interest: ["swimming", "reading"]
        }
    }));
}


/**
 * Send a ban request to the server.
 */
function sendBanRequest(banedUserID) {
    webSocket.send(JSON.stringify({
        request: "ban",
        data: {
            chatRoomID: currentChatRoomID,
            userID: banedUserID,
            executorID: userID,
        }
    }));
}

/**
 * Send a approve request to the server.
 */
function sendApproveRequest(chatRoomIDIn, userIDIn, isApproveIn) {
    webSocket.send(JSON.stringify({
        request: "approve",
        data: {
            chatRoomID: chatRoomIDIn,
            userID: userIDIn,
            isApprove: isApproveIn,
        }
    }));
}

/**
 * Send a recall request to the server.
 */
function sendRecallRequest(messageid) {
    webSocket.send(JSON.stringify({
        request: "recall_msg",
        data: {
            chatRoomID: currentChatRoomID,
            executorID: userID,
            messageID: messageid,
        }
    }));
}

/**
 * Send a join request to the server.
 */
function sendJoinRequest(chatRoomIDIn) {
    webSocket.send(JSON.stringify({
        request: "join",
        data: {
            chatRoomID: chatRoomIDIn,
            userID: userID
        }
    }));
}

/**
 * Send a join request to the server.
 */
function sendExitRoomRequest() {
    webSocket.send(JSON.stringify({
        request: "exit",
        data: {
            chatRoomID: currentChatRoomID,
            userID: userID
        }
    }));
    document.getElementById("chatRoomMainWindow").style = "visibility:hidden";
    currentChatRoomID = 999;
}

/**
 * Send a chatting message.
 * @param msg  The message to send.
 */
function sendChattingMessage(val) {
    let sendTo = document.getElementById("sendToMembers");
    let index = sendTo.selectedIndex;
    webSocket.send(JSON.stringify({
        request: "send_msg",
        data: {
            chatRoomID: currentChatRoomID,
            senderID: userID,
            receiverID: parseInt(sendTo.options[index].value),
            content: val
        }}));
    $("#message").val("");
}

/**
 * Send a get Message message.
 * @param msg  The message to send.
 */
function sendGetMsgMessage() {
    webSocket.send(JSON.stringify({
        request: "get_msgs",
        data: {
            chatRoomID: currentChatRoomID,
            userID:userID,
        }
    }));
}

/**
 * Send a get UserList message.
 * @param msg  The message to send.
 */
function sendGetUserListMessage() {
    webSocket.send(JSON.stringify({
        request: "get_user_list",
        data: {
            chatRoomID: currentChatRoomID,
            userID:userID,
        }
    }));
}

/**
 * Update the chat room with messages.
 * @param message  The messages to update the chat room with.
 */
function updateChatRoomMessages(data){
    var chatArea = document.getElementById("chatArea");
    while (chatArea.hasChildNodes()) {
        chatArea.removeChild(chatArea.firstChild);
    }
    data.messageList.forEach(function (jsonData) {
        updateChatRoomMessage(jsonData);
    })
}

/**
 * Update the chat room with a message.
 * @param message  The message to update the chat room with.
 */
function updateChatRoomMessage(data) {
    if (data.chatRoomID == currentChatRoomID) {
        var container = document.createElement("div");
        container.name = data.messageID.toString();
        container.id = "chatMessageContainer" + data.messageID.toString();

        var chatBox = document.createElement("div");

        if (data.messageType == "system") {
            chatBox.className = "chatBox_system";
            var strong = document.createElement("strong");
            strong.className = "primary-font";
            strong.innerHTML = "—— "+data.content+" ——";

            var small = document.createElement("small");
            small.className = "pull-right text-muted";

            var i = document.createElement("i");
            i.className = "far fa-clock";
            small.appendChild(i);
            small.innerHTML = data.timestamp;

            chatBox.append(small);
            chatBox.append(strong);
            document.getElementById("chatArea").appendChild(chatBox);
            return;
        } else if (data.messageType == "public") {
            if (userID == data.userID) {
                chatBox.className = "chatBox_me";
            } else {
                chatBox.className = "chatBox_other";
            }
        } else {
            if (userID == data.userID) {
                chatBox.className = "chatBox_secret_me";
            } else {
                chatBox.className = "chatBox_secret";
            }
        }

        var dropdown = document.createElement("div");
        dropdown.style = "float:right";
        dropdown.innerHTML = document.getElementById("dropdown").innerHTML;
        dropdown.id = data.messageID.toString();
        dropdown.name = data.userID.toString();

        var messageCard = document.createElement("div");
        messageCard.className = "chat-body white p-3 ml-2 z-depth-1";

        var header = document.createElement("div");
        header.className = "header";

        var strong = document.createElement("strong");
        strong.className = "primary-font";
        if (data.messageType == "private") {
            if (data.receiverID == userID) {
                strong.innerHTML = data.senderName + " said to you:"
            } else {
                strong.innerHTML = "You said to " +data.receiverName + ":";
            }
        } else {
            strong.innerHTML = data.senderName;
        }

        var small = document.createElement("small");
        small.className = "pull-right text-muted";

        var i = document.createElement("i");
        i.className = "far fa-clock";
        small.appendChild(i);
        small.innerHTML = data.timestamp;
        header.appendChild(strong);
        header.appendChild(small);
        messageCard.appendChild(header);

        var hr = document.createElement("hr");
        hr.className = "w-100";
        messageCard.appendChild(hr);

        var p = document.createElement("p");
        p.className = "mb-0";
        p.innerHTML = data.content;
        p.id = "p" + data.messageID.toString();
        messageCard.appendChild(p);

        chatBox.appendChild(messageCard);

        if (userID == data.userID) {
            container.className = "container_me";

            var span = document.createElement("span");
            span.id = "span" + data.messageID.toString();
            span.style = "margin-right:5px";

            var container1 = document.createElement("div");
            container1.style = "float:right";

            container1.appendChild(chatBox);
            container1.appendChild(dropdown);

            container.appendChild(span);
            container.appendChild(container1);
        } else {
            container.className = "container_other";

            container.appendChild(chatBox);
            container.appendChild(dropdown);
        }
        document.getElementById("chatArea").appendChild(container);
    }
}

function deleteMessage(messageNode) {
    while (messageNode.className != "container_me" && messageNode.className != "container_other"){
        messageNode = messageNode.parentNode;
    }
    if (messageNode != null) {
        document.getElementById("chatArea").removeChild(messageNode);
    }
}

function showNotify(data) {
    if (data.chatRoomID == currentChatRoomID) {
        var node = document.getElementById("span" + data.messageID.toString());
        if (node != null) {
            node.className = "glyphicon glyphicon-ok icon-success";
        }
    }
}

function sendRecallMessageRequest(messageNode) {
    while (messageNode.className != "container_me" && messageNode.className != "container_other"){
        messageNode = messageNode.parentNode;
    }
    webSocket.send(JSON.stringify({
        request: "recall_msg",
        data: {
            chatRoomID: currentChatRoomID,
            executorID: userID,
            messageID: parseInt(messageNode.name),
        }
    }));
}

/**
 * Show this chatroom window.
 */
function showChatRoom(chatRoomID) {
    currentChatRoomID = chatRoomID;
    var userList = document.getElementById("userList");
    while (userList.hasChildNodes()) {
        userList.removeChild(userList.firstChild);
    }

    var sendMembers = document.getElementById("sendToMembers");
    while (sendMembers.hasChildNodes()) {
        sendMembers.removeChild(sendMembers.firstChild);
    }

    var chatArea = document.getElementById("chatArea");
    while (chatArea.hasChildNodes()) {
        chatArea.removeChild(chatArea.firstChild);
    }
    document.getElementById("chatRoomMainWindow").style = "visibility:visible";
    sendGetMsgMessage();
    sendGetUserListMessage();
}

/**
 * recall a message.
 */
function recallMessage(data) {
    if (data.chatRoomID == currentChatRoomID) {
        var node = document.getElementById("chatMessageContainer" + data.messageID.toString());
        if (node != null) {
            document.getElementById("chatArea").removeChild(node);
        }
    }
}
/**
 * add public chatting rooms.
 */
function updatePublicChatRoomList(data) {
    var publicRooms =  document.getElementById("publicRooms");
    while (publicRooms.hasChildNodes()) {
        publicRooms.removeChild(publicRooms.firstChild);
    }
    data.forEach(function (jsonData) {
        var li = document.createElement("li");
        li.className = "active grey lighten-3 p-2";
        li.name = "getID";

        var a = document.createElement("a");
        a.className = "d-flex justify-content-between";
        a.href = "#";

        var div = document.createElement("div");
        div.className = "text-small";

        var strong = document.createElement("strong");
        strong.innerHTML = jsonData.chatRoomName;

        var button = document.createElement("button");
        button.className = "btn btn-primary btn-xs";
        button.style = "float:right";
        button.innerHTML = "Join";
        button.name = "chatRoomID" + jsonData.chatRoomID.toString();
        button.onclick = function () {
            sendJoinRequest(jsonData.chatRoomID);
            button.className = "btn btn-primary btn-xs disabled";
            button.innerHTML = "Waiting Approved";
        };

        div.appendChild(strong);
        a.appendChild(div);
        a.appendChild(button);
        li.appendChild(a);

        publicRooms.appendChild(li);
    })
}

/**
 * add joined chatting rooms.
 */
function updateJoinedChatRoomList(data) {
    var joinedRooms = document.getElementById("joinedRooms");
    while (joinedRooms.hasChildNodes()) {
        joinedRooms.removeChild(joinedRooms.firstChild);
    }
    data.forEach(function (jsonData) {
        var li = document.createElement("li");
        li.className = "active grey lighten-3 p-2";
        li.name = "chatRoomID" + jsonData.chatRoomID.toString();
        var a = document.createElement("a");
        a.className = "d-flex justify-content-between";
        a.href = "#";
        a.onclick = function () {
            showChatRoom(jsonData.chatRoomID);
            document.getElementById("chattingRoomName").innerText = jsonData.chatRoomName;
        }

        var div = document.createElement("div");
        div.className = "text-small";

        var strong = document.createElement("strong");
        strong.innerHTML = jsonData.chatRoomName;

        div.appendChild(strong);
        a.appendChild(div);
        li.appendChild(a);

        joinedRooms.appendChild(li);
    })
}

/**
 * add joined chatting rooms request.
 */
function updateJoinedRequest(data) {
    console.log(data);
    var li = document.createElement("li");
    li.className = "active grey lighten-3 p-2";
    li.id = "joinedRequestUserID"+data.userID.toString()+"roomID"+data.chatRoomID.toString();
    var a = document.createElement("a");
    a.className = "d-flex justify-content-between";
    a.href = "#";

    var div1 = document.createElement("div");
    div1.className = "text-small";

    var strong = document.createElement("strong");
    strong.innerHTML = data.userName + " asks to join room "
        + data.chatRoomName;

    var div2 = document.createElement("div");

    var declineButton = document.createElement("button");
    declineButton.className = "btn btn-danger";
    declineButton.innerHTML = "Reject";
    declineButton.onclick = function () {
        sendApproveRequest(data.chatRoomID, data.userID, false);
        var liNode = document.getElementById("joinedRequestUserID"+data.userID.toString()+"roomID"+data.chatRoomID.toString());
        document.getElementById("approveRequest").removeChild(liNode);
    }

    var approveButton = document.createElement("button");
    approveButton.className = "btn btn-primary";
    approveButton.innerHTML = "Approve";
    approveButton.onclick = function () {
        sendApproveRequest(data.chatRoomID, data.userID, true);
        var liNode = document.getElementById("joinedRequestUserID"+data.userID.toString()+"roomID"+data.chatRoomID.toString());
        document.getElementById("approveRequest").removeChild(liNode);
    }

    div2.appendChild(declineButton);
    div2.appendChild(approveButton);
    div1.appendChild(strong);
    a.appendChild(div1);
    a.append(div2);
    li.appendChild(a);

    document.getElementById("approveRequest").appendChild(li);
}


/**
 * updateChatRoomUserList.
 */
function updateChatRoomUserList(data) {
    var userList = document.getElementById("userList");
    while (userList.hasChildNodes()) {
        userList.removeChild(userList.firstChild);
    }

    var sendMembers = document.getElementById("sendToMembers");
    while (sendMembers.hasChildNodes()) {
        sendMembers.removeChild(sendMembers.firstChild);
    }

    var allOption = document.createElement("option");
    allOption.value = "0";
    allOption.innerHTML = "All";
    document.getElementById("sendToMembers").appendChild(allOption);

    var isAdmin = false;
    data.forEach(function (jsonData) {
        if (jsonData.isAdmin == true && jsonData.userID == userID) {
            isAdmin = true;
        }
    });

    data.forEach(function (jsonData) {
        var item = document.createElement("a");
        item.href = "#";
        item.className = "list-group-item";
        item.style = "padding-left:10px";

        var name = document.createElement("div");
        name.innerHTML = jsonData.userName;
        if (jsonData.isAdmin == true) {
            var node = document.createElement("span");
            node.className = "glyphicon glyphicon-user";
            node.style = "float:right";
            name.appendChild(node);
        } else {
            if (isAdmin == true) {
                var banButton = document.createElement("button");
                banButton.style = "float:right";
                if (jsonData.isBanned == true) {
                    banButton.className = "btn btn-danger btn-xs";
                } else {
                    banButton.className = "btn btn-light btn-xs";
                }
                var banIcon = document.createElement("span");
                banIcon.className = "glyphicon glyphicon-ban-circle";
                banButton.appendChild(banIcon);
                banButton.onclick = function(){
                    sendBanRequest(jsonData.userID);
                }
                name.appendChild(banButton);
            } else {
                if (jsonData.isBanned == true) {
                    var banButton = document.createElement("button");
                    banButton.style = "float:right";
                    banButton.className = "btn btn-danger btn-xs disable";
                    var banIcon = document.createElement("span");
                    banIcon.className = "glyphicon glyphicon-ban-circle";
                    banButton.appendChild(banIcon);
                    name.appendChild(banButton);
                }
            }
        }

        item.appendChild(name);
        document.getElementById("userList").appendChild(item);

        var option = document.createElement("option");
        option.value = jsonData.userID.toString();
        option.innerHTML = jsonData.userName + " (" + jsonData.userID.toString() + ")";
        document.getElementById("sendToMembers").appendChild(option);
    })
}

/**
 * Send Register Information form register.html.
 */
function sendRegisterInfo() {
    webSocket.send(JSON.stringify({
        request: "register",
        data: {
            userName: localStorage.username,
            age: localStorage.age,
            school: localStorage.school,
            interest: [localStorage.interest1, localStorage.interest2]
        }
    }));

}


function EditMessage(messageNode) {
    localStorage.editID = messageNode.parentNode.parentNode.parentNode.parentNode.id;
    localStorage.senderId = messageNode.parentNode.parentNode.parentNode.parentNode.name;
}

function messageEditFinished() {
    webSocket.send(JSON.stringify({
        request: "edit_msg",
        data: {
            chatRoomID: currentChatRoomID,
            executorID: userID,
            senderID: parseInt(localStorage.senderId),
            messageID: parseInt(localStorage.editID),
            newContent: $("#messageEdit").val()
        }
    }));
}

function editMessage(data) {
    document.getElementById("p" + data.messageID.toString()).innerHTML = data.content;

}

function reportRequest() {
    webSocket.send(JSON.stringify({
        request: "report",
        data: {
            chatRoomID: currentChatRoomID,
            userID: localStorage.senderId,
            reason: $("#reportContent").val()
        }
    }));
}

function backToLogin() {
    localStorage.clear();
}