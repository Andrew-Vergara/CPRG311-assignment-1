package client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import problemdomain.InputListener;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientGUI.
 */
public class ClientGUI extends JFrame implements PropertyChangeListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3981533572116209201L;

	/** The connect. */
	private JButton connect;

	/** The disconnect. */
	private JButton disconnect;

	/** The send. */
	private JButton send;

	/** The board. */
	private JTextArea messageBoard;

	/** The message. */
	private JTextField message;

	/** The socket. */
	private Socket socket = null;

	/** The outputStream. */
	private ObjectOutputStream outputStream = null;

	/** The listener. */
	private InputListener listener;

	/** The client. */
	Thread client;

	/**
	 * Instantiates a new client GUI.
	 */
	public ClientGUI() {
		setTitle("GUI Message Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		JPanel northPanel = createNorthPanel();
		add(northPanel, BorderLayout.NORTH);

		JPanel centerPanel = createCenterPanel();
		add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = createSouthPanel();
		add(southPanel, BorderLayout.SOUTH);
	}

	/**
	 * Creates the north panel.
	 *
	 * @return the j panel
	 */
	private JPanel createNorthPanel() {
		JPanel connectionPanel = new JPanel();
		connectionPanel.setLayout(new GridLayout(1, 2));

		connect = new JButton("Connect");
		connectionPanel.add(connect);
		connect.addActionListener(new ConnectAction(this));

		disconnect = new JButton("Disconnect");
		disconnect.setEnabled(false);
		connectionPanel.add(disconnect);
		disconnect.addActionListener(new DisconnectAction());

		return connectionPanel;
	}

	/**
	 * Creates the center panel.
	 *
	 * @return the j panel
	 */
	private JPanel createCenterPanel() {
		JPanel boredPanel = new JPanel();
		boredPanel.setLayout(new BorderLayout());

		boredPanel.add(new JLabel("Message Board"), BorderLayout.NORTH);

		messageBoard = new JTextArea(20, 50);
		messageBoard.setEditable(false);
		boredPanel.add(messageBoard, BorderLayout.CENTER);

		return boredPanel;
	}

	/**
	 * Creates the south panel.
	 *
	 * @return the j panel
	 */
	private JPanel createSouthPanel() {
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());

		messagePanel.add(new JLabel("Message to Send:"), BorderLayout.NORTH);

		message = new JTextField();
		messagePanel.add(message, BorderLayout.CENTER);

		send = new JButton("Send");
		messagePanel.add(send, BorderLayout.SOUTH);
		send.addActionListener(new SendMessageAction());
		send.setEnabled(false);

		return messagePanel;
	}

	/**
	 * Display.
	 */
	public void display() {
		pack();
		setVisible(true);
	}

	/**
	 * The Class ConnectAction.
	 */
	private class ConnectAction implements ActionListener {

		/** The client GUI. */
		private ClientGUI clientGUI;

		/**
		 * Instantiates a new connect action.
		 *
		 * @param clientGUI the client GUI
		 */
		public ConnectAction(ClientGUI clientGUI) {
			this.clientGUI = clientGUI;
		}

		/**
		 * Action performed.
		 *
		 * @param event the event
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				socket = new Socket("localhost", 1234);

				listener = new InputListener(socket, 0);
				listener.addListener(this.clientGUI);

				client = new Thread(listener);
				client.start();

				disconnect.setEnabled(true);
				connect.setEnabled(false);
				send.setEnabled(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * The Class DisconnectAction.
	 */
	private class DisconnectAction implements ActionListener {

		/**
		 * Action performed.
		 *
		 * @param event the event
		 */
		public void actionPerformed(ActionEvent event) {
			try {
				OutputStream output = socket.getOutputStream();
				outputStream = new ObjectOutputStream(output);
				outputStream.writeObject("Disconnected");

				disconnect.setEnabled(false);
				connect.setEnabled(true);
				send.setEnabled(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * The Class SendMessageAction.
	 */
	private class SendMessageAction implements ActionListener {

		/**
		 * Action performed.
		 *
		 * @param event the event
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				OutputStream output = socket.getOutputStream();
				outputStream = new ObjectOutputStream(output);
				outputStream.writeObject(message.getText());

				messageBoard.append("Me: " + message.getText() + "\n");
				message.setText("");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Property change.
	 *
	 * @param event the event
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String msg = (String) event.getNewValue();
		if (msg.equals("Stay?")) {
			int n = JOptionPane.showConfirmDialog(this, "Would you like to wait for a new connection?",
					"Client disconnected", JOptionPane.YES_NO_OPTION);
			System.out.println(n);

			try {
				OutputStream output = socket.getOutputStream();
				outputStream = new ObjectOutputStream(output);
				outputStream.writeObject("disconnected");

				if (n > 0) {
					disconnect.setEnabled(false);
					connect.setEnabled(true);
					send.setEnabled(false);
				} else {
					socket = new Socket("localhost", 1234);

					listener = new InputListener(socket, 0);
					listener.addListener(this);

					client = new Thread(listener);
					client.start();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			messageBoard.append("Client: " + msg + "\n");
		}
	}
}
