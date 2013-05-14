package ie.freeman.socket.handler;

import ie.freeman.service.trade.TradeService;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("tradeHandler")
public class TradeHandler extends SimpleChannelHandler
{
	private final Logger logger = LoggerFactory.getLogger(TradeHandler.class);

	@Autowired
	private TradeService tradeService;

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception
	{
		Channel ch = e.getChannel();
		// ch.write(e.getMessage());

		String msg = (String) e.getMessage();

		String price = tradeService.getPrice(msg);

		ChannelFuture f = ch.write(price);

		f.addListener(new ChannelFutureListener()
		{
			public void operationComplete(ChannelFuture future)
			{
				Channel ch = future.getChannel();
				ch.close();
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
	{
		logger.error("Error processing trade", e);

		Channel ch = e.getChannel();
		ch.close();
	}
}
