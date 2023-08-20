package artifixal.reimbursementcalculationapp.userPanel;

import artifixal.reimbursementcalculationapp.ExcludedDays;
import artifixal.reimbursementcalculationapp.ExcludedDaysException;
import artifixal.reimbursementcalculationapp.Receipt;
import artifixal.reimbursementcalculationapp.ReceiptType;
import artifixal.reimbursementcalculationapp.daos.LimitDAO;
import artifixal.reimbursementcalculationapp.daos.RatesDAO;
import artifixal.reimbursementcalculationapp.daos.ReceiptTypeDAO;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ArtiFixal
 */
@WebServlet(name="CalculateClaim", urlPatterns={"/calculateClaim"})
public class CalculateClaim extends HttpServlet {

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        ObjectMapper mapper=new ObjectMapper()
				.registerModule(new JavaTimeModule());
		try{
			JsonNode jsonRequest=mapper.readValue(request.getInputStream(),
					JsonNode.class);
			JsonNode tripStartNode=jsonRequest.get("dateFrom");
			JsonNode tripEndNode=jsonRequest.get("dateTo");
			JsonNode receiptsListNode=jsonRequest.get("receipts");
			JsonNode excludedDaysNode=jsonRequest.get("excluded");
			JsonNode mileageNode=jsonRequest.get("mileage");
			if(tripStartNode==null||tripStartNode instanceof NullNode)
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: DateFrom not found");
				return;
			}
			if(tripEndNode==null||tripStartNode instanceof NullNode)
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: DateTo not found");
				return;
			}
			Optional<ArrayList<Receipt>> receiptsList=Optional.empty();
			if(receiptsListNode!=null)
			{
				try{
					ArrayList<Receipt> array=UserServletUtils
							.readReceiptsFromNode(receiptsListNode);
					receiptsList=Optional.ofNullable(array);
				}catch(NumberFormatException e){
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Receipt list is unreadable");
					return;
				}
			}
			Optional<ExcludedDays> excluded=Optional.empty();
			if(excludedDaysNode!=null)
			{
				try{
					excluded=Optional.of(ExcludedDays.readFrom(excludedDaysNode));
				}catch(ExcludedDaysException e){
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Exclude days list have errors: "+
								e.getMessage());
					return;
				}catch(IllegalArgumentException e){
					System.out.println(e);
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Exclude days list is unreadable");
					return;
				}
			}
			Optional<Integer> mileage=Optional.empty();
			if(mileageNode!=null)
			{
				try{
					String s=mileageNode.textValue();
					mileage=Optional.of(Integer.valueOf(s));
				}catch(NumberFormatException e){
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Mileage have to be a number");
					return;
				}
			}
			try(LimitDAO limitDao=new LimitDAO();ReceiptTypeDAO typeDao=
					new ReceiptTypeDAO();RatesDAO ratesDao=new RatesDAO()){
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
				System.out.println(e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occured during processing DB request");
			}catch(DateTimeParseException e){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Dates are unreadeable");
			}
		}catch(StreamReadException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Failed to read JSON request");
		}
    }
	
	/*private int getDaysCount(LocalDate from,LocalDate to,ExcludedDays ex)
	{
		from.until(to).
	}*/
	
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Calculates claim value";
    }// </editor-fold>

}
