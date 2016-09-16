package net.xmeter;

import java.io.IOException;
import java.text.MessageFormat;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/echo")
public class EchoService {
	private Session session;
	
	@OnOpen
	public void start(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}
	
	@OnError
	public void onError(Session session, Throwable t) {
		System.out.println(MessageFormat.format("Find exception {0} for web-socket session {1}.", t.getMessage(), session.getId()));
		if(!session.isOpen()) {
			System.out.println(MessageFormat.format("The web-socket {0} was already closed, now is going to remove the notification listeners.", session.getId()));
		}
	}

	@OnMessage
	public void onMessage(Session session, String msg, boolean last) {
		try {
			String message = "server echo, " + msg.toString();
			System.out.println(message);
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				session.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
