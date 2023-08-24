package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.Limit;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class responsible for operations on <b>limits</b> DB table.
 * 
 * Limits table have the given srtucture: <p>
 * limits( <br>
 *	id INT UNSIGNED <b>AI PK</b>, <br>
 *	amount DECIMAL(12,2) UNSIGNED NOT NULL, <br>
 *	description VARCHAR(30) NOT NULL <br>
 * )
 * 
 * @author ArtiFixal
 */
public class LimitDAO extends DAOObject{
	/**
	 * ID of total possible amount owed for single reimbursement claim.
	 */
	public final static int TOTAL_LIMIT_ID=1;
	
	/**
	 * ID of max personal car mileage considered to calculate reimbursement 
	 * value.
	 */
	public final static int DISTANCE_LIMIT_ID=2;

	public LimitDAO() throws SQLException {
		super();
	}

	public LimitDAO(Connection con) {
		super(con);
	}
	
	/**
	 * Selects single {@code Limit} record from the DB by given ID.
	 * 
	 * @param id ID by which we want to select {@code Limit} record.
	 * 
	 * @return Selected limit.
	 * @throws SQLException Any error occured during the query.
	 */
	public Limit getLimitById(int id) throws SQLException
	{
		BigDecimal amount=getSingleNumber("SELECT amount FROM limits WHERE id="
				+id,BigDecimal.class);
		return new Limit(id,amount);
	}
	
	/**
	 * Selects single total reimbursement limit, which is an total possible 
	 * amount owed for single reimbursement claim.
	 * 
	 * @return Total reimbursement limit.
	 * @throws SQLException Any error occured during the query.
	 */
	public Limit getTotalLimit() throws SQLException
	{
		return getLimitById(TOTAL_LIMIT_ID);
	}
	
	/**
	 * Selects distance limit, which is an max personal car mileage considered 
	 * into calculations of reimbursement claim.
	 * 
	 * @return Total distance limit.
	 * @throws SQLException Any error occured during the query.
	 */
	public Limit getDistanceLimit() throws SQLException
	{
		return getLimitById(DISTANCE_LIMIT_ID);
	}
	
	/**
	 * Updates single {@code Limit} by its ID in the DB.
	 * 
	 * @param id ID of a {@code Limit} to update.
	 * @param newAmount New limit
	 * 
	 * @return True if update was successful, false otherwise.
	 * @throws SQLException Any error occured during the DML query.
	 */
	public boolean updateLimit(int id,BigDecimal newAmount) throws SQLException
	{
		try(Statement update=con.createStatement()){
			return update.executeUpdate("UPDATE limits SET amount="+newAmount+" WHERE id="+id)==1;
		}
	}
}
