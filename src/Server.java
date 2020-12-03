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
    private void waitForConnection() throws IOException {
        displayMessage("Waiting for connection\n");
        connection = serverSocket.accept(); //allow server to accept connection
        displayMessage("Connection " + counter + " received from: " + connection.getInetAddress().getHostName());
    }

    //get streams to send abd receive data
    private void getStreams() throws IOException {
        //setting up output streams for objects
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush(); //flush output buffer to send header information

        //set up input streams for objects
        inputStream = new ObjectInputStream(connection.getInputStream());
        displayMessage("\nGot I/O streams\n");
    }

    //process connection with client
    private void processConnection() throws IOException {
        String message = "Connection successful";
        sendData(message);

        //enable enterfield so server user can send messages
        setTextFieldEditable(true);
        do { //process messages sent from client
            try {
                message = (String) inputStream.readObject();
                displayMessage("\n" + message);
            } catch (ClassNotFoundException classNotFoundException) {
                displayMessage("\nUnknown object type received ");
            }

        }while (!message.equals("CLIENT>>> TERMINATE"));
    }

    private void setTextFieldEditable(boolean b) {
    }

    //send message to client
    private void sendData(String message) {
        try { //send object to client
            outputStream.writeObject("SERVER>>> "+ message);
            outputStream.flush();
            displayMessage("\nSERVER>>>  "+ message);
        } catch (IOException e) {
            displayArea.append("\nError writing message");
        }
    }

    //close streams and socket
    private void closeConnection() {
        displayMessage("\nTerminating connection...\n");
        setTextFieldEditable(false);

        try {
            outputStream.close();
            inputStream.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayMessage(String s) {
        System.out.println(s);
    }

}
