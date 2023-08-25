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
	protected void processRequest(ObjectMapper mapper,LocalDate tripStart,
				LocalDate tripEnd,Optional<ArrayList<Receipt>> receiptsList,
				Optional<ExcludedDays> excluded,Optional<Integer> mileage,
				HttpServletResponse response) throws IOException
	{
		try(Connection singleCon=DBConfig.getInstance().createConnection()){
			try(LimitDAO limitDao=new LimitDAO(singleCon);
				ReceiptTypeDAO typeDao=new ReceiptTypeDAO(singleCon);
				RatesDAO ratesDao=new RatesDAO(singleCon)){
				BigDecimal claimAmount=BigDecimal.ZERO;
				// Get receipts
				final ArrayList<ReceiptType> receiptLimits=typeDao.getAllReceipts();
				final HashMap<Integer,BigDecimal> allowancePerReceipt=new HashMap<>();
				// If receipts exists sum amount owed for them
				if(receiptsList.isPresent())
				{
					// Sum receipts amount and make sure tehey don't exceed limit
					for(Receipt r:receiptsList.get())
					{
						addToMapNoExceed(allowancePerReceipt,r.getTypeID(),
								r.getValue(),receiptLimits.get(r.getTypeID())
										.getLimit());
					}
					Set<Integer> keys=allowancePerReceipt.keySet();
					// Add receipts to claim amount
					for(Iterator<Integer> it=keys.iterator();it.hasNext();)
					{
						claimAmount=claimAmount.add(allowancePerReceipt.
								get(it.next()));
					}
				}
				// If excluded days exists sum amount owed for them
				if(excluded.isPresent())
				{
					int daysPassed=Period.getNumberOfDaysPassed(tripStart
							,tripEnd)-excluded.get().getTotalNumberOfExcludedDays();
					Rate allowanceRate=ratesDao.getAllowanceRate();
					BigDecimal allowance=allowanceRate.getRate()
							.multiply(BigDecimal.valueOf(daysPassed));
					// Make sure owed amount won't exceed limit
					if(allowance.compareTo(allowanceRate.getLimit())<1)
						claimAmount=claimAmount.add(allowance);
					else
						claimAmount=claimAmount.add(allowanceRate.getLimit());
				}
				// If mileage exists sum amount owed for it
				if(mileage.isPresent())
				{
					int distanceLimit=limitDao.getDistanceLimit().getAmount()
							.intValue();
					BigDecimal mileageAmount;
					// Make sure that mileage won't exceed distance limit.
					if(mileage.get()>distanceLimit)
						mileageAmount=ratesDao.getMileageRate().getRate()
							.multiply(BigDecimal.valueOf(distanceLimit));
					else
						mileageAmount=ratesDao.getMileageRate().getRate()
							.multiply(BigDecimal.valueOf(mileage.get()));
					claimAmount=claimAmount.add(mileageAmount);
				}
				final BigDecimal totalClaimLimit=limitDao.getTotalLimit()
						.getAmount();
				try(PrintWriter w=new PrintWriter(response.getOutputStream())){
					if(claimAmount.compareTo(totalClaimLimit)==1)
						w.write(claimAmount.toString());
					else
						w.write(totalClaimLimit.toString());
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
