package artifixal.reimbursementcalculationapp.userPanel;

import artifixal.reimbursementcalculationapp.Receipt;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author ArtiFixal
 */
public class UserServletUtils {
	public static ArrayList<Receipt> readReceiptsFromNode(JsonNode node) throws NumberFormatException
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
}
