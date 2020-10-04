import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectInputStream inputStream; //input stream from client
    private ObjectOutputStream outputStream; //output stream to client
    private ServerSocket serverSocket;
    private Socket connection;
    private  int counter = 1; //counter of number of connections

    public Server(){
        super("Server");
        enterField = new JTextField();
        enterField.setEditable(false);
        enterField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //send message to client
                sendData(e.getActionCommand());
                enterField.setText("");
            }
        });

        add(enterField, BorderLayout.NORTH);
        displayArea = new JTextArea();
        add(new JScrollPane(displayArea),BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
        }


    //set up and run server
    public void runServer(){
        try {
            serverSocket = new ServerSocket(12345, 100); //create ServerSocket

            while (true){
                try {
                    waitForConnection();
                    getStreams(); //get input and output streams
                    processConnection();
                }catch (EOFException eofException){
                    displayMessage("\nServer terminated connection");
                }
                finally {
                    closeConnection();
                    ++counter;
                }
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    //wait for connection then display connection info
    private void waitForConnection(){
        displayMessage("Waiting for connection\n");
        connection = serverSocket.accept(); //allow server to accept connection
        displayMessage("Connection " + counter + " received from: " + connection.getInetAddress().getHostName());
    }

    private void getStreams() {
    }

    private void processConnection() {
    }

    private void sendData(String actionCommand) {
    }

    private void closeConnection() {
    }

    private void displayMessage(String s) {
        System.out.println(s);
    }

}
