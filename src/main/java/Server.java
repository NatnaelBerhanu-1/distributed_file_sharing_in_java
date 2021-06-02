import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Server {
    private static ServerSocket servSock;
    private static final int PORT = 1234;
    static List<ClientHandler> clients = new ArrayList<>();
    static int id = 0;
    static HashMap<String, List<String>> allFiles = new HashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Openning port.....");
        listenSocket();
    }

    public static void listenSocket() {
        try {
            servSock=new ServerSocket(PORT);
            System.out.println("Socket created successfully");
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Unable to create socket with port no:1234!");
            System.exit(-1);
        }
        Socket link=null;
        while (true) {
            try {
                link=servSock.accept();
                System.out.println(link.getInetAddress());
                BufferedReader in=new BufferedReader(new InputStreamReader(link.getInputStream()));
                PrintWriter out=new PrintWriter(link.getOutputStream(),true);
                ClientHandler clientHandler = new ClientHandler(link, id, in, out);
                Thread t = new Thread(clientHandler);
                clients.add(clientHandler);
                id++;
                t.start();
            } catch(IOException e){
                System.out.println("Accept failed:Port 1234");
            }
        }

    }

    static class ClientHandler implements Runnable{
        Socket s;
        int userId;
        BufferedReader in;
        PrintWriter out;

        ClientHandler(Socket s, int id, BufferedReader in, PrintWriter out){
            this.s = s;
            this.userId = id;
            this.in = in;
            this.out = out;
        }
        private String lastMessage = "";

        @Override
        public void run() {
            try{
                String message=in.readLine();

                while(!message.equals("close")) {
                    System.out.println(lastMessage);
                    if(lastMessage.equals("share")){
                        System.out.println(message);
                        JSONObject jsonObject = new JSONObject(message);
                        List<String> files =  new ArrayList<>();
                        for (Object file :
                                (JSONArray) jsonObject.get("files")) {
                            files.add(file.toString());
                            System.out.println("file:" + file);
                        }
                        System.out.println(files.size());
                        allFiles.put(s.getInetAddress().toString(), files);
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("serverFiles", allFiles);
                        StringBuilder builder = new StringBuilder();
                        for(Map.Entry<String, List<String>> sharedFiles: allFiles.entrySet()){
                            for(String value: sharedFiles.getValue()){
                                builder.append(value + ",");
                                System.out.println("here here");
                            }
                        }
                        System.out.println("server files\n" + builder);
                        out.println(builder);
                    }else if(lastMessage.equals("downloadFile")){
                        String ip = _searchFile(message);
                        if(ip!=null){
                            out.println(ip);
                        }else{
                            out.println("404");
                        }
                    }
//                    System.out.println("Message recieved.");
//                    System.out.println("client: "+message);
//                    for (ClientHandler client : Server.clients) {
//                        System.out.println("sending....");
//                        client.out.println(message);
//                    }

                    lastMessage = message;
                    message=in.readLine();
                }
                this.in.close();
                this.out.close();
            }catch(Exception e){
//                e.printStackTrace();
                // TODO: remove the client from clients list
                allFiles.remove(s.getInetAddress().toString());
                System.out.println("Can't recieve message");
            }
        }

        private String _searchFile(String message) {
            for(Map.Entry<String, List<String>> sharedFiles: allFiles.entrySet()){
                for(String value: sharedFiles.getValue()){
                    if(value.equals(message)){
                        return sharedFiles.getKey();
                    }
                }
            }
            return null;
        }

    }
}
