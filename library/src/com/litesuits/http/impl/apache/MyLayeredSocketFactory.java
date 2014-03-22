package com.litesuits.http.impl.apache;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;

import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpParams;


/**
 * Android had a bug where HTTPS made reverse DNS lookups (fixed in Ice Cream Sandwich) 
 * http://code.google.com/p/android/issues/detail?id=13117
 * 
 * @author MaTianyu
 * 2014-1-10上午12:17:04
 */
public class MyLayeredSocketFactory implements LayeredSocketFactory{
	SSLSocketFactory delegate = SSLSocketFactory.getSocketFactory();

	@Override
	public Socket createSocket() throws IOException {
		return delegate.createSocket();
	}

	@Override
	public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort, HttpParams params)
			throws IOException {
		return delegate.connectSocket(sock, host, port, localAddress, localPort, params);
	}

	@Override
	public boolean isSecure(Socket sock) throws IllegalArgumentException {
		return delegate.isSecure(sock);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
		injectHostname(socket, host);
		return delegate.createSocket(socket, host, port, autoClose);
	}

	private void injectHostname(Socket socket, String host) {
		try {
			Field field = InetAddress.class.getDeclaredField("hostName");
			field.setAccessible(true);
			field.set(socket.getInetAddress(), host);
		} catch (Exception ignored) {}
	}
};