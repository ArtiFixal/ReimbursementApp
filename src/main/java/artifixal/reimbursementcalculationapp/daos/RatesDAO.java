package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.DBConfig;
import artifixal.reimbursementcalculationapp.Rate;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 *
 * @author ArtiFixal
 */
public class RatesDAO implements AutoCloseable{
	public final static int ALLOWANCE_RATE_ID=1;
	public final static int MILEAGE_RATE_ID=2;
			
	private final Connection con;

	public RatesDAO() throws SQLException {
		this.con=DBConfig.getInstance().createConnection();
	}

	public RatesDAO(Connection con) {
		this.con=con;
	}

	@Override
	public void close() throws SQLException {
		con.close();
	}
	
	public Rate getRate(int id) throws SQLException
	{
		try(Statement select=con.createStatement()){
			ResultSet result=select.executeQuery("SELECT amount,`limit` FROM rates WHERE id="+id);
			result.next();
			BigDecimal amount=result.getBigDecimal("amount");
			BigDecimal limit=result.getBigDecimal("limit");
			result.close();
			return new Rate(id,amount,limit);
		}
	}
	
	public Rate getAllowanceRate() throws SQLException
	{
		return getRate(ALLOWANCE_RATE_ID);
	}
	
	public Rate getMileageRate() throws SQLException
	{
		return getRate(MILEAGE_RATE_ID);
	}
	
	public boolean updateRate(int id,Optional<BigDecimal> rateAmount,Optional<BigDecimal> limit) throws SQLException
	{
		try(Statement update=con.createStatement()){
			StringBuilder sql=new StringBuilder("UPDATE rates SET ");
			if(rateAmount.isPresent())
				sql.append("amount=").append(rateAmount.get());
			if(limit.isPresent())
				sql.append("`limit`=").append(limit.get());
			sql.append(" WHERE id=").append(id);
			return update.executeUpdate(sql.toString())==1;
		}
	}
}
