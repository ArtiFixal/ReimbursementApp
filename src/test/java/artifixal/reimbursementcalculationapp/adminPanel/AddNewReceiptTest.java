package artifixal.reimbursementcalculationapp.adminPanel;

import artifixal.reimbursementcalculationapp.DBConfig;
import artifixal.reimbursementcalculationapp.MinimalServletServer;
import artifixal.reimbursementcalculationapp.testUtils.ServletUtilis;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests related to {@link AddNewReceipt} {@code Servlet} functionality.
 * 
 * @author ArtiFixal
 */
public class AddNewReceiptTest {
	
	private static final MinimalServletServer testServer=
			new MinimalServletServer(AddNewReceipt.class);
	
	@BeforeAll
	public static void startServer() throws Exception
	{
		testServer.startServer();
	}
	
	private void sendRequestAndTestResponseCode(String json,
			int exceptedResponseCode,String errorMsg) throws IOException
	{
		HttpURLConnection con=
				ServletUtilis.sendRequest(testServer.getURL(),"PUT",ServletUtilis.JSON_CONTENT,json);
		int response=con.getResponseCode();
		assertEquals(exceptedResponseCode,response,
				errorMsg+". Code: "+response+" Message: "+con.getResponseMessage());
	}
	
	@Test
	public void validInsertTest() throws IOException, SQLException
	{
		String json="{\"name\":\"backendTest\",\"limit\":4321}";
		try (Connection dbCon=DBConfig.getInstance().createConnection()){
			Statement deleteIfExists=dbCon.createStatement();
			deleteIfExists.executeUpdate("DELETE FROM types WHERE name LIKE 'backendTest'");
		}
		sendRequestAndTestResponseCode(json,HttpURLConnection.HTTP_OK,
				"Valid receipt type insert test failed");
	}
	
	@Test
	public void invalidLimitTest() throws IOException
	{
		String json="{\"name\":\"backendTest\",\"limit\":\"1z1x\"}";
		sendRequestAndTestResponseCode(json,HttpURLConnection.HTTP_BAD_REQUEST,
				"Invalid limit number insert test failed");
	}
	
	@Test
	public void emptyLimitTest() throws IOException
	{
		String json="{\"name\":\"backendTest\",\"limit\":}";
		sendRequestAndTestResponseCode(json,HttpURLConnection.HTTP_BAD_REQUEST,
				"Empty limit number test failed");
	}
	
	@Test
	public void noLimitFieldTest() throws IOException
	{
		String json="{\"name\":\"backendTest\"}";
		sendRequestAndTestResponseCode(json,HttpURLConnection.HTTP_BAD_REQUEST,
				"No limit field test failed");
	}
	
	@Test
	public void negativeLimitValueTest() throws IOException
	{
		String json="{\"name\":\"backendTest\",\"limit\":-123}";
		sendRequestAndTestResponseCode(json,HttpURLConnection.HTTP_BAD_REQUEST,
				"Negative limit test failed");
	}
	
	@AfterAll
	public static void stopServer() throws Exception
	{
		testServer.startServer();
	}
}
