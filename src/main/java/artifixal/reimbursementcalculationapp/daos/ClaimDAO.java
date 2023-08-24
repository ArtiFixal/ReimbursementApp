package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.ExcludedDays;
import artifixal.reimbursementcalculationapp.Receipt;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Class responsible for operations related to reimbursement claims. This 
 * class operates on <b>claims</b>, <b>mileages</b> and <b>receipts</b> tables,
 * since mileages are optional and claims are in relationship with user receitps 
 * as 1-*.
 * 
 * Tables have the given structure: <p>
 * claims( <br>
 *	id INT UNSIGNED <b>AI PK</b>, <br>
 *	userID INT UNSIGNED <b>FK</b>, <br>
 *	dateFrom DATE NOT NULL, <br>
 *	dateTo DATE NOT NULL, <br>
 *	ignoredDays JSON NOT NULL <br>
 * ) <p>
 * 
 * mileages( <br>
 *	id INT UNSIGNED <b>AI PK</b>, <br>
 *	claimID INT UNIGNED <b>FK</b>, <br>
 *	distance INT UNSIGNED NOT NULL <br>
 * ) <p>
 * 
 * receipts( <br>
 *	id INT UNSIGNED <b>AI PK</b>, <br>
 *	claimID INT UNSIGNED <b>FK</b>, <br>
 *	receiptID INT UNSIGNED <b>FK</b>, <br>
 *	amount DECIMAL(12,2) NOT NULL <br>
 * )
 * 
 * @author ArtiFixal
 */
public class ClaimDAO extends DAOObject{

	public ClaimDAO() throws SQLException {
		super();
	}

	public ClaimDAO(Connection con) {
		super(con);
	}
	
	/**
	 * Rollbacks transaction and sets auto commit to its default behavior.
	 * 
	 * @throws SQLException Any error occurred during transaction rollback.
	 */
	private void cancelTransaction() throws SQLException
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
				final long claimID=getLastInsertedId();
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
						cancelTransaction();
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
						cancelTransaction();
						return false;
					}
				}
			}
			else
			{
				cancelTransaction();
				return false;
			}
		}catch(SQLException e){
			cancelTransaction();
			throw e;
		}
		con.commit();
		con.setAutoCommit(true);
		return true;
	}
}
