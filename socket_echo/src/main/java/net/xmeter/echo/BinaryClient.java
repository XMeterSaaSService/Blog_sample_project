package net.xmeter.echo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class BinaryClient implements Constants{
	private static byte[] produceData() {
		byte[] data = new byte[]{START_DELIMITER, 0x3, TYPE_TEMPERATURE, 1, TYPE_BRIGHTNESS, 20, TYPE_HUMIDITY, 30};
		long checksum = BinaryServer.calculateChecksum(data);
		byte[] ret = new byte[data.length + 1];
		System.arraycopy(data, 0, ret, 0, data.length);
		ret[ret.length - 1] = (byte) checksum;
		System.out.println(bytesToHex(ret));
		return ret;
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	
	public static void main(String[] args) throws Exception {
		Socket server;
		server = new Socket(InetAddress.getLocalHost(), 4700);
		InputStream stream = server.getInputStream();
		OutputStream os = server.getOutputStream();
		int count = 0;
		while (true) {
			if(count == 3) {
				break;
			}
			os.write(produceData());
			os.flush();
			byte[] data = new byte[2014];
			int length = stream.read(data);
			if(length != -1) {
				byte[] tmp = new byte[length - 2];
				System.arraycopy(data, 0, tmp, 0, length - 2);
				System.out.println(bytesToHex(tmp));	
			}
			TimeUnit.SECONDS.sleep(1);
			count++;
		}
		server.close();
	}
}
