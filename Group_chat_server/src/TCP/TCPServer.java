package TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class TCPServer{

    public static int PORT = 6770;
    static Socket clientSocket;
    public static void main (String [] args)  {
        try{

                ServerSocket listenSocket = new ServerSocket(PORT);

                while(true) {
                    clientSocket = listenSocket.accept();
                    Connection c1 = new Connection(clientSocket);
//                    for(Connection c : c1.connections ){
//                        System.out.println(c.username);
//                    }

                    c1.start();

                }

        } catch(IOException e) {System.out.println("Listen :"+e.getMessage());}
        }
    }
    class Connection extends Thread{

        DataInputStream in;
        DataOutputStream out;
        Socket clientSocket;
        BufferedReader reader;
        BufferedWriter writer;
        public static ArrayList<Connection> connections  = new ArrayList<>();
        String username;
        static boolean  is_super_user = false;
        public Connection (Socket aClientSocket) {
            try {
                this.clientSocket = aClientSocket;
                this.in = new DataInputStream( clientSocket.getInputStream());
                this.out =new DataOutputStream( clientSocket.getOutputStream());
                writer = new BufferedWriter(new OutputStreamWriter(out));
                reader = new BufferedReader(new InputStreamReader(in));
                this.username = reader.readLine();
                connections.add(this);
                if(!is_super_user){
                    is_super_user=true;
                    this.writer.write("You are the super user that can remove any user from the chat");
                    this.writer.newLine();
                    this.writer.flush();
                }
                broadcast("Server: "+ username+" has entered the chat!");
            } catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
        }
        public void run(){
            try { // an echo server

                String message_from_client;
                while (clientSocket.isConnected()) {
                    message_from_client = reader.readLine();

                    if (is_super_user) {
                        if (message_from_client.contains("rmv")) {
                            this.writer.write("Which client would like to remove??");
                            this.writer.newLine();
                            this.writer.flush();
                            String remove_client = this.reader.readLine();
                            remove_spc_client(remove_client);
                        }

                    }
                    if (message_from_client.contains("@")) {
                        String[] client_username = message_from_client.split("@");
                        this.writer.write("You write now to " + client_username[1]);
                        this.writer.newLine();
                        this.writer.flush();
                        String message_client = reader.readLine();
                        individual_broadcast(client_username[1], message_client);
                    } else {
                        broadcast(message_from_client);
                    }


                }


            } catch(EOFException e) {System.out.println("EOF:"+e.getMessage());
            } catch(IOException e) {System.out.println("IO:"+e.getMessage());
            } finally {
                        try {
                            clientSocket.close();
                            remove_client();
                        }catch (IOException e){/*close failed*/}}

        }
        public void broadcast(String massage_to_clients){
            for(Connection c : connections){
                System.out.println(massage_to_clients);
                if(!c.username.equals(username)){
                    try {
//                        String messege_to_individual = reader.readLine();

                        c.writer.write(massage_to_clients);
                        c.writer.newLine();
                        c.writer.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        public void individual_broadcast(String client_username,String massage_to_clients) throws IOException {
            if(massage_to_clients !=null && ! client_username.equals(this.username)) {

                for (Connection c : connections) {
                    if (c.username.equals(client_username)) {
                        try {
                            c.writer.write(" (Direct message) "+massage_to_clients);
                            c.writer.newLine();
                            c.writer.flush();
                            return;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                this.writer.write("The requested client does not connected to the server");
            }
        }
        public void remove_client(){
            broadcast("Server: "+username +" has left the chat!!");
            connections.remove(this);

        }
        public void remove_spc_client(String remove_client) throws IOException {
            String [] split_username = remove_client.split(": ");
            for (int i =0; i<connections.size(); i++ ){
                if(connections.get(i).username.equals(split_username[1])){
                    System.out.println(split_username[1]);
                    connections.get(i).clientSocket.close();
//                    connections.get(i).interrupt();
//                    System.out.println(connections.get(i).isInterrupted());
                    connections.remove(connections.get(i));
                    System.out.println(connections.size());

                }
            }
        }

    }

