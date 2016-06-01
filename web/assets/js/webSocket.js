var socket;

socket = new WebSocket("ws://127.0.0.1:80");
socket.binaryType = 'arraybuffer';

socket.onopen = function(event) {

};
socket.onerror = function(event) {
	
};

socket.onmessage = function(msg) {
	postMessage(msg.data);
};
