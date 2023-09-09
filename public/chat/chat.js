const socket = new WebSocket("ws://localhost:25465");
let logged = false;
let accountid = undefined;
let admin= false;
let urlparams = new URLSearchParams(window.location.search);
let chatid = undefined;
let chatdesc = document.getElementById("chatdesc");
let chatusername = document.getElementById("chatname");
let sendmsg = document.getElementById("sendmessage");
let inputmsg = document.getElementById("inputmsg");

if(urlparams.has("chat")){
    chatid = urlparams.get("chat");
}

socket.addEventListener("open", (e) => {
    if(logged){
        if(chatid !== undefined){
            socket.send('{"type":"LOADMESSAGES", "chat": ' + chatid + '}');
        }

        checkAdmin()
    }
    updateChat()
})
socket.addEventListener("message", (m) => {
    let payload = JSON.parse(m.data);

    if(payload.type === "ADMIN"){
        admin = payload.is;
        setAdminMode()
    } else if(payload.type === "CHATLIST"){
        for (let i = 0; i < payload.chats.length; i++) {
            let info = payload.chats[i];
            if(chatid !== undefined){
                chatid = info.uid;
                chatusername.innerText = info.username;
                chatdesc.innerText = info.title +" | "+info.desc;
                socket.send('{"type":"LOADMESSAGES", "chat": ' + chatid + '}');
            } else if(chatid === info.uid){
                chatusername.innerText = info.title;
                chatdesc.innerText = info.desc;
            }
            let chat = document.createElement("a");
            chat.setAttribute("href", "chat.html?chat="+info.uid);
            chat.setAttribute("class", "d-flex align-items-center");
            chat.innerHTML = `<div class="flex-shrink-0">
                                                            <img class="img-fluid"
                                                                 src="https://mehedihtml.com/chatbox/assets/img/user.png"
                                                                 alt="user img">
                                                        </div>
                                                        <div class="flex-grow-1 ms-3">
                                                            <h3>`+info.username+`</h3>
                                                            <p>`+info.title+`</p>
                                                        </div>`
            if(info.owner === accountid) {
                if (info.state === "OPEN") {
                    document.getElementById("Open").appendChild(chat);
                } else if (info.state === "CLOSED") {
                    document.getElementById("Closed").appendChild(chat);
                }
            } else {
                document.getElementById("Suporte").appendChild(chat);
            }
        }
    } else if(payload.type === "UNREAD"){
        for (let i = 0; i < payload.messages.length; i++) {
            let info = payload.messages[i];
            let message = document.createElement("li");
            message.setAttribute("class", (info.owner === accountid ? "repaly" : "sender"));
            message.innerHTML = `<p> `+info.message+` </p>
                                                <span class="time">`+info.time+`</span>`
            document.getElementById("msgslist").appendChild(message);
        }
    } else if(payload.type === "MESSAGES"){
        for (let i = 0; i < payload.messages.length; i++) {
            let info = payload.messages[i];
            let message = document.createElement("li");
            message.setAttribute("class", (info.owner === accountid ? "repaly" : "sender"));
            message.innerHTML = `<p> `+info.message+` </p>
                                                <span class="time">`+info.time+`</span>`
            document.getElementById("msgslist").appendChild(message);
        }
    }
})

function checkAdmin(){
    socket.send('{"type":"CHECKADMIN", "uid": ' + accountid + '}');
}

function setAdminMode(){
    let sppLi = document.createElement("li");
    sppLi.setAttribute("class", "nav-item")
    sppLi.setAttribute("role", "presentation")
    sppLi.innerHTML = `<button class="nav-link" id="Suporte-tab" data-bs-toggle="tab"
                                                    data-bs-target="#Suporte" type="button" role="tab"
                                                    aria-controls="Suporte" aria-selected="false">Suporte
                                            </button>`
    document.getElementById("myTab").appendChild(sppLi);
}

function updateChat(){
    if(logged) {
        socket.send('{"type":"UPDATECHAT", "uid": ' + accountid + ', "user":' + accountid + '}');
        if (chatid !== undefined) {
            socket.send('{"type":"UPDATEUNREADMESSAGES", "chat": ' + chatid + ', "user":' + accountid + '}');
        }
        if (admin) {
            socket.send('{"type":"UPDATECHAT", "uid":"admin"}');
        }
    }
    setTimeout(updateChat, 100)
}

function sendMessage(){
    if(inputmsg.value != "" && chatid !== undefined && logged){
        socket.send('{"type":"SENDMSG", "uid":"'+chatid+'", "message":'+inputmsg.value+', "user":'+accountid+' }');
    }
}

sendmsg.addEventListener("click", sendMessage)