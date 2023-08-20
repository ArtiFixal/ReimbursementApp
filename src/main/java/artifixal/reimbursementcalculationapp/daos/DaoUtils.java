package artifixal.reimbursementcalculationapp.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ArtiFixal
 */
public class DaoUtils {

	private DaoUtils(){
		throw new AssertionError("Not instantable");
	}
	
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
	
	public static long getLastInsertedId(Connection con) throws SQLException
	{
		return getSingleLongNumer(con,"SELECT last_insert_id();");
	}
	
}
