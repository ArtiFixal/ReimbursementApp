package artifixal.reimbursementcalculationapp.testUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility class containing usefull methods used to test servlets.
 * 
 * @author ArtiFixal
 */
public class ServletUtilis {
	public final static String JSON_CONTENT="application/json";
	
	private ServletUtilis(){
		throw new AssertionError("Not instantiable");
	}
	
	public static HttpURLConnection createHttpConnection(String url,String method,
			boolean doOutput,boolean doInput) throws MalformedURLException, IOException
	{
		final URL urlObject=new URL(url);
		final HttpURLConnection con=(HttpURLConnection)urlObject.openConnection();
		con.setRequestMethod(method);
		con.setDoOutput(doOutput);
		con.setDoInput(doInput);
		return con;
	}
	
	public static HttpURLConnection sendRequest(String url,String method,
			String contentType,String data) throws MalformedURLException,
			IOException
	{
		final HttpURLConnection con=createHttpConnection(url,method,true,true);
		con.setRequestProperty("Content-Type",contentType);
		try(DataOutputStream dos=new DataOutputStream(con.getOutputStream()))
		{
			dos.writeBytes(data);
			dos.flush();
		}
		return con;
	}
}
