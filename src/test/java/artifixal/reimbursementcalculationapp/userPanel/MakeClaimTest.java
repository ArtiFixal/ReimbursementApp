package artifixal.reimbursementcalculationapp.userPanel;

import artifixal.reimbursementcalculationapp.MinimalServletServer;
import artifixal.reimbursementcalculationapp.testUtils.ServletUtilis;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests related to {@link MakeClaim} {@code Servlet} functionality.
 * 
 * @author ArtiFixal
 */
public class MakeClaimTest {

	private static final MinimalServletServer testServer=
			new MinimalServletServer(MakeClaim.class);

	@BeforeAll
	public static void startServer() throws Exception
	{
		testServer.startServer();
	}
	
	@Test
	public void insertValidClaimTest() throws IOException, InterruptedException{
		String json="{\"dateFrom\":\"2022-10-14T20:00:00.000Z\",\"dateTo\":"
				+ "\"2022-10-21T20:00:00.000Z\",\"excluded\":{\"days\":["
				+ "\"2022-10-14T20:00:00.000Z\"],\"periods\":[{"
				+ "\"from\":\"2022-10-17T20:00:00.000Z\",\"to\":"
				+ "\"2022-10-19T20:00:00.000Z\"}]},\"mileage\":135,"
				+ "\"receipts\":[{\"id\":1,\"name\":\"taxi\",\"value\":\"120.5\"},"
				+ "{\"id\":3,\"name\":\"plane ticket\",\"value\":\"321.4\"}]}";
		HttpURLConnection con=ServletUtilis.sendRequest(
				testServer.getURL(),"PUT",ServletUtilis.JSON_CONTENT,json);
		int result=con.getResponseCode();
		assertEquals(HttpURLConnection.HTTP_OK,result,
				"Claim insert failed. Code: "+result+" Message: "+con.getResponseMessage());
	}
	
	@AfterAll
	public static void stopServer() throws Exception
	{
		testServer.startServer();
	}
}
