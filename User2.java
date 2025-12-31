package Spring.codeio.CRUDOP.ChatAPP;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class User2 extends Frame implements Runnable, ActionListener {
    TextField textField;
    TextArea textArea;
    Button send;
    Socket socket;
    DataInputStream input;
    DataOutputStream output;
    Thread chat;

    User2() {
        setTitle("User2 Chat Client");
        setSize(600, 500);
        setLayout(null);
        setBackground(Color.decode("#128C7E"));

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
//                } catch (Exception ex) {
//                    System.out.println(ex);
//                }
//                System.exit(0);
//            }
//        });

        setVisible(true);

        // Connect to the server in a background thread
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 8080);
   ;             textArea.append("Connected to User1!\n");

                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                // Start receiving messages
                chat = new Thread(this);
                chat.setDaemon(true);
                chat.start();
            } catch (Exception e) {
                textArea.append("Connection failed: " + e + "\n");
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
                textArea.append("User1: " + msg + "\n");
            } catch (Exception e) {
                textArea.append("Connection closed.\n");
                break;
            }
        }
    }

    public static void main(String[] args) {
        new User2();
    }
}
