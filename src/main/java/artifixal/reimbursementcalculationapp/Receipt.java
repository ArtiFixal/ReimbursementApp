package artifixal.reimbursementcalculationapp;

import java.math.BigDecimal;

/**
 *
 * @author ArtiFixal
 */
public class Receipt {
	private final int typeID;
	public String name;
	public BigDecimal value;

	public Receipt(int typeID,String name,BigDecimal value) {
		this.typeID=typeID;
		this.name=name;
		this.value=value;
	}

	public int getTypeID() {
		return typeID;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getValue() {
		return value;
	}
}
