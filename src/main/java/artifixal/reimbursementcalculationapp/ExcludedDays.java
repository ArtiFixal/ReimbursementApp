package artifixal.reimbursementcalculationapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class responsible for handling days excluded from the claim calculation.
 * 
 * @author ArtiFixal
 */
public class ExcludedDays {
	/**
	 * Single days excluded from the claim.
	 */
	private final ArrayList<LocalDate> days;
	
	/**
	 * Periods excluded from the claim.
	 */
	private final ArrayList<Period> periods;

	public ExcludedDays() {
		days=new ArrayList<>(4);
		periods=new ArrayList<>(4);
	}

	public ArrayList<LocalDate> getDays() {
		return days;
	}

	public ArrayList<Period> getPeriods() {
		return periods;
	}
	
	public void addDay(LocalDate day){
		days.add(day);
	}
	
	public void addPeriod(Period p){
		periods.add(p);
	}
	
	/**
	 * Converts date from JSON object request node into {@code LocalDate}.
	 * 
	 * @param mapper Referene to the mapper.
	 * @param node Node containing date to convert.
	 * 
	 * @return Converted date.
	 * 
	 * @throws IllegalArgumentException If its impossible to the convert date 
	 * into {@code LocalDate}.
	 * @see LocalDate
	 */
	private static LocalDate getLocalDateFromNode(ObjectMapper mapper,JsonNode node)
			throws IllegalArgumentException
	{
		return mapper.convertValue(node,LocalDate.class);
	}
	
	/**
	 * Converts period from JSON object request into {@link Period}.
	 * 
	 * @param mapper Referene to the mapper.
	 * @param node Node containing period to convert.
	 * 
	 * @return Converted period.
	 * 
	 * @throws IllegalArgumentException If its impossible to convert JSON period
	 * into {@code Period}.
	 */
	private static Period getPeriodFromNode(ObjectMapper mapper,JsonNode node) 
			throws IllegalArgumentException
	{
		final JsonNode fromNode=node.get("from");
		final JsonNode toNode=node.get("to");
		return new Period(getLocalDateFromNode(mapper,fromNode),
				getLocalDateFromNode(mapper,toNode));
	}
	
	private static Comparator<LocalDate> createAscLocalDateComparator()
	{
		return new Comparator<LocalDate>() {
			@Override
			public int compare(LocalDate o1,LocalDate o2){
				return o1.compareTo(o2);
			}
		};
	}
	
	private static Comparator<Period> createAscPeriodComparator()
	{
		return new Comparator<Period>() {
			@Override
			public int compare(Period o1,Period o2){
				int result=o1.getDateFrom().compareTo(o2.getDateFrom());
				if(result==0)
				{
					return o1.getDateTo().compareTo(o2.getDateTo());
				}
				return result;
			}
		};
	}
	
	/**
	 * Reads {@code ExcludedDays} object from given {@code JsonNode}.
	 * 
	 * @param node From which to read.
	 * 
	 * @return ExcludedDays object.
	 * 
	 * @throws ExcludedDaysException If any syntax error is present.
	 * @throws IllegalArgumentException If any date isn't convertable into 
	 * {@code LodalDate}
	 */
	public static ExcludedDays readFrom(JsonNode node) throws ExcludedDaysException,
			IllegalArgumentException{
		final JsonNode daysNode=node.get("days");
		final JsonNode periodNode=node.get("periods");
		final ExcludedDays excluded=new ExcludedDays();
		final ObjectMapper mapper=new ObjectMapper()
				.registerModule(new JavaTimeModule());
		// Check for days existence
		if(daysNode!=null&&!(daysNode instanceof NullNode))
		{
			int i=0;
			JsonNode nextNode;
			while((nextNode=daysNode.get(i))!=null)
			{
				excluded.addDay(getLocalDateFromNode(mapper,nextNode));
				i++;
			}
			excluded.getDays().sort(createAscLocalDateComparator());
			LocalDate previousDate=excluded.getDays().get(0);
			for(int j=1;j<excluded.getDays().size();j++)
			{
				LocalDate current=excluded.getDays().get(j);
				if(previousDate.equals(current))
					throw new ExcludedDaysException("Duplicated days were found");
				else if(Period.getNumberOfDaysPassed(current
						,previousDate)==1)
					throw new ExcludedDaysException("Days are increasing in order instead of being a period");
				previousDate=current;
			}
		}
		// Check for periods existence
		if(periodNode!=null&&!(daysNode instanceof NullNode))
		{
			int i=0;
			JsonNode nextNode=periodNode.get(0);
			if(nextNode!=null)
			{
				while((nextNode=periodNode.get(i))!=null)
				{
					Period current=getPeriodFromNode(mapper,nextNode);
					int compareResult=current.getDateFrom().compareTo(current.getDateTo());
					if(compareResult>0)
						throw new ExcludedDaysException("Period dateFrom is after dateTo");
					else if(compareResult==0)
						throw new ExcludedDaysException("Period dateFrom and dateTo are the same day - Period should be a day instead");
					excluded.addPeriod(current);
					i++;
				}
				excluded.getPeriods().sort(createAscPeriodComparator());
				Period previous=excluded.getPeriods().get(0);
				for(int j=1;j<excluded.getPeriods().size();j++)
				{
					Period current=excluded.getPeriods().get(j);
					if(previous.equals(current))
						throw new ExcludedDaysException("Duplicated periods were found");
					else if(Period.getNumberOfDaysPassed(previous.getDateTo(),current.getDateFrom())==1)
						throw new ExcludedDaysException("Periods follow each other instead of being a single one");
					previous=current;
				}
			}
		}
		return excluded;
	}
	
	/**
	 * Converts this object into JSON object.
	 * 
	 * @return JSON object valid to insert into DB.
	 */
	public String toJson()
	{
		final StringBuilder json=new StringBuilder("{");
		boolean daysExist=false;
		System.out.println(days.size());
		if(!days.isEmpty())
		{
			json.append("\"days\":[");
			daysExist=true;
			for(int i=0;i<days.size()-1;i++)
			{
				json.append("\"")
						.append(days.get(i).toString())
						.append("\",");
			}
			json.append("\"")
					.append(days.get(days.size()-1).toString())
					.append("\"]");
		}
		if(!periods.isEmpty())
		{
			if(daysExist)
				json.append(",");
			json.append("\"periods\":[");
			for(int i=0;i<periods.size()-1;i++)
			{
				json.append(periods.get(i).toJson())
						.append(",");
			}
			json.append(periods.get(periods.size()-1).toJson())
					.append("]");
		}
		json.append("}");
		return json.toString();
	}
}
