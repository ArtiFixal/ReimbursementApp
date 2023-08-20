package artifixal.reimbursementcalculationapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author ArtiFixal
 */
public class TestConfig {
	
	private final static File TEST_CONFIG=new File("testConfig.cfg");
	
	private final String appUrl;
	
	private static TestConfig config;

	private TestConfig() throws IOException, FileNotFoundException,
			NullPointerException, OptionNotFoundException{
		ConfigIO configFile=new ConfigIO(TEST_CONFIG);
		appUrl=configFile.readOptionValue("<appURL>");
	}
	
	public static TestConfig getInstance()
	{
		if(config==null)
		{
			try{
				config=new TestConfig();
			}catch(FileNotFoundException|NullPointerException e){
				// Create example config
				synchronized(TEST_CONFIG){
					String configBuilder=
							"<appURL>http://localhost:8080/ReimbursementCalculationApp";
					ConfigIO w=new ConfigIO(TEST_CONFIG);
					try{
						w.writeEntireConfig(configBuilder);
					}catch(IOException ex){
						//Logger.getLogger(TestConfig.class.getName()).log(Level.SEVERE,null,ex);
					}
					try{
						config=new TestConfig();
					}catch(Exception ex){
						// Abadon all hope, ye who enter here.
					}
				}
			}catch(OptionNotFoundException e){
				throw new RuntimeException("Unable to continue - test config is "
						+ "incomplete: "+e.getMessage());
			}catch(IOException e){
				throw new RuntimeException("Unable to continue - An error "
						+ "occured during reading of test config: "+e.getMessage());
			}
		}
		return config;
	}
	
	public String getAppUrl()
	{
		return appUrl;
	}
}
