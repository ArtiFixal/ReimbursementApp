package artifixal.reimbursementcalculationapp;

import java.math.BigDecimal;

/**
 *
 * @author ArtiFixal
 */
public class Rate {
	private final int id;
	private BigDecimal rate;
	private BigDecimal limit;

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
