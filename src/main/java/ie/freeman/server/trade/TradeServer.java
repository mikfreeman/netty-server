package ie.freeman.server.trade;

import ie.freeman.socket.handler.TradeHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TradeServer
{
	private final Logger logger = LoggerFactory.getLogger(TradeServer.class);

	public static void main(String[] args)
	{
		TradeServer tradeServer = new TradeServer();
		tradeServer.listen(8095);
	}

	@SuppressWarnings("resource")
	public void listen(int port)
	{
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "spring/app-context.xml" });

		final TradeHandler tradeHandler = appContext.getBean("tradeHandler",
				TradeHandler.class);

		ChannelFactory factory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());

		int corePoolSize = 5;
		int maxPoolSize = 10;
		long keepAliveTime = 5000;

		final ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
				corePoolSize, maxPoolSize, keepAliveTime,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		ServerBootstrap bootstrap = new ServerBootstrap(factory);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory()
		{
			public ChannelPipeline getPipeline()
			{
				ChannelPipeline pipeline = Channels.pipeline();

				// pipeline.addLast("framer", new DelimiterBasedFrameDecoder(4,
				// Delimiters.lineDelimiter()));

				pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("encoder", new StringEncoder());

				pipeline.addLast("pipelineExecutor", new ExecutionHandler(
						threadPoolExecutor));

				pipeline.addLast("handler", tradeHandler);

				return pipeline;
			}
		});

		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);

		bootstrap.bind(new InetSocketAddress(port));

		logger.info("Server starting and listening on port [{}]", port);
	}

}
