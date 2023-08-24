package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.DBConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class used as base for other DAO classes.
 * 
 * @author User
 */
public abstract class DAOObject implements AutoCloseable{
	
	/**
	 * Connection to the DB.
	 */
	protected final Connection con;

	public DAOObject() throws SQLException{
		this.con=DBConfig.getInstance().createConnection();
	}

	public DAOObject(Connection con){
		this.con=con;
	}
	
	/**
	 * Selects single number from executed query.
	 * 
	 * @param <T> Returned number type
	 * @param sql Selection query of wanted number.
	 * @param numberTypeToReturn What type to return
	 * 
	 * @return Selected number.
	 * @throws SQLException Any error occurred during the query.
	 */
	public <T extends Number> T getSingleNumber(String sql,
			Class<T> numberTypeToReturn) throws SQLException
	{
		Statement count=con.createStatement();
		T value;
		try(ResultSet result=count.executeQuery(sql)) {
			result.next();
			value=result.getObject(1,numberTypeToReturn);
		}
		return value;
	}
	
	/**
	 * Counts rows in given table.
	 * 
	 * @param tableName Table in which rows will be counted.
	 * 
	 * @return Row count.
	 * @throws SQLException Any error occurred during the query.
	 */
	public long countRows(String tableName) throws SQLException
	{
		return getSingleNumber("SELECT COUNT(id) FROM "+tableName,Long.class);
	}
	
	/**
	 * Retrives from database ID of last inserted record.
	 * 
	 * @return Last inserted ID.
	 * @throws SQLException Any error occurred during the query.
	 */
	public long getLastInsertedId() throws SQLException
	{
		return getSingleNumber("SELECT last_insert_id();",Long.class);
	}
	
	/**
	 * Closes connection with DB.
	 * 
	 * @throws SQLException Any error occured during connection close try.
	 */
	@Override
	public void close() throws SQLException {
		con.close();
	}
}
