package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.DBConfig;
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
 *
 * @author ArtiFixal
 */
public class ReceiptTypeDAO implements AutoCloseable{
	
	private final Connection con;

	public ReceiptTypeDAO() throws SQLException{
		con=DBConfig.getInstance().createConnection();
	}

	public ReceiptTypeDAO(Connection con) {
		this.con=con;
	}
	
	public ReceiptType getReceiptById(long id,boolean includeLimit) throws SQLException
	{
		Statement selectReceipt=con.createStatement();
		ResultSet result;
		if(includeLimit)
			result=selectReceipt.executeQuery("SELECT name,limit FROM types WHERE id="+id);
		else
			result=selectReceipt.executeQuery("SELECT name FROM types WHERE id="+id);
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
	 * @throws SQLException 
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
					return DaoUtils.getLastInsertedId(con);
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
	 * @throws SQLException 
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
	 * @throws SQLException 
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
	 * @throws SQLException 
	 */
	public ArrayList<ReceiptType> getAllReceiptsMinimal() throws SQLException
	{
		Statement selection=con.createStatement();
		ResultSet result=selection.executeQuery("SELECT id,name FROM types");
		return makeReceiptsArray(result);
	}
	
	/**
	 * 
	 * @param id
	 * @param newName
	 * @param newLimit
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public boolean updateReceipt(long id,Optional<String> newName,Optional<BigDecimal> newLimit) throws SQLException
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
	
	public boolean updateReceipt(ReceiptType r,Optional<String> newName,Optional<BigDecimal> newLimit) throws SQLException
	{
		return updateReceipt(r.getId(),newName,newLimit);
	}
	
	/**
	 * Deletes {@code ReceiptType} from DB by given ID.
	 * @param id ID of receipt to delete;
	 * 
	 * @return True if receipt was deleted, false otherwise.
	 * @throws SQLException 
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
	 * @param r ReceiptType to delete;
	 * 
	 * @return True if receipt was deleted, false otherwise.
	 * @throws SQLException 
	 */
	public boolean deleteReceipt(ReceiptType r) throws SQLException{
		return deleteReceipt(r.getId());
	}
	
	/**
	 * Closes connection with DB.
	 * 
	 * @throws SQLException 
	 */
	@Override
	public void close() throws SQLException {
		con.close();
	}
}
