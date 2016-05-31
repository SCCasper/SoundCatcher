var socket;

socket = new WebSocket("ws://113.198.84.72:80");
socket.binaryType = 'arraybuffer';

socket.onopen = function(event) {

};
socket.onerror = function(event) {
	
};

socket.onmessage = function(msg) {
	postMessage(msg.data);
};
