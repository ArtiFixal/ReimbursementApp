package artifixal.reimbursementcalculationapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class responsible for all kind of read/write operations related to configs.
 * 
 * @author ArtiFixal
 */
public class ConfigIO {
	private File config;

	public ConfigIO(File config) {
		this.config=config;
	}
	
	public String readOptionValue(String option) throws FileNotFoundException, 
			IOException, NullPointerException, OptionNotFoundException {
		try(FileReader r=new FileReader(config);BufferedReader br=new BufferedReader(r,512)){
			String line;
			while((line=br.readLine())!=null)
			{
				if(line.startsWith(option))
					return line.substring(line.indexOf(">")+1);
			}
		}
		throw new OptionNotFoundException(option,config.getPath());
	}
	
	public void writeOption(String option,String value) throws IOException{
		try(FileWriter w=new FileWriter(config);BufferedWriter bw=new BufferedWriter(w)){
			bw.write("<");
			bw.write(option);
			bw.write(">");
			bw.write(value);
			bw.write('\n');
		}
	}
	
	public void writeEntireConfig(String configToWrite) throws IOException{
		try(FileWriter w=new FileWriter(config);BufferedWriter bw=new BufferedWriter(w)){
			bw.write(configToWrite);
		}
	}
}
