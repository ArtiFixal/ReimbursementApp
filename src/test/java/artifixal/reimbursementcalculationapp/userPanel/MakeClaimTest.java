package artifixal.reimbursementcalculationapp.userPanel;

import artifixal.reimbursementcalculationapp.MinimalServletServer;
import artifixal.reimbursementcalculationapp.testUtils.ServletUtilis;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.AfterAll;
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
	
	private void sendRequestAndTestResponseCode(String json,
			int exceptedResponseCode,String errorMsg) throws IOException
	{
		ServletUtilis.sendRequestAndTestResponseCode(testServer.getURL(),"PUT",
				ServletUtilis.JSON_CONTENT,json,exceptedResponseCode,errorMsg);
	}
	
	@Test
	public void allFieldsValidClaimRequestTest() throws IOException, InterruptedException{
		String json="{\"dateFrom\":\"2022-10-14T20:00:00.000Z\",\"dateTo\":"
				+ "\"2022-10-21T20:00:00.000Z\",\"excluded\":{\"days\":["
				+ "\"2022-10-14T20:00:00.000Z\"],\"periods\":[{"
				+ "\"from\":\"2022-10-17T20:00:00.000Z\",\"to\":"
				+ "\"2022-10-19T20:00:00.000Z\"}]},\"mileage\":135,"
				+ "\"receipts\":[{\"id\":1,\"name\":\"taxi\",\"value\":\"120.5\"},"
				+ "{\"id\":3,\"name\":\"plane ticket\",\"value\":\"321.4\"}]}";
		sendRequestAndTestResponseCode(json,HttpURLConnection.HTTP_OK,
				"Claim insert failed");
	}
	
	@AfterAll
	public static void stopServer() throws Exception
	{
		testServer.startServer();
	}
}
