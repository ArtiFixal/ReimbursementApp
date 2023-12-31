package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.Rate;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class responsible for operations on <b>rates</b> DB table. <p>
 * 
 * Rates table have the given structure: <p>
 * rates( <br>
 *	id INT UNSIGNED <b>AI PK</b>, <br>
 *	amount DECIMAL(12,2) UNSIGNED NOT NULL, <br>
 *	`limit` DECIMAL(12,2) UNSIGNED NOT NULL, <br>
 *	description VARCHAR(30) NOT NULL <br>
 * )
 * 
 * @author ArtiFixal
 */
public class RatesDAO extends DAOObject{
	/**
	 * ID of the allowance rate.
	 */
	public final static int ALLOWANCE_RATE_ID=1;
	
	/**
	 * ID of the personal car mileage rate.
	 */
	public final static int MILEAGE_RATE_ID=2;

	public RatesDAO() throws SQLException {
		super();
	}

	public RatesDAO(Connection con) {
		super(con);
	}
	
	/**
	 * Selects single {@code Rate} record from the DB by given ID.
	 * 
	 * @param id ID by which we want to select {@code Rate} record.
	 * 
	 * @return Selected rate or null if there is no result.
	 * 
	 * @throws SQLException Any error occured during the query.
	 * @see Rate
	 */
	public Rate getRate(int id) throws SQLException
	{
		try(ResultSet result=getElementByID("amount,`limit`","rates",id)){
			if(!result.isBeforeFirst())
				return null;
			result.next();
			BigDecimal amount=result.getBigDecimal("amount");
			BigDecimal limit=result.getBigDecimal("limit");
			return new Rate(id,amount,limit);
		}
	}
	
	/**
	 * Selects allowance rate from the DB.
	 * 
	 * @return Allowance rate.
	 * @throws SQLException Any error occured during the query.
	 */
	public Rate getAllowanceRate() throws SQLException
	{
		return getRate(ALLOWANCE_RATE_ID);
	}
	
	/**
	 * Selects personal car mileage rate from the DB.
	 * 
	 * @return Mileage rate.
	 * @throws SQLException Any error occured during the query.
	 */
	public Rate getMileageRate() throws SQLException
	{
		return getRate(MILEAGE_RATE_ID);
	}
	
	/**
	 * Updates single {@code Rate} by its ID in the DB.
	 * 
	 * @param id ID of a {@code Rate} to update.
	 * @param newRateAmount If we want to update rate amount.
	 * @param newLimit If we want to update limit.
	 * 
	 * @return True if update was successful, false otherwise.
	 * @throws SQLException Any error occured during the DML query.
	 */
	public boolean updateRate(int id,OptionalDBField<BigDecimal> newRateAmount,
			OptionalDBField<BigDecimal> newLimit) throws SQLException
	{
            try(PreparedStatement update=createUpdateStatement("rates", "id="+id,newRateAmount,newLimit)){
                    return update.executeUpdate()==1;
		}
	}
}
