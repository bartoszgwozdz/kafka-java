import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Optional;

public class Main {
    public static void main(String[] args)
    {
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

            // Get input stream
            InputStream reader = clientSocket.getInputStream();
            //Get output stream
            OutputStream writer = clientSocket.getOutputStream();

            //read message_size
            reader.readNBytes(4);
            //read request api key & version
            var apiKeyBytes = reader.readNBytes(2);
            var apiVersionBytes = reader.readNBytes(2);
            var apiVersion = ByteBuffer.wrap(apiVersionBytes).getShort();
            var correlationIdBytes  = reader.readNBytes(4);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            //Write header
            //write correlation
            bos.write(correlationIdBytes);
            //write error_code
            if(apiVersion < 0 || apiVersion > 4) {
                bos.write(new byte[]{0,35});
            } else {
                // error code 16bit
                //    api_key => INT16
                //    min_version => INT16
                //    max_version => INT16
                //  throttle_time_ms => INT32
                bos.write(new byte[] {0, 0});       // error code
                bos.write(2);                       // array size + 1
                bos.write(new byte[] {0, 18});      // api_key
                bos.write(new byte[] {0, 3});       // min version
                bos.write(new byte[] {0, 4});       // max version
                bos.write(0);                       // tagged fields
                bos.write(new byte[] {0, 0, 0, 0}); // throttle time
                // All requests and responses will end with a tagged field buffer.  If
                // there are no tagged fields, this will only be a single zero byte.
                bos.write(0); // tagged fields
            }

            var size = bos.size();
            var response = bos.toByteArray();
            var sizeBytes = ByteBuffer.allocate(4).putInt(size).array();

            writer.write(sizeBytes);
            writer.write(response);
            writer.flush();


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
