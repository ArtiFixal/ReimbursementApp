package artifixal.reimbursementcalculationapp;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Class representing date periods logic.
 * 
 * @author ArtiFixal
 */
public class Period {
	private final LocalDate dateFrom;
	private final LocalDate dateTo;

	public Period(LocalDate dateFrom,LocalDate dateTO) {
		this.dateFrom=dateFrom;
		this.dateTo=dateTO;
	}

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public LocalDate getDateTo() {
		return dateTo;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Period)
		{
			final Period other=(Period)obj;
			return dateFrom.equals(other.getDateFrom())&&
					dateTo.equals(other.getDateTo());
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash=3;
		hash=29*hash+Objects.hashCode(this.dateFrom);
		hash=29*hash+Objects.hashCode(this.dateTo);
		return hash;
	}
	
	/**
	 * Converts period into JSON object.
	 * 
	 * @return Converted JSON object;
	 */
	public String toJson()
	{
		return "{\"from\":\""+dateFrom.toString()+"\",\"to\":\""+dateTo.toString()+"\"}";
	}
	
	/**
	 * Converts this {@code Period} into {@link java.time.Period}.
	 * 
	 * @return Converted period.
	 * @see java.time.Period
	 */
	public java.time.Period toJavaPeriod()
	{
		return dateFrom.until(dateTo);
	}
	
	/**
	 * Returns number of days that given month have.
	 * 
	 * @param month The month as number which will be taken into consideration 
	 * how many days does it have.
	 * @param isLeapYear Do february have 29 days?
	 * 
	 * @return Month number of days.
	 * @throws IllegalArgumentException If month is out of range [1,12].
	 */
	private static int getMonthLength(int month,boolean isLeapYear)
			throws IllegalArgumentException
	{
		switch(month){
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				return 31;
			case 2:
				return (isLeapYear)?29:28;
			case 4:
			case 6:
			case 9:
			case 11:
				return 30;
		}
		throw new IllegalArgumentException("Month must be a value in range from "
				+ " to 12, but given: "+month);
	}
	
	/**
	 * Counts passed days betwen start and end date
	 * 
	 * @return Number of days passed.
	 */
	public int getNumberOfDaysPassed()
	{
		return getNumberOfDaysPassed(dateFrom,dateTo);
	}
	
	/**
	 * Counts passed days betwen two dates.
	 * 
	 * @param from From when count passed days.
	 * @param to To when count passed days.
	 * 
	 * @return Number of days passed, can be negative if date to is before 
	 * date from.
	 */
	public static int getNumberOfDaysPassed(LocalDate from,LocalDate to)
	{
		int duration=0;
		java.time.Period passed=from.until(to);
		// Count days of months in first year
		for(int i=from.getMonthValue();i<from.getMonthValue()+
				passed.getMonths()-1;i++)
		{
			duration+=getMonthLength(i,from.isLeapYear());
		}
		// Add passed days
		duration+=passed.getDays();
		// Sum the rest lengths of years.
		for(int i=from.getYear()+1;i<from.getYear()+passed.getYears();i++)
		{
			LocalDate year=LocalDate.of(i,1,1);
			duration+=year.lengthOfYear();
		}
		return duration;
	}
}
