const socket = new WebSocket("ws://localhost:25465");
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let logged = false;
let accountid = undefined;
let admin = false;

socket.addEventListener("open", (e) => {
    if (urlParams.has("code") && !logged) {
        console.log("Identificado um codigo de login!")
        socket.send('{"type":"CODE", "code": "' + urlParams.get("code") + '", "state": "' + urlParams.get("state") + '"}')
        urlParams.delete("code")
        urlParams.delete("state")
    }
    if(logged){
        checkAdmin()
    }
})

socket.addEventListener("message", (m) => {
    let payload = JSON.parse(m.data);

    if (payload.type === "NEWSLETTER") {
        alert(payload.message);
    } else if (payload.type === "LOGIN_URL") {
        window.location.href = payload.url;
    } else if (payload.type === "LOGIN") {
        cookies.set('account', payload.value, {expires: 1})
        logged = true;
        accountid = payload.value;
    } else if(payload.type === "ERRO"){
        alert(payload.value);
    } else if(payload.type === "ACTION"){
        if(payload.action === 0){
            openChat();
        }
    } else if(payload.type === "ADMIN"){
        admin = payload.is;
    }
})

function openChat() {
    if (logged) {
        document.getElementById("site").style.display = "none";
        document.getElementById("chat-frame").style.display = "unset";
    } else {
        requestLogin(0)
    }
}

function closeChat() {
    document.getElementById("site").style.display = "unset";
    document.getElementById("chat-frame").style.display = "none";
}

function createChat(action) {
    if (logged) {
        openChat();
        socket.send('{"type":"NEWCHAT", "action": '+action+', "user": "'+accountid+'"}');
    } else {
        requestLogin(action)
    }
}

function requestLogin(value) {
    alert("Você ainda não está logado ou sua sessão expirou!\nO login é feito por meio do Discord, afim de evitar abusos.");
    socket.send('{"type":"LOGIN", "value": ' + value + '}');
}

function checkAdmin(){
    socket.send('{"type":"CHECKADMIN", "uid": ' + accountid + '}');
}

function registerNewsletter() {
    if (socket.readyState === socket.OPEN) {
        let input =
            document.getElementById("emailNews");
        socket.send('{"type":"NEWSLETTER", "email": ' + input.value + '}')
        input.value = "";
    }
}