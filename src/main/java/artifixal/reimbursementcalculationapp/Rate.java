package artifixal.reimbursementcalculationapp;

import java.math.BigDecimal;

/**
 * Class representing DB rate record.
 * 
 * @author ArtiFixal
 */
public class Rate {
	private final int id;
	
	/**
	 * Rate per one unit.
	 */
	private final BigDecimal rate;
	
	/**
	 * Max amount of money possible for this rate.
	 */
	private final BigDecimal limit;

	public Rate(int id,BigDecimal rates,BigDecimal limit) {
		this.id=id;
		this.rate=rates;
		this.limit=limit;
	}

	public int getId() {
		return id;
	}

	public BigDecimal getRate() {
		return rate;
	}
	
	public BigDecimal getLimit() {
		return limit;
	}
}
