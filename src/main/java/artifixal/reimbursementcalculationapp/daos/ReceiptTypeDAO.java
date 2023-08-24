package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.ReceiptType;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Class responsible for operations on <b>types</b> DB table. <p>
 * 
 * Types table have the given structure: <br>
 * 
 * types( <br>
 *	id INT UNSIGNED <b>AI PK</b>, <br>
 *	`limit` DECIMAL(12,2) UNSIGNED NOT NULL, <br>
 *	name VARCHAR(50) NOT NULL <br>
 * ) 
 * 
 * @author ArtiFixal
 */
public class ReceiptTypeDAO extends DAOObject{

	public ReceiptTypeDAO() throws SQLException{
		super();
	}

	public ReceiptTypeDAO(Connection con) {
		super(con);
	}
	
	/**
	 * Selects single {@code ReceiptType} record from DB table.
	 * 
	 * @param id ID by which we want to select {@code ReceiptType} record.
	 * @param includeLimit Should limit be included into query?
	 * 
	 * @return Selected receipt type or null if there is no result.
	 * @throws SQLException Any error occurred during the query.
	 * @see ReceiptType
	 */
	public ReceiptType getReceiptTypeById(long id,boolean includeLimit) throws SQLException
	{
		Statement selectReceipt=con.createStatement();
		ResultSet result;
		if(includeLimit)
			result=selectReceipt.executeQuery("SELECT name,`limit` FROM types WHERE id="+id);
		else
			result=selectReceipt.executeQuery("SELECT name FROM types WHERE id="+id);
		if(!result.isBeforeFirst())
			return null;
		result.next();
		String name=result.getString("name");
		BigDecimal limit=null;
		try{
			limit=result.getBigDecimal("limit");
		}catch(SQLException e){
			// Do nothing 
		}
		return new ReceiptType(id,name,Optional.ofNullable(limit));
	}
	
	/**
	 * Inserts new receipt type into DB.
	 * 
	 * @param r ReceiptType to be inserted.
	 * 
	 * @return Inserted ID or -1 if receipt already exists.
	 * @throws SQLException Any error occurred during the DML query.
	 */
	public long createReceipt(ReceiptType r) throws SQLException
	{
		BigDecimal limit=r.getLimit();
		if(limit.compareTo(BigDecimal.ZERO)==-1)
			throw new IllegalArgumentException("Receipt limit can't be negative number");
		try(PreparedStatement existanceCheck=con.prepareStatement("SELECT id FROM types WHERE name LIKE ?")){
			existanceCheck.setString(1,r.getName());
			// Avoid receipt duplications
			if(!existanceCheck.executeQuery().next())
			{
				existanceCheck.close();
				try (PreparedStatement insertStatement=con.prepareStatement("INSERT INTO types VALUES(NULL,?,?)")) {
					insertStatement.setBigDecimal(1,limit);
					insertStatement.setString(2,r.getName());
					insertStatement.executeUpdate();
					return getLastInsertedId();
				}
			}
		}
		return -1;
	}
	
	/**
	 * Creates array of receipts from {@code ResultSet} data.
	 * 
	 * @param result Result of an SQL Querry.
	 * 
	 * @return All available receipts.
	 * @throws SQLException Any error occurred during the query.
	 * @see ReceiptType
	 */
	private ArrayList<ReceiptType> makeReceiptsArray(ResultSet result) throws SQLException
	{
		ArrayList<ReceiptType> receipts=new ArrayList<>();
		while(result.next())
		{
			BigDecimal limit=null;
			try{
				limit=result.getBigDecimal("limit");
			}catch(SQLException e){
				// Do nothing - optional value
			}
			receipts.add(new ReceiptType(result.getInt(1),result.getString("name"),
					Optional.ofNullable(limit)));
		}
		result.close();
		return receipts;
	}
	
	/**
	 * Retrieves all {@code ReceiptType} types from DB.
	 * 
	 * @return All available receipts.
	 * @throws SQLException Any error occurred during the query.
	 * @see ReceiptType
	 */
	public ArrayList<ReceiptType> getAllReceipts() throws SQLException
	{
		Statement selection=con.createStatement();
		ResultSet result=selection.executeQuery("SELECT * FROM types");
		return makeReceiptsArray(result);
	}
	
	/**
	 * Retrieves all {@code ReceiptType} types from DB without <b>limit</b> column.
	 * 
	 * @return All available receipts without limit column.
	 * @throws SQLException Any error occurred during the query.
	 * @see ReceiptType
	 */
	public ArrayList<ReceiptType> getAllReceiptsMinimal() throws SQLException
	{
		Statement selection=con.createStatement();
		ResultSet result=selection.executeQuery("SELECT id,name FROM types");
		return makeReceiptsArray(result);
	}
	
	/**
	 * Updates {@code ReceiptType} columns by its ID. <p>
	 * 
	 * This method allows to update both name and limit at the same time or only
	 * one of them.
	 * 
	 * @param id ID of which {@code ReceiptType} to update
	 * @param newName If we want to update name
	 * @param newLimit If we want to update limit
	 * 
	 * @return True if update was successful, false otherwise.
	 * @throws SQLException Any error occurred during the DML query.
	 */
	public boolean updateReceipt(long id,Optional<String> newName,
			Optional<BigDecimal> newLimit) throws SQLException
	{
		StringBuilder sqlQuery=new StringBuilder("UPDATE types SET ");
		if(newName.isPresent()&&newLimit.isPresent())
			sqlQuery.append("name=?,`limit`=?");
		else if(newName.isPresent())
			sqlQuery.append("name=?");
		else
			sqlQuery.append("`limit`=?");
		sqlQuery.append(" WHERE id=");
		sqlQuery.append(id);
		PreparedStatement update=con.prepareStatement(sqlQuery.toString());
		if(newName.isPresent()&&newLimit.isPresent())
		{
			update.setString(1,newName.get());
			update.setBigDecimal(2,newLimit.get());
		}
		else if(newName.isPresent())
			update.setString(1,newName.get());
		else
			update.setBigDecimal(1,newLimit.get());
		int result=update.executeUpdate();
		update.close();
		// Since we update by ID
		return result==1;
	}
	
	/**
	 * Updates {@code ReceiptType} columns for given {@code ReceiptType} object
	 * in DB. <p>
	 * 
	 * This method allows to update both name and limit at the same time or only
	 * one of them.
	 * 
	 * @param r Object which we want to update
	 * @param newName If we want to update name
	 * @param newLimit If we want to update limit
	 * 
	 * @return True if update was successful, false otherwise.
	 * @throws SQLException Any error occurred during the DML query.
	 */
	public boolean updateReceipt(ReceiptType r,Optional<String> newName,
			Optional<BigDecimal> newLimit) throws SQLException
	{
		return updateReceipt(r.getId(),newName,newLimit);
	}
	
	/**
	 * Deletes {@code ReceiptType} from DB by given ID.
	 * 
	 * @param id ID of receipt to delete;
	 * 
	 * @return True if receipt was deleted, false otherwise.
	 * @throws SQLException Any error occurred during the DML query.
	 */
	public boolean deleteReceipt(long id) throws SQLException
	{
		Statement deletion=con.createStatement();
		int result=deletion.executeUpdate("DELETE FROM types WHERE id="+id);
		deletion.close();
		// Since we delete by ID
		return result==1;
	}
	
	/**
	 * Deletes given {@ code ReceiptType} from DB.
	 * 
	 * @param r ReceiptType to delete;
	 * 
	 * @return True if receipt was deleted, false otherwise.
	 * @throws SQLException Any error occurred during the DML query.
	 */
	public boolean deleteReceipt(ReceiptType r) throws SQLException{
		return deleteReceipt(r.getId());
	}
}
