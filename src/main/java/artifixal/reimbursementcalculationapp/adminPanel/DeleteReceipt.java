package artifixal.reimbursementcalculationapp.adminPanel;

import artifixal.reimbursementcalculationapp.daos.ReceiptTypeDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ArtiFixal
 */
@WebServlet(name="DeleteReceipt", urlPatterns={"/deleteReceipt"})
public class DeleteReceipt extends HttpServlet {
	
	@Override
	protected void doDelete(HttpServletRequest reqest,HttpServletResponse response)
			throws ServletException,IOException {
		ObjectMapper mapper=new ObjectMapper();
		final Map<String,String> jsonMap=mapper.readValue(reqest.getInputStream(),Map.class);
		String stringIdValue=jsonMap.get("id");
		// Check existence
		if(stringIdValue!=null)
		{
			// Check id validity
			try{
				int id=Integer.parseUnsignedInt(stringIdValue);
				// Send request to DB
				try(ReceiptTypeDAO deleteReceipt=new ReceiptTypeDAO()){
					// Check if delete was successful
					if(deleteReceipt.deleteReceipt(id))
						response.setStatus(HttpServletResponse.SC_OK);
					else
						response.sendError(HttpServletResponse.SC_NOT_FOUND,
								"Receipt of given ID not found!");
				}
			}catch(NumberFormatException e){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: ID must be an integer");
			}catch(SQLException e){
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occured during deletion");
			}
		}
		else
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Malformed JSON request: receipt ID not found");
		}
	}
	
	@Override
	public String getServletInfo() {
		return "Deletes selected receipt from DB";
	}
   
    
}
