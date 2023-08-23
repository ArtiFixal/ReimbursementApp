package artifixal.reimbursementcalculationapp;

import java.math.BigDecimal;

/**
 * Class representing user receipt DB record.
 * 
 * @author ArtiFixal
 */
public class Receipt {
	/**
	 * ID of related {@code ReceiptType}.
	 */
	private final int typeID;
	
	/**
	 * Receipt type name.
	 */
	public String name;
	
	/**
	 * User declared receipt value.
	 */
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
