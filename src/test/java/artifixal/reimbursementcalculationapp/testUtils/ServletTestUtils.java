package artifixal.reimbursementcalculationapp.testUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Utility class containing usefull methods used to test servlets.
 * 
 * @author ArtiFixal
 */
public class ServletTestUtils {
	public final static String JSON_CONTENT="application/json";
	
	private ServletTestUtils(){
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
	
	public static void sendRequestAndTestResponseCode(String URL,String method,String contentType,String json,
			int exceptedResponseCode,String errorMsg) throws IOException
	{
		HttpURLConnection con=
				ServletTestUtils.sendRequest(URL,method,contentType,json);
		int response=con.getResponseCode();
		assertEquals(exceptedResponseCode,response,
				errorMsg+". Code: "+response+" Message: "+con.getResponseMessage());
	} 
}
