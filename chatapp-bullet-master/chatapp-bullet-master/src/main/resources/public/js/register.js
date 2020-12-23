'use strict';
let userID;
let currentChatRoomID = 999;

/**
 * Entry point into chat room
 */
window.onload = function () {
    $("#registerbutton").click(() => sendRegister());
};

/**
 * Send register page information request to the server.
 */
function sendRegister() {
    var forms = document.getElementsByClassName('needs-validation');
    var validation = Array.prototype.filter.call(forms, function (form) {
        form.addEventListener('submit', function (event) {
            if (form.checkValidity() === false) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    localStorage.username = document.getElementById("username").value;
    localStorage.age = document.getElementById("age").value;
    localStorage.interest1 = document.getElementById("interest1").value;
    localStorage.interest2 = document.getElementById("interest2").value;
    localStorage.school = document.getElementById("school").value;
    localStorage.ifRegistered = 1;

    //location.href = "/register.html";
}

