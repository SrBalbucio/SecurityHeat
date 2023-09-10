const socket = new WebSocket("ws://localhost:25465");
let logged = false;
let chatloaded = false;
let accountid = undefined;
let admin= false;
let urlparams = new URLSearchParams(window.location.search);
let chatid = undefined;
let chatdesc = document.getElementById("chatdesc");
let chatusername = document.getElementById("chatname");
let sendmsg = document.getElementById("sendmessage");
let inputmsg = document.getElementById("inputmsg");
let allchats = []
let allmessages = []

if(urlparams.has("chat")){
    chatid = urlparams.get("chat");
}

socket.addEventListener("open", (e) => {
    if(logged){
        if(chatid !== undefined){
            socket.send('{"type":"LOADMESSAGES", "chat": ' + chatid + ', "user": '+accountid+'}');
        } else{
            socket.send('{"type":"GETFIRSTCHAT", "user": ' + accountid + '}');
        }

        checkAdmin()
    }
    updateChat()
    updateMessages()
})
socket.addEventListener("message", (m) => {
    let payload = JSON.parse(m.data);

    if(payload.type === "ADMIN"){
        admin = payload.is;
        if(admin) {
            setAdminMode()
        }
    } else if(payload.type === "CHATLIST"){
        for (let i = 0; i < payload.chats.length; i++) {
            let info = payload.chats[i];
            if (!allchats.includes(info.uid)) {
                allchats.push(info.uid);
                let foradm = payload.adminonly;
                let chat = document.createElement("a");
                chat.setAttribute("href", "chat.html?chat=" + info.uid);
                chat.setAttribute("class", "d-flex align-items-center");
                if (!foradm) {
                    chat.innerHTML = `<div class="flex-shrink-0">
                                                            <img class="img-fluid groupimg"
                                                                 src="`+info.img+`"
                                                                 alt="user img">
                                                        </div>
                                                        <div class="flex-grow-1 ms-3">
                                                            <h5>` + info.title + `</h5>
                                                            <p>` + info.desc + `</p>
                                                        </div>`
                } else {
                    chat.innerHTML = `<div class="flex-shrink-0">
                                                            <img class="img-fluid groupimg"
                                                                 src="`+info.img+`"
                                                                 alt="user img">
                                                        </div>
                                                        <div class="flex-grow-1 ms-3">
                                                            <h5>` + info.username + `</h5>
                                                            <p>` + info.title + `</p>
                                                        </div>`
                }

                if (chatid === undefined) {
                    chatid = info.uid;
                    socket.send('{"type":"LOADMESSAGES", "chat": ' + chatid + ', "user": ' + accountid + '}');
                }

                if (chatid === info.uid) {
                    if (foradm) {
                        chatusername.innerText = info.username;
                        chatdesc.innerText = info.title + " | " + info.desc;
                    } else if (chatid === info.uid) {
                        chatusername.innerText = info.title;
                        chatdesc.innerText = info.desc;
                        if(info.category.includes("updatechannel")){
                            sendmsg.setAttribute("disabled", "");
                            inputmsg.setAttribute("disabled", "");
                        }
                    }
                }

                if (!foradm) {
                    if (info.state === "OPEN") {
                        document.getElementById("Open").appendChild(chat);
                    } else if (info.state === "CLOSED") {
                        document.getElementById("Closed").appendChild(chat);
                    }
                } else {
                    document.getElementById("Suporte").appendChild(chat);
                }
            }
        }
    } else if(payload.type === "UNREAD"){
        for (let i = 0; i < payload.messages.length; i++) {
            let info = payload.messages[i];
            if(!allmessages.includes(info.uid)) {
                let message = document.createElement("li");
                message.setAttribute("class", (info.owner === accountid ? "repaly" : "sender"));
                message.innerHTML = `<p> ` + info.message + ` </p>
                                                <span class="time">` + info.time + `</span>`
                document.getElementById("msgslist").appendChild(message);
                allmessages.push(info.uid);
            }
        }
    } else if(payload.type === "MESSAGES"){
        for (let i = 0; i < payload.messages.length; i++) {
            let info = payload.messages[i];
            if(!allmessages.includes(info.uid)) {
                let message = document.createElement("li");
                message.setAttribute("class", (info.owner === accountid ? "repaly" : "sender"));
                message.innerHTML = `<p> ` + info.message + ` </p>
                                                <span class="time">` + info.time + `</span>`
                document.getElementById("msgslist").appendChild(message);
                allmessages.push(info.uid);
            }
        }
    } else if(payload.type === "FIRSTCHAT"){
        window.location.href = window.location.href + "?chat="+payload.chatid;
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

function updatePage(){
    window.location.reload();
}

function updateChat(){
    if(logged) {
        chatloaded = true;
        socket.send('{"type":"UPDATECHAT", "uid": ' + accountid + ', "user":' + accountid + '}');
        if (admin) {
            socket.send('{"type":"UPDATECHAT", "uid":"admin"}');
        }
    }
    setTimeout(updateChat, 10000)
}

function updateMessages() {
    if(logged) {
        if (chatid !== undefined) {
            socket.send('{"type":"LOADMESSAGES", "chat": ' + chatid + ', "user": ' + accountid + '}');
        }
    }
    setTimeout(updateMessages, 500)
}

function sendMessage(){
    if(inputmsg.value != "" && chatid !== undefined && logged){
        socket.send('{"type":"SENDMSG", "uid":"'+chatid+'", "message":'+inputmsg.value+', "user":'+accountid+' }');
    }
    inputmsg.value = "";
}

sendmsg.addEventListener("click", sendMessage)