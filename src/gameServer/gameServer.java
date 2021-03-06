package gameServer;

import controller.Controller;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class gameServer {
    private ServerSocket serverSocket;
    private int numPlayer;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private Controller controller;

    public gameServer(){
        System.out.println("----Game Server----");
        numPlayer = 0;
            try{
                serverSocket = new ServerSocket(60000);
            }
            catch (IOException ex){
                System.out.println("IOException from Game Server");
            }
    }
    public void acceptConnection(){
        try {
            System.out.println("Waiting for connection....");
            while (numPlayer<2){
                Socket s = serverSocket.accept();
                numPlayer++;
                System.out.println("Player #" + numPlayer + " has connected");
                ServerSideConnection ssc = new ServerSideConnection(s,numPlayer);
                if (numPlayer == 1){
                    player1 = ssc;
                }
                else{
                    player2 = ssc;
                }
                Thread thread = new Thread(ssc);//2 player chay5 2 thread song song
                thread.start();
            }
        }
        catch (IOException ex){
            System.out.println("IOException from acceptConnection");
        }
    }
    private class ServerSideConnection implements Runnable{
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;
        private String btnTextP1;
        private String btnTextP2;

        //constructor
        public ServerSideConnection(Socket sock,int id){

            socket = sock;
            playerID = id;

            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            }
            catch (IOException ex){
                System.out.println("IOException from SSC constructor");
            }
        }

        @Override
        public void run() {
            try {
                dataOut.writeInt(playerID);
                dataOut.flush();
                while (true){
                    if (playerID == 1){
                        btnTextP1 = dataIn.readUTF();
                        player2.sendButton(btnTextP1);
                        System.out.println("Button player 1 ???? g???i");
                    }
                    else {
                        //lam sau
                        btnTextP2 = dataIn.readUTF();
                        player1.sendButton(btnTextP2);
                        System.out.println("Button player 2 ???? g???i");
                    }
                }
                /*if (controller.isEnd()){

                }

                 */
                //player1.closeConnectionSSC();
                //player2.closeConnectionSSC();
            }
            catch (IOException ex){
                System.out.println("IOException from run() method SSC");
            }
        }
        public void sendButton(String btnText){
            try {
                dataOut.writeUTF(btnText);
                dataOut.flush();
            }
            catch (IOException ex){
                System.out.println("IOException from sendButton SSC");
            }
        }
        public void closeConnectionSSC(){
            try {
                socket.close();
                System.out.println("Connection close");
            }
            catch (IOException ex){
                System.out.println("IOException from closeConnectionSSC");
            }
        }
    }

    public Object inputname(){
        JFrame frame = new JFrame();
        Object result = JOptionPane.showInputDialog(frame, "Enter name:");
        return result;
    }

    public static void main(String args[]){
        gameServer gs = new gameServer();//tao sever
        gs.acceptConnection();//moo73 cho client vo6
    }
}
