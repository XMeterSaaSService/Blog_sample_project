package net.xmeter.echo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class SensorData {
	int type;
	int value;
}

public class BinaryServer implements Constants {
	public static AtomicInteger sessions = new AtomicInteger(0);

	public void handleRequest(final Socket socket) {
		ExecutorService executor = Executors.newSingleThreadExecutor();

		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					DataInputStream ds = new DataInputStream(socket.getInputStream());
					OutputStream os = socket.getOutputStream();
					int readEmptyContentCount = 0;
					while (true) {
						byte[] data = new byte[1024];
						int actualLen = ds.read(data);
						if (actualLen == -1) {
							TimeUnit.MICROSECONDS.sleep(100);
							readEmptyContentCount++;
							if(readEmptyContentCount == 50) {
								System.out.println("Probably the client side closed the connection, now close me as well.");
								socket.close();
								break;
							}
							continue;
						}
						byte[] tmp = new byte[actualLen];
						System.arraycopy(data, 0, tmp, 0, actualLen);
						int startDelimterIndex = -1;
						for (int i = 0; i < tmp.length; i++) {
							if (tmp[i] == START_DELIMITER) {
								startDelimterIndex = i;
								break;
							}
						}
						if (startDelimterIndex != -1) {
							System.out.println("Find the start delimiter at " + startDelimterIndex + ".");
							byte[] ret = parseData(startDelimterIndex, tmp);
							os.write(ret);
							os.flush();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						socket.close();
						int num = sessions.decrementAndGet();
						System.out.println("Now has " + num + " of conn.");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		});
	}

	public static long calculateChecksum(byte[] buf) {
		int length = buf.length;
		int i = 0;

		long sum = 0;
		long data;

		// Handle all pairs
		while (length > 1) {
			data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
			sum += data;
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}

			i += 2;
			length -= 2;
		}

		if (length > 0) {
			sum += (buf[i] << 8 & 0xFF00);
			if ((sum & 0xFFFF0000) > 0) {
				sum = sum & 0xFFFF;
				sum += 1;
			}
		}

		sum = ~sum;
		sum = sum & 0xFFFF;
		return sum;
	}

	/**
	 * The protocol: start_delimit|length[|data_type|data_val]|checksum
	 */
	private byte[] parseData(int startDelimiter, byte[] data) {
		if (!validateChecksum(startDelimiter, data)) {
			long checksum = calculateChecksum(new byte[] {START_DELIMITER, RESPONSE_ERR});
			System.out.println("Wrong data, invalid checksum. Return with err response code.");
			return new byte[] {START_DELIMITER, RESPONSE_ERR, (byte)checksum, '\n'};
		}
		
		int length = data[startDelimiter + 1];
		if(length == 0) {
			long checksum = calculateChecksum(new byte[] {START_DELIMITER, RESPONSE_ERR});
			System.out.println("Wrong data, invalid data length. Return with err response code.");
			return new byte[] {START_DELIMITER, RESPONSE_ERR, (byte)checksum, '\n'};
		}
		
		int dataCount = 0;
		for (int i = 2; i < data.length;) {
			int type = data[i++];
			int value = data[i++];
			dumpData(type, value);
			dataCount++;
			if(length == dataCount) {
				break;
			}
		}
		long checksum = calculateChecksum(new byte[] {START_DELIMITER, RESPONSE_OK});
		System.out.println("Correct data. Return with correct response code.");
		return new byte[] {START_DELIMITER, RESPONSE_OK, (byte)checksum, '\n'};
	}

	private void dumpData(int type, int data) {
		String desc = "Unknow type";
		if (type == TYPE_BRIGHTNESS) {
			desc = "brightness";
		} else if (type == TYPE_HUMIDITY) {
			desc = "humidity";
		} else if (type == TYPE_TEMPERATURE) {
			desc = "temperature";
		}
		System.out.println(MessageFormat.format("Received data {0} for sensor {1}.", data, desc));
	}

	private boolean validateChecksum(int startDelimiter, byte[] data) {
		byte[] tmp = new byte[data.length - 1];
		System.arraycopy(data, 0, tmp, 0, data.length - 1);
		byte checksum = (byte) calculateChecksum(tmp);
		return checksum == data[data.length - 1];
	}

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(4700);
			while (true) {
				Socket socket = server.accept();
				BinaryServer srv = new BinaryServer();
				srv.handleRequest(socket);
				int num = sessions.incrementAndGet();
				System.out.println("Received new conn, now totally has " + num + " of conn.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
