var socket;
var audioCtx;
var queue = new Array();
var startFlag = true;
var receiveCnt = 0;

window.addEventListener('load', initAudio, false);

function initAudio() {
	try {
		window.AudioContext = window.AudioContext || window.webkitAudioContext;
		audioCtx = new AudioContext();
		console.log("Web AudioContext Create");
	} catch (e) {
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
		})
	} else {
		console.log("START FLAG ON");
		startFlag = true;
	}
}

function webSocketConnection(){
	var serverIP = "ws://" + prompt("Server IP 주소 입력해주세요") + ":80";
	var project = document.getElementById('project');
	var success = document.getElementById('success');
	var msgCnt = document.getElementById('msgCnt');
	
	socket = new WebSocket(serverIP);
	socket.binaryType = 'arraybuffer';
	socket.onopen = function(event) {
		console.log("Socket Open Success");
		project.style.display = 'none';
		success.style.display = 'block';
	};
	socket.onerror = function(event) {
		alert("Socket Open Error Retry");
		console.log("Socket Open Error");
	};
	socket.onmessage = function(msg) {
		receiveCnt++;
		console.log("MSG CNT : " + receiveCnt);
		if (msg.data instanceof ArrayBuffer) {
			console.log("MSG ARRIVE");
			queue.push(msg.data);
			console.log("QUEUE PUSH");
			console.log("QUEUE Length : " + queue.length);
			if (startFlag && queue.length > 3) {
				console.log("START FLAG OFF");
				startFlag = false;
				play();				
			}
		}
	};
}
