package artifixal.reimbursementcalculationapp.userPanel;

import artifixal.reimbursementcalculationapp.DBConfig;
import artifixal.reimbursementcalculationapp.ExcludedDays;
import artifixal.reimbursementcalculationapp.Period;
import artifixal.reimbursementcalculationapp.Rate;
import artifixal.reimbursementcalculationapp.Receipt;
import artifixal.reimbursementcalculationapp.ReceiptType;
import artifixal.reimbursementcalculationapp.daos.LimitDAO;
import artifixal.reimbursementcalculationapp.daos.RatesDAO;
import artifixal.reimbursementcalculationapp.daos.ReceiptTypeDAO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for processing user requests to calculate total amount of
 * reimbursement claim. <p>
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
@WebServlet(name="CalculateClaim", urlPatterns={"/calculateClaim"})
public class CalculateClaim extends ClaimServlet {

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
		validateAndProcessRequest(request,response);
    }
	
	private void addToMapNoExceed(HashMap<Integer,BigDecimal> map,Integer key,
			BigDecimal value,BigDecimal limit)
	{
		if(map.containsKey(key))
		{
			BigDecimal newValue=map.get(key).add(value);
			putIfNoExceed(map,key,newValue,limit);
		}
		else
		{
			putIfNoExceed(map,key,value,limit);
		}
			
	}
	
	private void putIfNoExceed(HashMap<Integer,BigDecimal> map,Integer key,
			BigDecimal value,BigDecimal limit)
	{
		if(value.compareTo(limit)>=0)
				map.put(key,limit);
			else
				map.put(key,value);
	}

    /** 
     * Returns a short description of the servlet.
	 * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Calculates claim value";
    }

	@Override
	protected void processRequest(ObjectMapper mapper,JsonNode tripStartNode,
				JsonNode tripEndNode,Optional<ArrayList<Receipt>> receiptsList,
				Optional<ExcludedDays> excluded,Optional<Integer> mileage,
				HttpServletResponse response) throws IOException{
		try(Connection singleCon=DBConfig.getInstance().createConnection()){
			try(LimitDAO limitDao=new LimitDAO(singleCon);
				ReceiptTypeDAO typeDao=new ReceiptTypeDAO(singleCon);
				RatesDAO ratesDao=new RatesDAO(singleCon)){
				LocalDate tripStart=mapper.convertValue(tripStartNode,
						LocalDate.class);
				LocalDate tripEnd=mapper.convertValue(tripEndNode,
						LocalDate.class);
				final BigDecimal totalClaimLimit=limitDao.getTotalLimit()
						.getAmount();
				BigDecimal value=BigDecimal.ZERO;
				final ArrayList<ReceiptType> receiptLimits=typeDao.getAllReceipts();
				final HashMap<Integer,BigDecimal> allowancePerReceipt=new HashMap<>();
				if(receiptsList.isPresent())
				{
					for(Receipt r:receiptsList.get())
					{
						addToMapNoExceed(allowancePerReceipt,r.getTypeID(),
								r.getValue(),receiptLimits.get(r.getTypeID())
										.getLimit());
					}
					Set<Integer> keys=allowancePerReceipt.keySet();
					for(Iterator<Integer> it=keys.iterator();it.hasNext();)
					{
						value=value.add(allowancePerReceipt.get(it.next()));
					}
				}
				if(excluded.isPresent())
				{
					
				}
				if(mileage.isPresent())
				{
					value=value.add(ratesDao.getMileageRate().getRate().
							multiply(BigDecimal.valueOf(mileage.get())));
				}
				try(PrintWriter w=new PrintWriter(response.getOutputStream())){
					w.write(value.toString());
					response.setStatus(HttpServletResponse.SC_OK);
				}
			}catch(SQLException e){
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occured during processing DB request");
			}catch(DateTimeParseException e){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Dates are unreadeable");
			}
		}catch(SQLException ex){
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Connection to DB failed");
		}
	}
}
