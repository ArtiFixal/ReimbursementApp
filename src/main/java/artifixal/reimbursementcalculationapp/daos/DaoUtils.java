package artifixal.reimbursementcalculationapp.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class containing usefull methods used along DAO's.
 * 
 * @author ArtiFixal
 */
public class DaoUtils {

	private DaoUtils(){
		throw new AssertionError("Not instantiable");
	}
	
	/**
	 * Selects single number from executed query.
	 * 
	 * @param con Connection on which to execute query.
	 * @param sql Selection query of wanted number.
	 * 
	 * @return Selected number.
	 * @throws SQLException Any error occurred during the query.
	 */
	private static long getSingleLongNumer(Connection con,String sql) throws SQLException
	{
		Statement count=con.createStatement();
		ResultSet result=count.executeQuery(sql);
		result.next();
		long value=result.getLong(1);
		result.close();
		return value;
	}
	
	public static long countRows(Connection con) throws SQLException
	{
		return getSingleLongNumer(con,"SELECT COUNT(id) FROM types");
	}
	
	/**
	 * Retrives from database ID of last inserted record.
	 * 
	 * @param con Connection used to retrive ID
	 * 
	 * @return Last inserted ID.
	 * @throws SQLException Any error occurred during the query.
	 */
	public static long getLastInsertedId(Connection con) throws SQLException
	{
		return getSingleLongNumer(con,"SELECT last_insert_id();");
	}
}
