package problemdomain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
// TODO: Auto-generated Javadoc

/**
 * The Class ClientHandler.
 */
public class ClientHandler implements PropertyChangeListener {

	/** The socket 1. */
	private Socket socket1;

	/** The socket 2. */
	private Socket socket2;

	/** The outputStream 1. */
	private ObjectOutputStream outputStream1 = null;

	/** The outputStream 2. */
	private ObjectOutputStream outputStream2 = null;

	/** The listener 1. */
	private InputListener listener1;

	/** The listener 2. */
	private InputListener listener2;

	/** The client 1. */
	Thread client1;

	/** The client 2. */
	Thread client2;

	/**
	 * Instantiates a new client handler.
	 *
	 * @param s1 the s 1
	 * @param s2 the s 2
	 */
	public ClientHandler(Socket s1, Socket s2) {
		this.socket1 = s1;
		this.socket2 = s2;

		listener1 = new InputListener(socket1, 1);
		listener2 = new InputListener(socket2, 2);

		listener1.addListener(this);
		listener2.addListener(this);

		client1 = new Thread(listener1);
		client2 = new Thread(listener2);

		client1.start();
		client2.start();

		try {
			outputStream1 = new ObjectOutputStream(socket1.getOutputStream());
			outputStream1.writeObject((String) "Connected");
			outputStream2 = new ObjectOutputStream(socket2.getOutputStream());
			outputStream2.writeObject((String) "Connected");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Property change.
	 *
	 * @param event the event
	 */
	public void propertyChange(PropertyChangeEvent event) {
		try {
			String propertyChanged = event.getPropertyName();
			String ois = (String) event.getNewValue();
			String msg = ois;

			if (propertyChanged.equals("Client1")) {
				if (msg.equals("Disconnected")) {
					outputStream1 = new ObjectOutputStream(socket1.getOutputStream());
					outputStream1.writeObject(msg);

					if (client2.isAlive()) {
						outputStream2 = new ObjectOutputStream(socket2.getOutputStream());
						outputStream2.writeObject("Stay?");
					}
				} else {
					outputStream2 = new ObjectOutputStream(socket2.getOutputStream());
					outputStream2.writeObject(msg);
				}
			} else if (propertyChanged.equals("Client2")) {
				if (msg.equals("Disconnected")) {
					outputStream2 = new ObjectOutputStream(socket2.getOutputStream());
					outputStream2.writeObject(msg);

					if (client1.isAlive()) {
						outputStream1 = new ObjectOutputStream(socket1.getOutputStream());
						outputStream1.writeObject("Stay?");
					}
				} else {
					outputStream1 = new ObjectOutputStream(socket1.getOutputStream());
					outputStream1.writeObject(msg);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
