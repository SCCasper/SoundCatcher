package sender;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import debug.SCDebug;
import server.AudioBuffer;
import server.AudioSource;
import server.Stub;

public class WebSender extends Sender {
	private static final int FRAME_MAX_COUNT_IN_BLOCK = 10;
	
	// Wav Header
	private static final int WAV_HEADER_SIZE = 44;
	private static final int WAV_BLOCK_SIZE = AudioBuffer.AUDIO_BUFFER_SIZE * FRAME_MAX_COUNT_IN_BLOCK;
	private static final int WAV_BUFFER_SIZE = WAV_BLOCK_SIZE + WAV_HEADER_SIZE;
	
	private static final byte[] WAV_HEADER = new byte[WAV_HEADER_SIZE];

	private static final byte TEXT_DATA = (byte) 129;
	private static final byte BINARY_DATA = (byte) 130;

	private static final byte MSG_TYPE = BINARY_DATA;
	
	private int frameIdxInBlock = 0;
	private byte [] sendBuffer;
	
	private Socket client;
	private OutputStream out;
	private Stub stub;

	public WebSender(Socket client, Stub stub){
		this.client = client;
		this.stub = stub;
		this.sendBuffer = new byte[WAV_BUFFER_SIZE];	
		initWavHeader();
		setStream();
	}
	
	private void setStream(){
		try {
			this.out = client.getOutputStream();
			SCDebug.DebugMsg("WEB : SET STREAM ");
		} catch (IOException e) {
			SCDebug.DebugMsg("WEB : SET STREAM ERROR");
		}
	}

	private void initWavHeader() {
		int totalDataLen = WAV_BLOCK_SIZE + 36;

		// RIFF
		WAV_HEADER[0] = 'R';
		WAV_HEADER[1] = 'I';
		WAV_HEADER[2] = 'F';
		WAV_HEADER[3] = 'F';
		// Total File Size
		WAV_HEADER[4] = (byte) (totalDataLen & 0xff);
		WAV_HEADER[5] = (byte) ((totalDataLen >> 8) & 0xff);
		WAV_HEADER[6] = (byte) ((totalDataLen >> 16) & 0xff);
		WAV_HEADER[7] = (byte) ((totalDataLen >> 24) & 0xff);
		// Wave
		WAV_HEADER[8] = 'W';
		WAV_HEADER[9] = 'A';
		WAV_HEADER[10] = 'V';
		WAV_HEADER[11] = 'E';
		// fmt
		WAV_HEADER[12] = 'f';
		WAV_HEADER[13] = 'm';
		WAV_HEADER[14] = 't';
		WAV_HEADER[15] = ' ';
		// Header Size
		WAV_HEADER[16] = 16;
		WAV_HEADER[17] = 0;
		WAV_HEADER[18] = 0;
		WAV_HEADER[19] = 0;
		// Audio Format
		WAV_HEADER[20] = 1;
		WAV_HEADER[21] = 0;
		// Channel
		WAV_HEADER[22] = (byte) AudioSource.NUMBER_OF_CHANNELS;
		WAV_HEADER[23] = 0;
		// SAMPLE_RATE
		WAV_HEADER[24] = (byte) (AudioSource.INT_AUDIO_SAMPLERATE & 0xff);
		WAV_HEADER[25] = (byte) ((AudioSource.INT_AUDIO_SAMPLERATE >> 8) & 0xff);
		WAV_HEADER[26] = (byte) ((AudioSource.INT_AUDIO_SAMPLERATE >> 16) & 0xff);
		WAV_HEADER[27] = (byte) ((AudioSource.INT_AUDIO_SAMPLERATE >> 24) & 0xff);
		//
		WAV_HEADER[28] = (byte) ((AudioSource.INT_AUDIO_SAMPLERATE * AudioSource.NUMBER_OF_BITS_IN_CHANNEL
				* AudioSource.NUMBER_OF_CHANNELS / 8) & 0xff);

		WAV_HEADER[29] = (byte) (((AudioSource.INT_AUDIO_SAMPLERATE * AudioSource.NUMBER_OF_BITS_IN_CHANNEL
				* AudioSource.NUMBER_OF_CHANNELS / 8) >> 8) & 0xff);

		WAV_HEADER[30] = (byte) (((AudioSource.INT_AUDIO_SAMPLERATE * AudioSource.NUMBER_OF_BITS_IN_CHANNEL
				* AudioSource.NUMBER_OF_CHANNELS / 8) >> 16) & 0xff);

		WAV_HEADER[31] = (byte) (((AudioSource.INT_AUDIO_SAMPLERATE * AudioSource.NUMBER_OF_BITS_IN_CHANNEL
				* AudioSource.NUMBER_OF_CHANNELS / 8) >> 24) & 0xff);

		//
		WAV_HEADER[32] = (byte) ((AudioSource.NUMBER_OF_CHANNELS * 16) / 8);
		WAV_HEADER[33] = 0;
		// Bit Per Sample
		WAV_HEADER[34] = AudioSource.NUMBER_OF_BITS_IN_CHANNEL;
		WAV_HEADER[35] = 0;
		// Chunk Id
		WAV_HEADER[36] = 'd';
		WAV_HEADER[37] = 'a';
		WAV_HEADER[38] = 't';
		WAV_HEADER[39] = 'a';
		// Audio Data Size
		WAV_HEADER[40] = (byte) (WAV_BUFFER_SIZE & 0xff);
		WAV_HEADER[41] = (byte) ((WAV_BUFFER_SIZE >> 8) & 0xff);
		WAV_HEADER[42] = (byte) ((WAV_BUFFER_SIZE >> 16) & 0xff);
		WAV_HEADER[43] = (byte) ((WAV_BUFFER_SIZE >> 24) & 0xff);
		
		System.arraycopy(WAV_HEADER, 0, sendBuffer, 0, WAV_HEADER_SIZE);
	}

	@Override
	public void sendData(byte[] buffer) {	//WebSocket Send Message
		System.arraycopy(buffer, 0, sendBuffer, WAV_HEADER_SIZE + (frameIdxInBlock * AudioBuffer.AUDIO_BUFFER_SIZE), buffer.length);
		if(frameIdxInBlock < FRAME_MAX_COUNT_IN_BLOCK -1) {
			frameIdxInBlock = (frameIdxInBlock + 1) % FRAME_MAX_COUNT_IN_BLOCK;
			return;
		}
		frameIdxInBlock = (frameIdxInBlock + 1) % FRAME_MAX_COUNT_IN_BLOCK;
		
		int frameCount = 0;
		byte[] frame = new byte[10];

		frame[0] = MSG_TYPE;
		if (sendBuffer.length <= 125) {
			frame[1] = (byte) sendBuffer.length;
			frameCount = 2;
		} else if (sendBuffer.length >= 126 && sendBuffer.length <= 65535) {
			frame[1] = (byte) 126;
			int len = sendBuffer.length;
			frame[2] = (byte) ((len >> 8) & (byte) 255);
			frame[3] = (byte) (len & (byte) 255);
			frameCount = 4;
		} else {
			frame[1] = (byte) 127;
			int len = sendBuffer.length;
			frame[2] = (byte) ((len >> 56) & (byte) 255);
			frame[3] = (byte) ((len >> 48) & (byte) 255);
			frame[4] = (byte) ((len >> 40) & (byte) 255);
			frame[5] = (byte) ((len >> 32) & (byte) 255);
			frame[6] = (byte) ((len >> 24) & (byte) 255);
			frame[7] = (byte) ((len >> 16) & (byte) 255);
			frame[8] = (byte) ((len >> 8) & (byte) 255);
			frame[9] = (byte) (len & (byte) 255);
			frameCount = 10;
		}

		int bLength = frameCount + sendBuffer.length;

		byte[] reply = new byte[bLength];
		System.arraycopy(frame, 0, reply, 0, frameCount);
		System.arraycopy(sendBuffer, 0, reply, frameCount, sendBuffer.length);
		try {
			this.out.write(reply);
		} catch (IOException e) {
			try {
				SCDebug.DebugMsg("WEBSERVER : WEB CLIENT OUT");
				stub.removeDeliver(client.getInetAddress());
				client.close();
			} catch (IOException e1) {
				SCDebug.DebugMsg("WEBSERVER : WEB CLIENT CLOSE ERROR");
			}
		}
	}
	
}
