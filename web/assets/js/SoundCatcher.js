
window.AudioContext = window.AudioContext || window.webkitAudioContext;

var audioCtx = new AudioContext();

var webSocketWorker = new Worker("assets/js/webSocket.js");

webSocketWorker.onmessage = function(msg){
	if (msg.data instanceof ArrayBuffer) {
		var src = audioCtx.createBufferSource();

		audioCtx.decodeAudioData(msg.data, function(audioBuffer) {
			src.buffer = audioBuffer;
			src.connect(audioCtx.destination);
			src.start();
		})
	}
}

function clientStart() {

}