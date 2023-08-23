package artifixal.reimbursementcalculationapp.adminPanel;

import artifixal.reimbursementcalculationapp.daos.LimitDAO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for processing update requests related to limits.
 * (Total reimbursement limit and Max distance)<p>
 * 
 * A valid request must be a JSON object with given structure:<br>
 * {<br>
 *	"id": <i>limit to update</i><br>
 *	"amount": <i>new amount</i><br>
 * }<br>
 * 
 * @author ArtiFixal
 */
@WebServlet(name="UpdateLimit", urlPatterns={"/updateLimit"})
public class UpdateLimit extends HttpServlet {
	
    /** 
     * Handles the HTTP <code>POST</code> method.
	 * 
     * @param request servlet request
     * @param response servlet response
	 * 
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
		ObjectMapper mapper=new ObjectMapper();
        final JsonNode jsonRequest=mapper.readValue(request.getInputStream()
				,JsonNode.class);
		JsonNode idNode=jsonRequest.get("id");
		// Check id existence
		if(idNode!=null)
		{
			// Check id validity
			try{
				final int id=Integer.parseInt(idNode.asText());
				JsonNode limitNode=jsonRequest.get("amount");
				// Check limit existence
				if(limitNode!=null)
				{
					// Check limit validity
					try{
						String limitString=limitNode.asText();
						BigDecimal newLimit=BigDecimal.valueOf(
								Float.parseFloat(limitString));
						// Send request to DB
						try(LimitDAO dao=new LimitDAO()){
							dao.updateLimit(id,newLimit);
							response.setStatus(HttpServletResponse.SC_OK);
						}catch(SQLException e){
							response.sendError(
									HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
									"An error occurred during processing DB request");
						}
					}catch(NumberFormatException e){
						response.sendError(
								HttpServletResponse.SC_BAD_REQUEST,
								"Malformed JSON request: Amount have to be a number");
					}
				}
				else
				{
					response.sendError(
							HttpServletResponse.SC_BAD_REQUEST,
							"Malformed JSON request: Amount not found");
				}
			}catch(NumberFormatException e){
				response.sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: ID have to be a number");
			}
		}
		else
		{
			response.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					"Malformed JSON request: ID not found");
		}
    }

    /** 
     * Returns a short description of the servlet.
	 * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Updates limit data in DB";
    }
}
