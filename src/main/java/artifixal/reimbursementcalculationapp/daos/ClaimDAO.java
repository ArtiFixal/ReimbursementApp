package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.DBConfig;
import artifixal.reimbursementcalculationapp.ExcludedDays;
import artifixal.reimbursementcalculationapp.Receipt;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;

/**
 *
 * @author ArtiFixal
 */
public class ClaimDAO implements AutoCloseable{
	private final Connection con;

	public ClaimDAO() throws SQLException {
		this.con=DBConfig.getInstance().createConnection();
	}

	public ClaimDAO(Connection con) {
		this.con=con;
	}
	
	private void rollback() throws SQLException
	{
		con.rollback();
		con.setAutoCommit(true);
	}
	
	public boolean createClaim(LocalDate dateFrom,LocalDate dateTo,Optional<ArrayList<Receipt>> receipts,Optional<ExcludedDays> ignoredDays,Optional<Integer> mileage) throws SQLException
	{
		// Start transaction
		con.setAutoCommit(false);
		// Insert claim
		try(PreparedStatement insert=con.prepareStatement("INSERT INTO claims VALUES(NULL,?,?,?,?)"))
		{
			insert.setInt(1,0);
			insert.setDate(2,Date.valueOf(dateFrom));
			insert.setDate(3,Date.valueOf(dateTo));
			if(ignoredDays.isPresent())
			{
				ExcludedDays excluded=ignoredDays.get();
				String json=excluded.toJson();
				insert.setString(4,json);
			}
			else
				insert.setString(4,"{}");
			if(insert.executeUpdate()==1)
			{
				final long claimID=DaoUtils.getLastInsertedId(con);
				// Check for receipts existence
				if(receipts.isPresent())
				{
					int done=0;
					// Insert receipts
					for(Receipt r:receipts.get())
					{
						Statement insertReceipts=con.createStatement();
						String sql="INSERT INTO receipts VALUES(NULL,"+claimID+
								","+r.getTypeID()+","+r.getValue()+")";
						if(insertReceipts.executeUpdate(sql)==1)
							done++;
					}
					if(done!=receipts.get().size())
					{
						rollback();
						return false;
					}
				}
				// Check for mileage existence
				if(mileage.isPresent())
				{
					// Insert mileage
					Statement insertMileage=con.createStatement();
					int result=insertMileage.executeUpdate("INSERT INTO mileages VALUES(NULL,"+
							claimID+","+mileage.get().toString()+")");
					if(result!=1)
					{
						rollback();
						return false;
					}
				}
			}
			else
			{
				rollback();
				return false;
			}
		}catch(SQLException e){
			rollback();
			throw e;
		}
		con.commit();
		con.setAutoCommit(true);
		return true;
	}

	@Override
	public void close() throws SQLException {
		con.close();
	}
	
}
