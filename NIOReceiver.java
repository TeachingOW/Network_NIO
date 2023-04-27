import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOReceiver {
    public static void main(String[] args) {
        try {
            // Create a selector and a server socket channel
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(12345));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            while (true) {
                // Wait for events
                selector.select();
                
                // Handle the events
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    
                    if (key.isAcceptable()) {
                        // Accept the incoming connection
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = serverChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        // Read data from the socket channel
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int numRead = socketChannel.read(buffer);
                        
                        if (numRead == -1) {
                            // The connection has been closed
                            key.cancel();
                            socketChannel.close();
                            continue;
                        }
                        
                        // Process the received data
                        String message = new String(buffer.array(), 0, numRead);
                        System.out.println("Received message: " + message);
                        
                        // Send a response back to the sender
                        ByteBuffer responseBuffer = ByteBuffer.wrap("Thanks for your message!".getBytes());
                        while (responseBuffer.hasRemaining()) {
                            socketChannel.write(responseBuffer);
                        }
                    }
                    
                    // Remove the processed key from the set
                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
