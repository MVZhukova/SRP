package srp6;



import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {
	 public static double hash(String inp){
	        String _input = inp;
	        return 	_input.length()%Math.exp(_input.length());
	    }
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    String I="",s = "";
    double v = 0.0, S= 0.0, B = 0.0, K=0.0;
    int N=0,A = 0, b=0;
    int k=3;
    int g=123;
    /*@Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    public void operationComplete(Future<Channel> future) throws Exception {
                        ctx.writeAndFlush(
                                "Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
                        ctx.writeAndFlush(
                                "Your session is protected by " +
                                        ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                                        " cipher suite.\n");

                        channels.add(ctx.channel());
                    }
        });
    }*/

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    	ctx.writeAndFlush(
                "Connection established\n");

        channels.add(ctx.channel());
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    	channels.remove(ctx.channel());
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // Send the received message to all channels but the current one.
        for (Channel c: channels) {
        	if(msg.contains("fi")){
        		String[] splittedmsg = msg.split(",");
        		I = splittedmsg[1];
        		s = splittedmsg[2];
        		v = Double.parseDouble(splittedmsg[3]);
        		N = Integer.parseInt(splittedmsg[4]);
        		System.out.println("Data recieved");
        	}
        	if(msg.contains("ft")){
        		String[] splittedmsg = msg.split(",");
        		I = splittedmsg[1];
        		A = Integer.parseInt(splittedmsg[2]);
        		if(A!=0){
        			System.out.println("A OK!");
        		}
        		b = (int)Math.random()*100;
        		B = (k*v+Math.pow(g, b)%N)%N;
        		ctx.writeAndFlush("msgb,"+s+","+Double.toString(B)+'\n');
        	}
        	if(msg.contains("neddK")){
        		double u = hash(Integer.toString(A)+Double.toString(B));
        		S = (Math.pow(A*(Math.pow(v, u)%N),b))%N;
        		K = hash(Double.toString(S));
        		ctx.writeAndFlush("countKey,"+Double.toString(K)+'\n');
        	}
        	if(msg.contains("neddR")){
        		double M = hash((((int)hash(Integer.toString(N))^(int)hash(Integer.toString(g))))+Double.toString(hash(I))+s+Integer.toString(A)+Double.toString(B)+Double.toString(K));
        		double R = hash(Integer.toString(A)+Double.toString(M)+Double.toString(K));
        		ctx.writeAndFlush("countR,"+Double.toString(R)+'\n');
        		
        	}

        }

        // Close the connection if the client has sent 'bye'.
        if ("bye".equals(msg.toLowerCase())) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}