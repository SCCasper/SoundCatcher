//window.onload
window.AudioContext = window.AudioContext || window.webkitAudioContext;

var audioCtx = new AudioContext();

var webSocketWorker = new Worker("assets/js/webSocket.js");

var queue = [];

var startFlag = true;

webSocketWorker.onmessage = function(msg) {

	if (msg.data instanceof ArrayBuffer) {
		console.log("MSG ARRIVE");
		queue.push(msg.data);
		console.log("QUEUE PUSH");
		if (startFlag) {
			console.log("START FLAG");
			playingSound();
			startFlag = false;
		}
	}
}

function playingSound() {
	var audioData = queue.shift();
	console.log(queue.length);
	console.log("QUEUE GET DATA");
	if (audioData instanceof ArrayBuffer) {
		console.log("RECURSIVE FUNCTION");
		audioCtx.decodeAudioData(audioData, function(audioBuffer) {
			console.log("RECURSIVE DECODE DATA");
			var src = audioCtx.createBufferSource();
			src.buffer = audioBuffer;
			src.connect(audioCtx.destination);
			src.start(audioCtx.currentTime, 0, audioBuffer.length);
			src.onended = playingSound();
		})
	}else{
		startFlag = true;
	}
}

function clientStart() {

}