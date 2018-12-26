package srp6;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Simple SSL chat client modified from {@link TelnetClient}.
 */
public final class Client {

    static final String HOST = System.getProperty("host", "localhost");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));
    public static double hash(String inp){
        String _input = inp;
        return 	_input.length()%Math.exp(_input.length());
    }
    public static void main(String[] args) throws Exception {
        // Configure SSL.
        /*final SslContext sslCtx = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE).build();*/

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ClientInitializer());

            // Start the connection attempt.
            Channel ch = b.connect(HOST, PORT).sync().channel();

            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            RandomString gen = new RandomString(10);
            		int k=3;
            		System.out.println("Prime number: ");
            		int temp = Integer.parseInt(in.readLine());
                    int N=2*temp+1;
                    String s = gen.nextString();
                    System.out.println("Username: ");
                    String I=in.readLine();
                    System.out.println("Password: ");
                    String p=in.readLine();
                    int g = 11;
                    System.out.println("Generator g = " + g);
                    double x = hash(s+p);
                    System.out.println("x="+x);
                    double v = (Math.pow(g, x))%N;
                    System.out.println("v="+v);
                    //FirstPhase
                    System.out.println("Sending to server I,s,v,N");
                    lastWriteFuture = ch.writeAndFlush("fi,"+I+","+s+","+Double.toString(v)+","+Integer.toString(N)+"\r\n");
                    lastWriteFuture.sync();
                    System.out.println("Data send");
                    int a= (int)Math.random()*1000;
                    System.out.println("Generating a="+a);
                    int A = (int) (Math.pow(g,a)%N);
                    System.out.println("Calculating A=g^a mod N="+A);
                    System.out.println("Sending to server I,A");
                    lastWriteFuture = ch.writeAndFlush("ft,"+I+","+A+"\r\n");
                    lastWriteFuture.sync();
                    System.out.println("Waiting server to respond...");
                    while(!ClientHandler.gotb){
                    	
                    }
                    
                    double B = ClientHandler.B;
                    double K =0.0;
                    System.out.println("Recieving from server B="+B);
                    double u = hash(Integer.toString(A)+Double.toString(B));
                    System.out.println("Calculating U=h(A,B)="+u);
                    System.out.println("Waiting server to respond...");
                    lastWriteFuture = ch.writeAndFlush("neddK"+"\r\n");
                    lastWriteFuture.sync();
                    while(!ClientHandler.gotk){
                    	
                    }
                    if (u!=0.0){
                        double servK = ClientHandler.K;
                        double S=(Math.pow((B-k*((Math.pow(g,x))%N)),(a+u*x)))%N;
                        K=hash(Double.toString(S));
                        System.out.println("Calculating K=h(((B-k*(g^x mod N))^a+u*x) mod N = "+K);
                        if(servK==K){
                        	 System.out.println("OK!");
                        }
                    }
                    //SecondPhase
                    double M = hash((((int)hash(Integer.toString(N))^(int)hash(Integer.toString(g))))+Double.toString(hash(I))+s+Integer.toString(A)+Double.toString(B)+Double.toString(K));
                    System.out.println("Calculating M="+M);
                    System.out.println("Waiting server to respond...");
                    lastWriteFuture = ch.writeAndFlush("neddR"+"\r\n");
                    lastWriteFuture.sync();
                    while(!ClientHandler.gotr){
                    	
                    }
                    if(hash(Integer.toString(A)+Double.toString(M)+Double.toString(K))==ClientHandler.R)
                    	System.out.println("Sucsess!");
                    else
                    	System.out.println("Error!");
                    lastWriteFuture = ch.writeAndFlush("bye");
                    lastWriteFuture.sync();
            /*for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                // Sends the received line to the server.
                lastWriteFuture = ch.writeAndFlush(line + "\r\n");

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    ch.closeFuture().sync();
                    break;
                }
            }*/

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }
}