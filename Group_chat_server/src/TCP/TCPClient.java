package TCP;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
    BufferedReader reader;
    BufferedWriter writer;
    Socket socket;
    String username;

    public TCPClient(Socket socket, String username){
       try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(){
        try{
            writer.write(username);
            writer.newLine();
            writer.flush();

            Scanner scanner  = new Scanner(System.in);
            while(socket.isConnected()){
                String input = scanner.nextLine();
                writer.write(username+" send: "+ input);
                writer.newLine();
                writer.flush();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void mail_box(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String  message_from_clients;
                while(socket.isConnected()) {
                try {
                    message_from_clients = reader.readLine();
                    if(message_from_clients ==null)
                        System.exit(0);
                    System.out.println(message_from_clients);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) {

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your username!");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", TCPServer.PORT);
            TCPClient client = new TCPClient(socket, username);
            client.mail_box();
            client.send();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
