package com.xcinfo.xc_blueteeth_android.main.uploading;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * TCP socket client;
 * 
 * @author jenny
 * 
 */
public class TCPSocketClient {

	private Socket socket;
	private SocketAddress socketAddress;
	private int connectTimeout = 1000 * 60;// 10 sec
	private int getInputStremTimeout = 1000 * 60;// 2sec
	public InputStream inputStream;

	/**
	 * connect
	 * 
	 * @param ip
	 *            dst ip
	 * @param port
	 *            dst port
	 * @throws IOException
	 */
	public void connect(String ip, int port) throws IOException {
		if (ip != null || port != 0) {
//			LogUtils.d("socket ip=" + ip.toString().trim());
			socketAddress = new InetSocketAddress(ip.toString().trim(), port);
			socket = new Socket();
			socket.connect(socketAddress, connectTimeout);
			socket.setSoTimeout(getInputStremTimeout);
		}
	}

	/**
	 * close socket
	 * 
	 * @throws IOException
	 *             close sokcet throw ioexception
	 */

	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * socket is connected
	 * 
	 * @return whether or not connected server
	 */
	public boolean isConnected() {
		if (socket != null) {
			return socket.isConnected();
		} else {
			return false;
		}
	}

	/**
	 * send data package
	 * 
	 * @param data
	 *            send server data
	 * @throws IOException
	 *             socket get outputstream throw ioexception
	 */
	public void send(byte[] data) throws IOException {
		if (null != data && socket != null) {
			OutputStream outStream = socket.getOutputStream();
			outStream.write(data);
			outStream.flush();
		}
	}

	/**
	 * receive data package
	 * 
	 * @throws IOException
	 *             sokcet get inputstream throw ioexception
	 */
	public InputStream receive() throws IOException {
		if (socket != null) {
			inputStream = socket.getInputStream();
			return inputStream;
		}
		return null;
	}

	public InputStream getInputStream() {
		return this.inputStream;
	}

	public void setInputStremTimeout(int timeout) {
		this.getInputStremTimeout = timeout;
	}

	public void setConnectTimeout(int timeout) {
		this.connectTimeout = timeout;
	}

}