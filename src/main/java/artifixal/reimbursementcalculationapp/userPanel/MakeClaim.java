package artifixal.reimbursementcalculationapp.userPanel;

import artifixal.reimbursementcalculationapp.ExcludedDays;
import artifixal.reimbursementcalculationapp.Receipt;
import artifixal.reimbursementcalculationapp.daos.ClaimDAO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for processing user requests to create new reimbursement 
 * claim.
 * 
 * Valid request must be a JSON object with given structure: <br>
 * { <br>
 *	"dateFrom": <i> trip start date </i><br>
 *	"dateTo": <i> trip end date </i><br>
 *	"receipts": <i> receipts declared by user </i> <b>*Optional* [{array of 
 * objects}]</b><br>
 *	"excluded": <i> days excluded from daily allowance </i> <b>*Optional* 
 * [{array of objects}]</b><br>
 *	"mileage": <i>distance for which personal car was used </i> <b>*Optional*</b><br>
 * }
 * 
 * @author ArtiFixal
 */
@WebServlet(name = "MakeClaim",urlPatterns = {"/makeClaim"})
public class MakeClaim extends ClaimServlet {
	
	@Override
	protected void processRequest(ObjectMapper mapper,LocalDate tripStart,
				LocalDate tripEnd,Optional<ArrayList<Receipt>> receiptsList,
				Optional<ExcludedDays> excluded,Optional<Integer> mileage,
				HttpServletResponse response) throws IOException{
		// Send request to DB
		try(ClaimDAO dao=new ClaimDAO()){
				// Check if insert was successful
				if(dao.createClaim(tripStart,tripEnd,receiptsList,
						excluded,mileage))
					response.setStatus(HttpServletResponse.SC_OK);
				else
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"Failed to create claim");
			}catch(SQLException e){
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occured during processing DB request");
			}catch(DateTimeParseException e){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Dates are unreadeable");
			}
	}
	
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
			throws ServletException, IOException {
		validateAndProcessRequest(request,response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Inserts user claim into DB";
	}
}
