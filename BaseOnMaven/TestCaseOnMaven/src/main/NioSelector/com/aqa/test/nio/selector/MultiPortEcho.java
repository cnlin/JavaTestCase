package com.aqa.test.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.IntStream;

public class MultiPortEcho {

	private int ports[];
	private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);
	
	public MultiPortEcho(int[] ports) throws IOException {
		super();
		this.ports = ports;
		go();
	}

	private void go() throws IOException {
		
		/**
		 * 创建一个新的Selector
		 * 这个Selector是跟系统关联的，其创建要么是自定义的Selector，
		 * 要么是系统默认的Selector
		 */
		Selector selector = Selector.open();
		
		//Java8的IntStream代替forEach,并使用lambda编程
		IntStream.range(0, ports.length).forEach(
			i -> {
				try {
					/*
					 * 之所以要先使用ServerSocketChannel来创建一个channel，是因为
					 * 我们无法确定性地从任意的已存在的ServerSocket得到一个ServerSocketChannel，
					 * 所以我们先创建一个ServerSocketChannel，然后得到它关联的ServerSocket，
					 * 最后再把ServerSocket绑定到相应的端口
					 */
					ServerSocketChannel ssc = ServerSocketChannel.open();
					//必须打开，否则默认使用的是阻塞
					ssc.configureBlocking(false);
					//获取ServerSocket
					ServerSocket ss = ssc.socket();
					InetSocketAddress address = new InetSocketAddress(ports[i]);
					//绑定到要监听的端口
					ss.bind(address);
					
					/*
					 * 将channel注册到Selector，
					 * 其实吧：是告诉操作系统的selector，当发生了我感兴趣的事件，
					 * 则把channel给我
					 */
					SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);
					System.out.printf("Going to listen on port:[%d] \n",ports[i]);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		);
		
		
		/*
		 * 把对应的Channel注册完成之后，接下来就等着连接事件的到来了
		 */
		while(true){
			System.out.println("always come in ...");
			/*
			 * 这个获取到有多少感兴趣的事件发生了
			 * 有多少Channel准备好I/O操作了
			 */
			int num = selector.select();
			/*
			 * 这些感兴趣的事件是:
			 * 谁感兴趣？ 	who
			 * 感兴趣什么？what
			 * 这些相关的数据被封装到一个对象SelectionKey中，所以
			 * 我们接下来可以使用selectionkey来获取感兴趣的人（channel）
			 * 和感兴趣的事（Selection.OP_ACCEPT/OP_READ...）
			 */
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = selectedKeys.iterator();
			
			/*
			 * 迭代key
			 */
			while (it.hasNext()){
				SelectionKey key = it.next();
				//判断发生的是哪些事件
				if((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT){
					//接收新连接事件，表示有新连接进来，有了连接，就有了channel
					ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);
					
					/*
					 * ServerSocketChannel只负责连接事件
					 * SocketChannel才负责读写事件
					 */
					SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
					it.remove();
					
					System.out.printf("Got connection from %s ",sc);
				}else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
					//读事件
					SocketChannel sc = (SocketChannel) key.channel();
					//读取数据
					int bytesEchoed = 0;
					while(true){
						//清空buffer
						echoBuffer.clear();
						int r = sc.read(echoBuffer);
						if(r<0){
							break;
						}
						//将limit移到数据的边界，表示buffer的空间用到这里
						//方便等下position从0开始读取，只读到limit就够了
						echoBuffer.flip();
						//写出数据
						sc.write(echoBuffer);
						//数据长度
						bytesEchoed += r;
						
					}
					System.out.printf("Echoed %d from %s ",bytesEchoed,sc);
					it.remove();
				}
			}
			
//			System.out.println("going to clear");
//			selectedKeys.clear();
//			System.out.println("cleared");
			
		}
	}





	public static void main(String[] args) throws IOException {
		if(args.length < 0){
			System.err.println("Usage : java MultiPortEcho port [port port ...]");
			System.exit(1);
		}

		int[] ports = new int[args.length];
		IntStream.range(0, ports.length).forEach(
				i -> ports[i] = Integer.parseInt(args[i])
		);
		
		new MultiPortEcho(ports);
		
	}

}
