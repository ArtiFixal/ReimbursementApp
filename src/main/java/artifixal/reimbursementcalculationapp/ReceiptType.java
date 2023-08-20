package artifixal.reimbursementcalculationapp;

import java.math.BigDecimal;
import java.util.Optional;

/**
 *
 * @author ArtiFixal
 */
public class ReceiptType {
	private final long id;
	
	/**
	 * Name of receipt type
	 */
	public String name;
	
	/**
	 * Max ammount of money owed per this receipt type.
	 */
	private Optional<BigDecimal> limit;

	public ReceiptType(long id,String name) {
		this.id=id;
		this.name=name;
	}
	
	public ReceiptType(long id,String name,Optional<BigDecimal> limit) {
		this.id=id;
		this.name=name;
		this.limit=limit;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return Limit value or -1 if unknown.
	 */
	public BigDecimal getLimit() {
		return limit.orElse(BigDecimal.valueOf(-1));
	}

	public void setName(String name) {
		this.name=name;
	}

	public void setLimit(Optional<BigDecimal> limit) {
		this.limit=limit;
	}
}
