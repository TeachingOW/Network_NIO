import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOSender {
    public static void main(String[] args) {
        try {
            // Create a socket channel and connect to the receiver
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("192.168.1.100", 12345));
            
            // Wait for the connection to be established
            while (!socketChannel.finishConnect()) {
                // Do nothing
            }
            
            // Send data to the receiver
            String message = "Hello, world!";
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
            
            // Close the socket channel
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
