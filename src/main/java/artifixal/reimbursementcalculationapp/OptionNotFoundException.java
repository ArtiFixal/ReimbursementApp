package artifixal.reimbursementcalculationapp;

/**
 * Exception thrown when searched option wasn't found in a file.
 * 
 * @author ArtiFixal
 */
public class OptionNotFoundException extends Exception{
	/**
	 * For what we searched in the file.
	 */
	private final String soughtOption;
	
	/**
	 * Path to a file where we searched for the option.
	 */
	private final String pathWhereSearched;
	
	public OptionNotFoundException(String option,String pathToAFile) {
		super("Option: "+option+" was not found in the given file: "+pathToAFile);
		soughtOption=option;
		pathWhereSearched=pathToAFile;
	}
	
	public String getSoughtOption()
	{
		return soughtOption;
	}

	public String getPathWhereSearched() {
		return pathWhereSearched;
	}
}
