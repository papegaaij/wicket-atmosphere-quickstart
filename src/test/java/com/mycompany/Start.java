package com.mycompany;

import org.apache.wicket.util.time.Duration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

public class Start
{
	public static void main(String[] args)
	{
		int timeout = (int) Duration.ONE_HOUR.getMilliseconds();

		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();

		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(timeout);
		connector.setSoLingerTime(-1);
		connector.setPort(8080);
		server.addConnector(connector);

		// check if a keystore for a SSL certificate is available, and
		// if so, start a SSL connector on port 8443. By default, the
		// quickstart comes with a Apache Wicket Quickstart Certificate
		// that expires about half way september 2021. Do not use this
		// certificate anywhere important as the passwords are available
		// in the source.
		Resource keystore = Resource.newClassPathResource("/keystore");
		if (keystore != null)
		{
			SslContextFactory sslFactory = new SslContextFactory();
			sslFactory.setKeyStoreResource(keystore);
			sslFactory.setKeyStorePassword("wicket");
			sslFactory.setKeyManagerPassword("wicket");

			SslSocketConnector sslConnector = new SslSocketConnector(sslFactory);
			sslConnector.setPort(8443);
			sslConnector.setMaxIdleTime(timeout);
			server.addConnector(sslConnector);

			System.out.println("SSL access to the quickstart has been enabled on port 8443");
			System.out
				.println("You can access the application using SSL on https://localhost:8443");
			System.out.println();
		}

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar("src/main/webapp");

		// START JMX SERVER
		// MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		// MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
		// server.getContainer().addEventListener(mBeanContainer);
		// mBeanContainer.start();

		server.setHandler(bb);

		try
		{
			System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
			server.start();
			System.in.read();
			System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
			server.stop();
			server.join();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}
