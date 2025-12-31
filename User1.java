package Spring.codeio.CRUDOP.ChatAPP;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class User1 extends Frame implements Runnable, ActionListener {
    TextField textField;
    TextArea textArea;
    Button send;
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream input;
    DataOutputStream output;
    Thread chat;

    User1() {
        setTitle("User1 Chat Server");
        setSize(600, 500);
        setLayout(null);
        setBackground(Color.decode("#075E54"));

        // Text Area
        textArea = new TextArea();
        textArea.setBounds(50, 50, 500, 300);
        add(textArea);

        // Text Field
        textField = new TextField();
        textField.setBounds(50, 370, 400, 40);
        add(textField);

        // Send Button
        send = new Button("Send");
        send.setBounds(470, 370, 80, 40);
        send.setBackground(Color.decode("#25D366"));
        send.setForeground(Color.white);
        send.addActionListener(this);
        add(send);

        // Window close handler
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                try {
//                    if (socket != null) socket.close();
//                    if (serverSocket != null) serverSocket.close();
//                } catch (Exception ex) {
//                    System.out.println(ex);
//                }
//                System.exit(0);
//            }
//        });

        setVisible(true);

        // Start the server connection in a separate thread
        new Thread(() -> {
            try {
                textArea.append("Waiting for connection...\n");
                serverSocket = new ServerSocket(8080);
                socket = serverSocket.accept();
                textArea.append("User2 connected!\n");

                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                // Start chat thread
                chat = new Thread(this);
                chat.setDaemon(true);
                chat.start();
            } catch (Exception e) {
                textArea.append("Error: " + e + "\n");
            }
        }).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String msg = textField.getText().trim();
            if (!msg.isEmpty()) {
                textArea.append("You: " + msg + "\n");
                output.writeUTF(msg);
                output.flush();
                textField.setText("");
            }
        } catch (Exception ex) {
            textArea.append("Error sending: " + ex + "\n");
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = input.readUTF();
                textArea.append("User2: " + msg + "\n");
            } catch (Exception e) {
                textArea.append("\n");
                break;
            }
        }
    }

    public static void main(String[] args) {
        new User1();
    }
}
