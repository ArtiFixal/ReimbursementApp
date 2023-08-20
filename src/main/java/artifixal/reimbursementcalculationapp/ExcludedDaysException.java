package artifixal.reimbursementcalculationapp;

/**
 *
 * @author ArtiFixal
 */
public class ExcludedDaysException extends Exception{

	public ExcludedDaysException() {
		super();
	}
	
	public ExcludedDaysException(String msg)
	{
		super(msg);
	}
}
