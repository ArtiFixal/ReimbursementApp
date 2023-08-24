package artifixal.reimbursementcalculationapp.userPanel;

import artifixal.reimbursementcalculationapp.ExcludedDays;
import artifixal.reimbursementcalculationapp.ExcludedDaysException;
import artifixal.reimbursementcalculationapp.Receipt;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class used as base for servlets related to claims.
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
public abstract class ClaimServlet extends HttpServlet{
	
	/**
	 * Medthod in which servlet request is processed.
	 * 
	 * @param mapper Mapper used to convert JSON values.
	 * @param tripStartNode Node containing date of trip start.
	 * @param tripEndNode Node containing date of trip end.
	 * @param receiptsList User declared receipts.
	 * @param excluded Days excluded from allowance.
	 * @param mileage Personal car mileage.
	 * @param response Servlet respponse.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	protected abstract void processRequest(ObjectMapper mapper,JsonNode tripStartNode,
				JsonNode tripEndNode,Optional<ArrayList<Receipt>> receiptsList,
				Optional<ExcludedDays> excluded,Optional<Integer> mileage,
				HttpServletResponse response) throws IOException;
	
	public ArrayList<Receipt> readReceiptsFromNode(JsonNode node) throws NumberFormatException
	{
		final ArrayList<Receipt> receipts=new ArrayList<>(4);
		int i=0;
		JsonNode el;
		while((el=node.get(i))!=null)
		{
			int id=Integer.parseInt(el.get("id").asText());
			String name=el.get("name").asText();
			BigDecimal value=BigDecimal.valueOf(Float.parseFloat(el.get("value")
					.asText()));
			receipts.add(new Receipt(id,name,value));
			i++;
		}
		return receipts;
	}
	
	/**
	 * Validates request and only if its valid processes it.
	 * 
	 * @param request Servlet request.
	 * @param response Servlet response.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	protected void validateAndProcessRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
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
			// Check for existence of trip start date
			if(tripStartNode==null||tripStartNode instanceof NullNode)
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: DateFrom not found");
				return;
			}
			// Check for existence of trip end date
			if(tripEndNode==null||tripStartNode instanceof NullNode)
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: DateTo not found");
				return;
			}
			Optional<ArrayList<Receipt>> receiptsList=Optional.empty();
			// Get receipts if they exists
			if(receiptsListNode!=null&&!(receiptsListNode instanceof NullNode))
			{
				try{
					ArrayList<Receipt> array=readReceiptsFromNode(receiptsListNode);
					receiptsList=Optional.ofNullable(array);
				}catch(NumberFormatException e){
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Receipt list is unreadable");
					return;
				}
			}
			Optional<ExcludedDays> excluded=Optional.empty();
			// Get excluded days if they exists
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
			// Get mileage if it exists
			if(mileageNode!=null)
			{
				try{
					String s=mileageNode.asText();
					mileage=Optional.of(Integer.valueOf(s));
				}catch(NumberFormatException e){
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Mileage have to be a number");
					return;
				}
			}
			// If valid process request
			processRequest(mapper,tripStartNode,tripEndNode,receiptsList,
					excluded,mileage,response);
		}catch(StreamReadException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Malformed JSON request: Failed to read JSON request");
		}
	}
}
