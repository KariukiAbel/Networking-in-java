import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends JFrame{
    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = ""; //message from server
    private String chatServer; //host server for this application
    private Socket client; //socket to communicate with the server

    //initiate chatServer and set up GUI
    public Client(String host){
        super("Client");
        chatServer = host; //set server to which this client connects to
        enterField = new JTextField();
        enterField.setEditable(false);
        enterField.addActionListener(new ActionListener() {
            //send message to server
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData(e.getActionCommand());
                enterField.setText("");
            }
        });

        add(enterField, BorderLayout.NORTH);
        displayArea = new JTextArea();
        add(new JScrollPane(displayArea), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }

    //connect to server and process messages from server
    public void runClient(){
        try{ //connect to server, get streams, process connection
            connectToServer(); // create a Socket to connection
            getStreams(); //get the input and output streams
            processConnection(); //process connection
        }catch (EOFException eofException){
            displayMessage("\nClient terminated connection");
        }catch (IOException ioException){
            ioException.printStackTrace();
        }finally {
            closeConnection();
        }
    }

    //connect to server
    private void connectToServer() throws IOException {
        displayMessage("Attempting connection\n");

        //create Socket to make connection to server
        client = new Socket(InetAddress.getByName(chatServer), 12345);

        //display connection information
        displayMessage("Connect to: " + client.getInetAddress().getHostName());
    }

    //get streams to send and receive data
    private void getStreams() throws IOException {
        //set up output streams for objects
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush(); //flush output buffer to send header information

        //set up input stream for objects
        input = new ObjectInputStream(client.getInputStream());
        displayMessage("\nGot I/O streams\n");
    }

    //process connection with server
    private void processConnection() throws IOException {
        //enable enterField so client user can send messages
        setTextEditable(true);

        do { //process messages sent from server
            try{ //read message and display it
                message = (String) input.readObject(); //read new message
                displayMessage("\n" + message);
            }catch (ClassNotFoundException classNotFoundException){
                displayMessage("\nUnknown object type received");
            }
        }while (!message.equals("SERVER>>> TERMINATE"));
    }

    //close streams and socket
    private void closeConnection() {
        displayMessage("\nClosing connection");
        setTextEditable(false);

        try {
            output.close();
            input.close();
            client.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //send message to server
    private void sendData(String actionCommand) {
        try {
            output.writeObject("CLIENT>>> " + actionCommand);
            output.flush(); //flush data to output
            displayMessage("\nClient>>> " + actionCommand);
        }catch (IOException ioException){
            displayArea.append("\nError writing object");
        }
    }

    //manipulates enterField in the event-dispatch thread
    private void setTextEditable(final boolean b) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { //sets enterField's editability
                enterField.setEditable(true);
            }
        });
    }

    //manipulates displayArea in the event-dispatch thread
    private void displayMessage(final String s) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { //updates displayArea
                displayArea.append(s);
            }
        });

    }




}