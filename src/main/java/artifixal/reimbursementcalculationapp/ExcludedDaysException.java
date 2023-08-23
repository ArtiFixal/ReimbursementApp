package artifixal.reimbursementcalculationapp;

/**
 * Exception thrown when there are errors related to the validity 
 * excluded {@code ExcludedDays}
 * 
 * @author ArtiFixal
 */
public class ExcludedDaysException extends Exception{

	public ExcludedDaysException() {
		super();
	}
	
	/**
	 * @param msg Error occured.
	 */
	public ExcludedDaysException(String msg)
	{
		super(msg);
	}
}
