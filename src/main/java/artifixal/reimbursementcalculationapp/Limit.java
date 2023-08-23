package artifixal.reimbursementcalculationapp;

import java.math.BigDecimal;

/**
 * Class representing DB limit record.
 * 
 * @author ArtiFixal
 */
public class Limit {

	private final int id;
	private final BigDecimal amount;

	public Limit(int id,BigDecimal amount) {
		this.id=id;
		this.amount=amount;
	}

	public int getId() {
		return id;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
