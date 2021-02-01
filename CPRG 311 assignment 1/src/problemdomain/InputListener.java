package problemdomain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving input events.
 * The class that is interested in processing a input
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addInputListener<code> method. When
 * the input event occurs, that object's appropriate
 * method is invoked.
 *
 * @see InputEvent
 */
public class InputListener implements Runnable {
	
	/** The number. */
	private int number;
	
	/** The socket. */
	private Socket socket;
	
	/** The input stream. */
	private ObjectInputStream inputStream = null;
	
	/** The connect. */
	private boolean connect = true;
	
	/** The listeners. */
	private List<PropertyChangeListener> listeners = new ArrayList<>();
	
	/**
	 * Instantiates a new input listener.
	 *
	 * @param socket the socket
	 * @param number the number
	 */
	public InputListener(Socket socket, int number) {
		this.socket = socket;
		this.number = number;
	}
	
	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	public void addListener(PropertyChangeListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Disconnect.
	 */
	public void disconnect() {
		try {
			inputStream.close();
			socket.close();
			connect = false;
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the connect.
	 *
	 * @param status the new connect
	 */
	public void setConnect(boolean status) {
		this.connect = status;
	}
	
	/**
	 * Run.
	 */
	@Override
	public void run() {
		while(connect) {
			try {
				inputStream = new ObjectInputStream(socket.getInputStream());
				String msg = (String) inputStream.readObject();
				notifyListener("Client"+this.number, msg);
				if ( msg.equals("Disconnected") ) {
					disconnect();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Notify listener.
	 *
	 * @param property the property
	 * @param inputStream the input stream
	 */
	private void notifyListener(String property, String inputStream) {
		for( PropertyChangeListener listener : listeners ) {
			listener.propertyChange(new PropertyChangeEvent(this, property, null, inputStream));
		}
	}
}
