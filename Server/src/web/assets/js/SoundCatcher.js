var socket;
var audioCtx;
var queue = new Array();
var startFlag = true;
var receiveCnt = 0;


window.addEventListener("load", initAudio);

function initAudio() {
    try {
        window.AudioContext = window.AudioContext || window.webkitAudioContext || window.mozAudioContext || window.oAudioContext || window.msAudioContext;
        audioCtx = new AudioContext();
        console.log("Web AudioContext Create");
    } catch (e) {
				console.log("Web Audio API is not supported in this browser");
        alert('Web Audio API is not supported in this browser');
    }
}

function play() {
    var audioData = queue.shift();
    console.log("QUEUE GET DATA");
    console.log("QUEUE Length : " + queue.length);
    if (audioData instanceof ArrayBuffer) {
        audioCtx.decodeAudioData(audioData, function(audioBuffer) {
            var src = audioCtx.createBufferSource();
            src.buffer = audioBuffer;
            src.connect(audioCtx.destination);
            src.start();
            src.onended = play;
        });
    } else {
        console.log("START FLAG ON");
        startFlag = true;
    }
}

function webSocketConnection() {
    //var serverIP = "ws://" + prompt("Server IP 주소 입력해주세요") + ":80";
    var serverIP = "ws://" + document.domain + ":80"; // Fix to Server IP  ex) localhost/something? -> localhost
    var project = document.getElementById("project");
    var success = document.getElementById("success");
    var cons = document.getElementById("console");
    var msgCnt = document.getElementById("msgCnt");
    var queueLength = document.getElementById("queueLength");

    socket = new WebSocket(serverIP);
    socket.binaryType = "arraybuffer"; // A string indicating the type of binary data being transmitted by the connection

    socket.onopen = function(event) {
        console.log("Socket Open Success");
        cons.innerHTML += "Socket Open Success";
        project.style.display = 'none';
        success.style.display = 'block';
    };
    socket.onerror = function(event) {
        alert("Socket Open Error Retry");
        console.log("Socket Open Error");
        cons.innerHTML += "Socket Open Error";
    };
    socket.onmessage = function(msg) {
        receiveCnt++;
        console.log("MSG CNT : " + receiveCnt);
        msgCnt.innerHTML = "MSG CNT : " + receiveCnt;
        if (msg.data instanceof ArrayBuffer) {
            console.log("MSG ARRIVE");
            queue.push(msg.data);
            console.log("QUEUE PUSH");
            console.log("QUEUE Length : " + queue.length);
            queueLength.innerHTML = "QUEUE Length : " + queue.length;
            if (startFlag && queue.length > 3) {
                console.log("START FLAG OFF");
                startFlag = false;
                play();
            }
        }
    };
}
