package artifixal.reimbursementcalculationapp;

import java.time.LocalDate;
import java.util.Objects;

/**
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
	
	public String toJson()
	{
		return "{\"from\":\""+dateFrom.toString()+"\",\"to\":\""+dateTo.toString()+"\"}";
	}
}
