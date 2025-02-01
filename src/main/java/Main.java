import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.err.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage

     ServerSocket serverSocket = null;
     Socket clientSocket = null;
     int port = 9092;
     try {
       serverSocket = new ServerSocket(port);
       // Since the tester restarts your program quite often, setting SO_REUSEADDR
       // ensures that we don't run into 'Address already in use' errors
       serverSocket.setReuseAddress(true);
       // Wait for connection from client.
       clientSocket = serverSocket.accept();
       Optional<String> correlationId;
//       try(BufferedInputStream inputStreamReader = new BufferedInputStream(clientSocket.getInputStream())){
//           byte[] buffer = inputStreamReader.readAllBytes();
//           String output = new String(buffer);
//            correlationId = output.lines()
//                   .filter(s -> s.startsWith("correlation_id"))
//                   .map(s -> s.split(":")[1])
//                   .findFirst();
//
//       }
         OutputStream outputStream = clientSocket.getOutputStream();
         outputStream.write(new byte[]{0,1,2,3});
         outputStream.write(new byte[]{0,0,0,7});
         outputStream.flush();
//       String response = "message_size => INT32 \n" +
//               "Response Header v0 => correlation_id \n" +
//               "  correlation_id => 7";
//       outputStream.write(response.getBytes());
     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     } finally {
       try {
         if (clientSocket != null) {
           clientSocket.close();
         }
       } catch (IOException e) {
         System.out.println("IOException: " + e.getMessage());
       }
     }
  }
}
