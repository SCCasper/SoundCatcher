
var socket;
var audioCtx;
	//var addWorker = new Worker("add1to10.js");
	function clientStart() {
		/* init */
		initSocket();
		initAudio();
		socket.onopen = function(event) { //socket open callback			
			alert("Success");
		};
		socket.onerror = function(event) { //socket error callback
			alert("Error!");
		};

		socket.onmessage = function(msg) {
			alert("HELLo");
			if (msg.data instanceof ArrayBuffer) {
				var src = audioCtx.createBufferSource();

				audioCtx.decodeAudioData(msg.data, function(audioBuffer) {
					src.buffer = audioBuffer;
					src.connect(audioCtx.destination);
					src.start(0);
				})
			}
		};
	}

	function initSocket() {
		socket = new WebSocket("ws://113.198.84.72:80"); //open socket	
		socket.binaryType = 'arraybuffer'; //set arrayBuffer
	}
	function initAudio() {
		window.AudioContext = window.AudioContext || window.webkitAudioContext;
		audioCtx = new AudioContext();
	}
