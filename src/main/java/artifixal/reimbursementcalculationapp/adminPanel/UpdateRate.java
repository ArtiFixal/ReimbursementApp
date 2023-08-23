package artifixal.reimbursementcalculationapp.adminPanel;

import artifixal.reimbursementcalculationapp.daos.RatesDAO;
import com.fasterxml.jackson.databind.JsonNode;
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
 * Servlet responsible for processing update requests related to rates.<p>
 * 
 * Valid request must be a JSON object with given structure:<br>
 * { <br>
 *	"id": <i> rate id </i><br>
 *	"amount": <i> new rate amount </i> <b> *Optional* </b><br>
 *	"limit": <i> new rate limit </i> <b> *Optional* </b><br>
 * }
 * 
 * @author ArtiFixal
 */
@WebServlet(name="UpdateRate", urlPatterns={"/updateRate"})
public class UpdateRate extends HttpServlet {
	
	private Optional<BigDecimal> getOptionalValue(JsonNode json,
			String key,String errorMsg,HttpServletResponse response) throws IOException
	{
		JsonNode fieldValue=json.get(key);
		Optional<BigDecimal> optionalField=Optional.empty();
		// Check value existence
		if(fieldValue!=null)
		{
			// Check value validity
			try{
				BigDecimal value=BigDecimal.valueOf(Float.parseFloat(fieldValue.asText()));
				optionalField=Optional.of(value);
			}catch(NumberFormatException e){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,errorMsg);
			}
		}
		return optionalField;
	}
	
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
		JsonMapper mapper=new JsonMapper();
        final JsonNode jsonRequest=mapper.readValue(request.getInputStream(),
				JsonNode.class);
		JsonNode idNode=jsonRequest.get("id");
		// Check id existence
		if(idNode!=null)
		{
			// Check id validity
			try{
				int id=Integer.parseInt(idNode.asText());
				Optional<BigDecimal> amount=getOptionalValue(jsonRequest,"amount",
						"Malformed JSON request: Amount have to be a number",
						response);
				Optional<BigDecimal> limit=getOptionalValue(jsonRequest,"limit",
						"Malformed JSON request: Limit have to be a number",
						response);
				// Send request to DB
				try(RatesDAO dao=new RatesDAO()){
					if(dao.updateRate(id,amount,limit))
					{
						response.setStatus(HttpServletResponse.SC_OK);
					}
					else
					{
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								"An error occured during processing DB request");
					}
				}catch(SQLException e){
					System.out.println(e);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"An error occured during processing request");
				}
			}catch(NumberFormatException e){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: ID have to be an integer");
			}
		}
		else
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Malformed JSON request: Rate ID not found");
		}
    }

    /** 
     * Returns a short description of the servlet.
	 * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Updates rate info in DB";
    }
}
