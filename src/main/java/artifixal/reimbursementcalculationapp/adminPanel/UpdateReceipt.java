package artifixal.reimbursementcalculationapp.adminPanel;

import artifixal.reimbursementcalculationapp.daos.OptionalDBField;
import artifixal.reimbursementcalculationapp.daos.ReceiptTypeDAO;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
 * Servlet responsible for processing {@code ReceiptType} update requests.
 * 
 * Valid request must be a JSON object with given structure: <br>
 * { <br>
 *	"id": <i> receipt type id </i><br>
 *	"name": <i> new receipt name </i><b> *Optional* </b><br>
 *	"limit": <i> new limit amount per receipt type </i> <b> *Optional* </b><br>
 * }
 * 
 * @author ArtiFixal
 */
@WebServlet(name="UpdateReceipt", urlPatterns={"/updateReceipt"})
public class UpdateReceipt extends HttpServlet {

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
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
		ObjectMapper mapper=new JsonMapper();
		// Check JSON validity
		try{
			final JsonNode requestJson=mapper.readValue(request.getInputStream(),
					JsonNode.class);
			JsonNode idNode=requestJson.get("id");
			// Get optional field to update
                        OptionalDBField<String> name=new OptionalDBField<>(Optional.ofNullable(requestJson.get("name")
					.asText(null)),"name");
			String limitString=requestJson.get("limit").asText();
			// Check id existence
			if(idNode!=null)
			{
				// Check id validity
				try{
					int id=Integer.parseUnsignedInt(idNode.asText());
					Optional<BigDecimal> limitOptional=Optional.empty();
					// Check limit existence
					if(limitString!=null)
					{
						// Check limit validity
						try{
							limitOptional=Optional.of(BigDecimal
									.valueOf(Float.parseFloat(limitString)));
						}catch(NumberFormatException e){
							response.sendError(HttpServletResponse.SC_BAD_REQUEST,
							"Malformed JSON request: limit have to be a number");
						}
					}
                                        OptionalDBField<BigDecimal> limit=new OptionalDBField<>(limitOptional,"limit");
					// Send request to DB
					try(ReceiptTypeDAO updateReceipt=new ReceiptTypeDAO()){
						// Check if update was successful
						if(updateReceipt.updateReceipt(id,name,limit))
							response.setStatus(HttpServletResponse.SC_OK);
						else
							response.sendError(HttpServletResponse.SC_BAD_REQUEST,
									"Malformed JSON request: Receipt Type of given ID doesn't exists");
					}
				}catch(NumberFormatException e) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
							"Malformed JSON request: ID have to be an integer");
				}catch(SQLException e){
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"An error ocurred during processing the DB request");
				}
			}
			else
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: receipt ID not found");
			}
		}catch(StreamReadException e){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An error ocurred during reading the request");
		}
		catch(DatabindException e){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An error ocurred during processing the JSON object");
		}
		catch(IOException e){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An error ocurred during processing the request");
		}
		
	}
	
    /** 
     * Returns a short description of the servlet.
	 * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Updates receipt data in DB";
    }
}
