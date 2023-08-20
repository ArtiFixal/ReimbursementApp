package artifixal.reimbursementcalculationapp;

import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @author ArtiFixal
 */
public class Claim {
	private int id;
	//private int userID;
	public LocalDate dateFrom;
	public LocalDate dateTo;
	public Optional<String> excludedDaysJson;
	public Optional<Receipt[]> receipts;
	public Optional<Integer> mileage;

	public Claim(int id,LocalDate dateFrom,LocalDate dateTo,Optional<String> excludedDaysJson) {
		this.id=id;
		this.dateFrom=dateFrom;
		this.dateTo=dateTo;
		this.excludedDaysJson=excludedDaysJson;
	}

	public int getId() {
		return id;
	}

	public LocalDate getDateFrom() {
		return dateFrom;
	}

	public LocalDate getDateTo() {
		return dateTo;
	}

	public String getExcludedDaysJson() {
		return excludedDaysJson.orElse("{}");
	}
}
