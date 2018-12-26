package srp6;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handles a client-side channel.
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {
	public static double B = 0.0;
	public static double K = 0.0;
	public static double R = 0.0;
	public static boolean gotb = false;
	public static boolean gotk = false;
	public static boolean gotr = false;
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    	if(msg.contains("msgb")){
    		String[] splittedmsg = msg.split(",");
    		B = Double.parseDouble(splittedmsg[2]);
    		gotb = true;
    	}
    	if(msg.contains("countKey")){
    		String[] splittedmsg = msg.split(",");
    		K = Double.parseDouble(splittedmsg[1]);
    		gotk = true;
    	}
        if(msg.contains("countR")){
    		String[] splittedmsg = msg.split(",");
    		R = Double.parseDouble(splittedmsg[1]);
    		gotr = true;
    	}
        System.err.println("Got from server>>"+msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}