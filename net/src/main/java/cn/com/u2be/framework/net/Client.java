package cn.com.u2be.framework.net;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import cn.com.u2be.myutils.Logger;

/**
 * Created by alek on 2016/5/13.
 */
public class Client implements Runnable {
    private final Logger logger = Logger.getLogger(Client.class);

    private TCPProtocol protocol;

    private final Selector selector;

    public Client(TCPProtocol protocol) throws IOException {
        selector = Selector.open();
        this.protocol = protocol;
    }


    public SelectionKey addChannel(String ip, int port) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(ip, port));
        while (!channel.finishConnect()) {
            logger.d("正在连接；IP:%1s，Port：%2d", ip, port);
        }
        return channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_ACCEPT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }


    public void send(SelectionKey key, byte[] data) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        final ByteBuffer buffer = ByteBuffer.wrap(data);
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

    }


    @Override
    public void run() {

        while (true) {
            try {
                if (selector.select(1000) == 0) {
                    continue;
                }
                final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();
                    //感兴趣的I/O操作为connect
                    if (key.isConnectable()) {
                        protocol.handleConnect(key);
                    }
                    //感兴趣的I/O操作为accept
                    if (key.isAcceptable()) {
                        protocol.handleAccept(key);
                    }
                    //感兴趣的I/O操作为read
                    if (key.isReadable()) {
                        protocol.handleRead(key);
                    }
                    //感兴趣的I/O操作为write
                    if (key.isValid() && key.isWritable()) {
                        protocol.handleWrite(key);
                    }
                    iterator.remove();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }


}
