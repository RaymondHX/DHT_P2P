import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    ServerSocket serverSocket;
    String filepath;
    public Server(ServerSocket serverSocket,int id) {
        this.serverSocket = serverSocket;
        filepath = "E:\\DHT_P2P\\src\\files\\file"+id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                byte[] bytes = new byte[1024];
                int len = inputStream.read(bytes);
                String firstLine = new String(bytes,0,len,"UTF-8");
                System.out.println("firstline"+firstLine);
                if(firstLine.substring(0, 7).equals("request")){
                    int want = Integer.parseInt(firstLine.substring(7));
                        if (Host.id == want) {
                            String response = String.valueOf(Host.port);
                            outputStream.write(response.getBytes("UTF-8"));
                        } else {
                            int get = query(want);
                            String response = String.valueOf(get);
                            outputStream.write(response.getBytes("UTF-8"));
                        }
                    inputStream.close();
                    outputStream.close();
                }
                else if(firstLine.substring(0,6).equals("upload")){
                    int index = firstLine.indexOf("*");
                    System.out.println("index"+index);
                    receieveFile(filepath+"\\"+firstLine.substring(6,index)+".txt",socket);

                }
                else if(firstLine.substring(0,8).equals("download")){
                    String filename = firstLine.substring(8);
                    File file = new File(filepath+"\\"+filename);
                    sendFile(file,socket);
                }
                else if(firstLine.substring(0,6).equals("change")){
                    String nextport = firstLine.substring(7);
                    System.out.println("next"+nextport);
                    Host.nextPort = Integer.parseInt(nextport);
                    OutputStream outputStream1 = socket.getOutputStream();
                    outputStream1.write("change port successfully".getBytes("UTF-8"));
                    outputStream1.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public synchronized int query(int port) {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), Host.nextPort);
            OutputStream outputStream = socket.getOutputStream();
            String response = "request" + String.valueOf(port);
            outputStream.write(response.getBytes("UTF-8"));
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                sb.append(new String(bytes, 0, len, "UTF-8"));
            }
            int want = Integer.parseInt(sb.toString());
            inputStream.close();
            outputStream.close();
            return want;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;

    }

    private synchronized void receieveFile(String filePath, Socket socket) {

            try {
                System.out.println("接收到客户端的连接，，，，");

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath));

                byte[] buf = new byte[1027 * 9];
                int len = 0;

                while ((len = dis.read(buf)) != -1) {
                    dos.write(buf, 0, len);
                }
                dos.flush();

                System.out.println("文件接受结束，，，，");
                dis.close();
                dos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private synchronized void sendFile(File file,Socket socket){
        try {
            System.out.println("文件大小：" + file.length() + "kb");
            DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            byte[] buf = new byte[1024 * 9];
            int len = 0;
            while ((len = dis.read(buf)) != -1) {
                dos.write(buf, 0, len);

            }
            dos.flush();
            System.out.println("文件传输结束，，，，");

            dis.close();
            dos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
