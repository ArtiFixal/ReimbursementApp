package artifixal.reimbursementcalculationapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author ArtiFixal
 */
public class ExcludedDays {
	private final ArrayList<LocalDate> days;
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
	
	private static LocalDate getLocalDateFromNode(ObjectMapper mapper,JsonNode node)
			throws IllegalArgumentException
	{
		return mapper.convertValue(node,LocalDate.class);
	}
	
	private static Period getPeriodFromNode(ObjectMapper mapper,JsonNode node) 
			throws IllegalArgumentException
	{
		final JsonNode fromNode=node.get("from");
		final JsonNode toNode=node.get("to");
		return new Period(getLocalDateFromNode(mapper,fromNode),
				getLocalDateFromNode(mapper,toNode));
	}
	
	public static ExcludedDays readFrom(JsonNode json) throws ExcludedDaysException,
			IllegalArgumentException{
		final JsonNode daysNode=json.get("days");
		final JsonNode periodNode=json.get("periods");
		final ExcludedDays ed=new ExcludedDays();
		final ObjectMapper mapper=new ObjectMapper().registerModule(new JavaTimeModule());
		if(daysNode!=null&&!(daysNode instanceof NullNode))
		{
			int i=1;
			JsonNode el=daysNode.get(0);
			if(el!=null)
			{
				LocalDate previousDate=getLocalDateFromNode(mapper,el);
				ed.addDay(previousDate);
				while((el=daysNode.get(i))!=null)
				{
					LocalDate current=getLocalDateFromNode(mapper,el);
					if(previousDate.equals(current))
						throw new ExcludedDaysException("Duplicated days was found");
					else if(previousDate.until(current).
							equals(java.time.Period.ofDays(1)))
						throw new ExcludedDaysException("Days are ascending instead of being a period");
					ed.addDay(current);
					previousDate=current;
					i++;
				}
			}
		}
		else if(daysNode instanceof NullNode)
			throw new ExcludedDaysException("Days node is null");
		if(periodNode!=null&&!(daysNode instanceof NullNode))
		{
			int i=1;
			JsonNode el=periodNode.get(0);
			if(el!=null)
			{
				Period previous=getPeriodFromNode(mapper,el);
				ed.addPeriod(previous);
				while((el=periodNode.get(i))!=null)
				{
					Period current=getPeriodFromNode(mapper,el);
					if(previous.equals(current))
						throw new ExcludedDaysException("Duplicated periods was found");
					ed.addPeriod(current);
					previous=current;
					i++;
				}
			}
		}
		else if(periodNode instanceof NullNode)
			throw new ExcludedDaysException("Period node is null");
		return ed;
	}
	
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
