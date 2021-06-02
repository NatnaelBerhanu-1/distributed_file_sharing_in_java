import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class Client {
    private static InetAddress host;
    private static final int PORT = 1234;
    private static String sharePath = new File("").getAbsolutePath() + "\\src\\main\\shareFolder";

    public static void main(String[] args) throws IOException {
        try {
            host = InetAddress.getLocalHost();
//            File shareDirectory = new File(sharePath);
//            System.out.println(shareDirectory.isDirectory());
//            if(shareDirectory.isDirectory()){
//                for (String fileName : shareDirectory.list()) {
//                    System.out.println(fileName);
//                }
//            }
//
//            System.out.println("here");
        } catch (UnknownHostException e) {
            System.out.println("Host id not found!");
            System.exit(-1);
        }
        listenSocket();
    }

    public static void listenSocket() {
        Socket link = null;
        try {
            link = new Socket(host, PORT);
        } catch (IOException e) {
            System.out.println("Unable to connect");
            System.exit(-1);
        }
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);
            BufferedReader userentry=new BufferedReader(new InputStreamReader(System.in));
            JSONObject jsonObject = new JSONObject();
            File shareDirectory = new File(sharePath);
            jsonObject.put("files", shareDirectory.list());
//            System.out.println(jsonObject);
            out.println("share");
            out.println(jsonObject);
            String serverFiles = in.readLine();
            System.out.println("Files on server\n" + serverFiles);
            String message, response;
            System.out.println("enter 1 to download file");
            String inputKey = userentry.readLine();
            while(!inputKey.equals("Q")){
                switch (inputKey){
                    case "1":
                        // TODO: download file
                        System.out.println("Enter file name: ");
                        inputKey = userentry.readLine();
                        out.println("downloadFile");
                        out.println(inputKey);
                        String ip = in.readLine();
                        System.out.println(ip);
                        if(ip.equals("404")){
                            continue;
                        }else{
                            // Download file from client
                            link.close();
                            link = new Socket(ip, 1234);
                        }
                        break;
                }
                inputKey = "";
            }
//            readMessage(in);
//            writeMessage(out, userentry);
        } catch (IOException e) {
            System.out.println("Message is not sent.");
        }
    }

    private static void readMessage(BufferedReader in){
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                String message;
                try{
                    while(true){
                        message = in.readLine();
                        System.out.println("Message: "+message);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private static void writeMessage(PrintWriter out, BufferedReader in){
        Thread t = new Thread(() -> {
            String message;
            try{
                while(true){
                    // System.out.print("Enter Message: ");
                    message = in.readLine();
                    out.println(message);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        });
        t.start();
    }
}