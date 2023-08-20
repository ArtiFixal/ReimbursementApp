package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.DBConfig;
import artifixal.reimbursementcalculationapp.Limit;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ArtiFixal
 */
public class LimitDAO implements AutoCloseable{

	public final static int TOTAL_LIMIT_ID=1;
	public final static int DISTANCE_LIMIT_ID=2;
	
	private final Connection con;

	public LimitDAO() throws SQLException {
		con=DBConfig.getInstance().createConnection();
	}

	public LimitDAO(Connection con) {
		this.con=con;
	}

	@Override
	public void close() throws SQLException {
		con.close();
	}
	
	public Limit getLimitById(int id) throws SQLException
	{
		try(Statement select=con.createStatement()){
			ResultSet result=select.executeQuery("SELECT amount FROM limits WHERE id="+id);
			result.next();
			BigDecimal amount=result.getBigDecimal("amount");
			return new Limit(id,amount);
		}	
	}
	
	public Limit getTotalLimit() throws SQLException
	{
		return getLimitById(TOTAL_LIMIT_ID);
	}
	
	public Limit getDistanceLimit() throws SQLException
	{
		return getLimitById(DISTANCE_LIMIT_ID);
	}
	
	public boolean updateLimit(int id,BigDecimal newAmount) throws SQLException
	{
		try(Statement update=con.createStatement()){
			return update.executeUpdate("UPDATE limits SET amount="+newAmount+" WHERE id="+id)==1;
		}
	}
}
