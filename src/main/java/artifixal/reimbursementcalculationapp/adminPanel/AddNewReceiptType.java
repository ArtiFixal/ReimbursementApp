package artifixal.reimbursementcalculationapp.adminPanel;

import artifixal.reimbursementcalculationapp.ReceiptType;
import artifixal.reimbursementcalculationapp.daos.ReceiptTypeDAO;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for processing the requests to add new receipt type.<p>
 * 
 * Valid request must be a JSON object and have given structure:<br>
 * {<br>
 *	"name": <i>receipt name</i>,<br>
 *	"limit": <i>value</i><br>
 * }
 * 
 * @author ArtiFixal
 */
@WebServlet(name = "AddNewReceipt",urlPatterns = {"/addReceipt"})
public class AddNewReceiptType extends HttpServlet{
	
	/** 
     * Handles the HTTP <code>PUT</code> method.
	 * 
     * @param request servlet request
     * @param response servlet response
	 * 
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
	@Override
	protected void doPut(HttpServletRequest request,HttpServletResponse response)
			throws ServletException,IOException {
		ObjectMapper mapper=new ObjectMapper();
		try{
			final JsonNode jsonRequest=mapper.readValue(
				request.getInputStream(),JsonNode.class);
			JsonNode nameNode=jsonRequest.get("name");
			JsonNode limitNode=jsonRequest.get("limit");
			// Check for existence in request
			if(nameNode!=null&&limitNode!=null)
			{
				String name=nameNode.textValue();
				// Check name validity
				if(!name.isBlank())
				{
					// Retrive and test limit validity
					try{
						float limit=Float.parseFloat(limitNode.asText());
						ReceiptType r=new ReceiptType(-1,name,
								Optional.of(BigDecimal.valueOf(limit)));
						// Send request to DB
						try(ReceiptTypeDAO insertReceipt=new ReceiptTypeDAO()){
							// Check if insert was successful
							if(insertReceipt.createReceipt(r)!=-1)
								response.setStatus(HttpServletResponse.SC_OK);
							else
								response.sendError(HttpServletResponse.SC_BAD_REQUEST,
										"Similiar receipt already exists");
						}catch(IllegalArgumentException|NullPointerException e){
							response.sendError(HttpServletResponse.SC_BAD_REQUEST,
								"Malformed JSON request: Limit can't be a negative number!");
						}
					}catch(NumberFormatException e){
						response.sendError(HttpServletResponse.SC_BAD_REQUEST,
								"Malformed JSON request: Limit have to be a number!");
					}catch(SQLException e){
						response.sendError(HttpServletResponse.SC_BAD_REQUEST,
								"Similiar receipt already exists");
					}
				}
				else
				{
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
							"Malformed JSON request: name can't be empty");
				}
			}
			else
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Requires name and limit!");
			}
		}catch(StreamReadException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Malformed JSON request: Failed to read JSON request");
		}
	}
	
	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Inserts new receipt into DB";
	}

}
