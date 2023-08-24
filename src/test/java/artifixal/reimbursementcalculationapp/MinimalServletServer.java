package artifixal.reimbursementcalculationapp;

import javax.servlet.Servlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Minimal Servlet server implementation to test servlets behaviour.
 * 
 * @author ArtiFixal
 */
public class MinimalServletServer {
	public static final String SERVER_URL="localhost";
	private final Server server;
	private final ServletHandler handler;

	public MinimalServletServer(Class<? extends Servlet> servletClassToHost){
		server=new Server(0);
		handler=new ServletHandler();
		server.setHandler(handler);
		handler.setAllowDuplicateMappings(true);
		handler.addServletWithMapping(servletClassToHost,"/test");
		
	}
	
	public int getServerPort()
	{
		return ((ServerConnector)server.getConnectors()[0]).getLocalPort();
	}
	
	public String getURL()
	{
		return "http://"+SERVER_URL+":"+getServerPort()+"/test";
	}

	public ServletHandler getHandler(){
		return handler;
	}

	public Server getServer(){
		return server;
	}
	
	public void startServer() throws Exception
	{
		server.start();
	}
	
	public void stopServer() throws Exception
	{
		server.stop();
	}
	
	public void joinServer() throws InterruptedException
	{
		server.join();
	}
}
