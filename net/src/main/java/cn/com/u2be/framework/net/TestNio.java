package cn.com.u2be.framework.net;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TestNio {
    /**
     * NIO客户端
     *
     * @author 小路
     */
    public static class NIOClient {
        // 通道管理器
        private Selector selector;

        /**
         * 获得一个Socket通道，并对该通道做一些初始化的工作
         *
         * @param ip   连接的服务器的ip
         * @param port 连接的服务器的端口号
         * @throws IOException
         */
        public void initClient(String ip, int port) throws IOException {
            // 获得一个Socket通道
            SocketChannel channel = SocketChannel.open();
            // 设置通道为非阻塞
            channel.configureBlocking(false);
            // 获得一个通道管理器
            this.selector = Selector.open();

            // 客户端连接服务器,其实方法执行并没有实现连接，需要在listen（）方法中调
            // 用channel.finishConnect();才能完成连接
            channel.connect(new InetSocketAddress(ip, port));
            // 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。
            channel.register(selector, SelectionKey.OP_CONNECT);
        }

        /**
         * 获得一个Socket通道，并对该通道做一些初始化的工作
         *
         * @param addresses 连接的服务器的 InetSocketAddress
         * @throws IOException
         */
        public void initClient(List<InetSocketAddress> addresses)
                throws IOException {

            // 获得一个通道管理器
            this.selector = Selector.open();

            // 客户端连接服务器,其实方法执行并没有实现连接，需要在listen（）方法中调
            // 用channel.finishConnect();才能完成连接
            for (InetSocketAddress address : addresses) {
                // 获得一个Socket通道
                SocketChannel channel = SocketChannel.open();
                // 设置通道为非阻塞
                channel.configureBlocking(false);
                channel.connect(address);
                // 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。
                channel.register(selector, SelectionKey.OP_CONNECT
                        // | SelectionKey.OP_WRITE | SelectionKey.OP_READ
                );
            }

        }

        /**
         * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
         *
         * @throws IOException
         */
        @SuppressWarnings("unchecked")
        public void listen() throws IOException {
            // 轮询访问selector
            while (true) {
                selector.select();
                // 获得selector中选中的项的迭代器
                Iterator ite = this.selector.selectedKeys().iterator();
                while (ite.hasNext()) {
                    SelectionKey key = (SelectionKey) ite.next();
                    // 删除已选的key,以防重复处理
                    ite.remove();
                    // 连接事件发生
                    if (key.isWritable()) {

                        SocketChannel channel = (SocketChannel) key.channel();
                        // 在这里可以给服务端发送信息哦
                        channel.write(ByteBuffer.wrap(new byte[]{0x34, 0x56, (byte) 0xf1, (byte) 0x8a}));
                        SocketAddress remoteAddress = channel.socket().getRemoteSocketAddress();
                        System.out.println(remoteAddress.toString()
                                + ":  isWritable");
                        // 获得了可读的事件
                        channel.register(this.selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        read(key);
                        System.out.println("isReadable");
                    } else if (key.isConnectable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        try {
                            // // 如果正在连接，则完成连接
                            if (channel.isConnectionPending()) {
                                channel.finishConnect();
                                System.out.println("finishConnect");
                            }
                            // 在和服务端连接成功之后，为了可以接收到服务端1的信息，需要给通道设置读的权限。
                            channel.register(this.selector,
                                    SelectionKey.OP_WRITE);
                        } catch (Exception e) {
                            // TODO: handle exception
                            if (channel != null)
                                channel.close();

                            System.out.println("close channel ");
                        } finally {

                            System.out.println("isConnectable");
                        }
                    } else if (key.isAcceptable()) {
                        System.out.println("isAcceptable");
                    }

                }

            }
        }

        /**
         * 处理读取服务端发来的信息 的事件
         *
         * @param key
         * @throws IOException
         */
        public void read(SelectionKey key) throws IOException {

            // 服务器可读取消息:得到事件发生的Socket通道
            SocketChannel channel = (SocketChannel) key.channel();
            // 创建读取的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            byte[] data = buffer.array();
            // String msg = new String(data).trim();
            System.out.println("收到信息：" + Arrays.toString(data));
            // ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
            // channel.write(outBuffer);// 将消息回送给客户端

            // 获得了可写的事件
            channel.register(this.selector, SelectionKey.OP_WRITE);
        }

        /**
         * 启动客户端测试
         *
         * @throws IOException
         */
        public static void main(String[] args) throws IOException {
            NIOClient client = new NIOClient();
            List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(
                    0);
            addresses.add(new InetSocketAddress("192.168.1.101", 8080));
            addresses.add(new InetSocketAddress("192.168.1.115", 8080));
            client.initClient(addresses);
            // client.initClient("192.168.1.101", 8080);
            client.listen();
        }

    }
}
