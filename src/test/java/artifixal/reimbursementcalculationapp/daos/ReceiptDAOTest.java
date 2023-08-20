package artifixal.reimbursementcalculationapp.daos;

import artifixal.reimbursementcalculationapp.DBConfig;
import artifixal.reimbursementcalculationapp.ReceiptType;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 *
 * @author ArtiFixal
 */
public class ReceiptDAOTest {
	
	private static ReceiptTypeDAO dao;
	private static long lastInsertedReceiptID=0;
	
	@BeforeAll
	public static void createDAOWithConnection()
	{
		try{
			dao=new ReceiptTypeDAO();
		}catch(CommunicationsException e){
			System.out.println("[Error]: DB is unreachable!");
		}
		catch(SQLException e){
			System.out.println(e);
		}
		assertNotNull(dao);
	}
	
	@Test
	@Order(1)
	public void insertDataTest() throws SQLException
	{
		ReceiptType r=new ReceiptType(-1,"test ticket",
				Optional.of(BigDecimal.valueOf(321)));
		lastInsertedReceiptID=dao.createReceipt(r);
		if(lastInsertedReceiptID==-1)
		{
			Connection con=DBConfig.getInstance().createConnection();
			Statement delete=con.createStatement();
			delete.executeUpdate("DELETE FROM types WHERE name LIKE 'test ticket' AND `limit`=321");
			con.close();
			lastInsertedReceiptID=dao.createReceipt(r);
		}			
		assertNotEquals(-1,lastInsertedReceiptID,"Row already exists");
		assertNotEquals(0,lastInsertedReceiptID,"Insert faliure");
	}
	
	@Test
	@Order(2)
	public void updateDataOnlyNameTest() throws SQLException
	{
		Optional<String> newName=Optional.of("test new name");
		Optional<BigDecimal> unchanged=Optional.empty();
		assertTrue(dao.updateReceipt(lastInsertedReceiptID,newName,unchanged),
				"Row to update not found.");
	}
	
	@Test
	@Order(3)
	public void updateDataOnlyLimitTest() throws SQLException
	{
		Optional<String> unchanged=Optional.empty();
		Optional<BigDecimal> newLimit=Optional.of(BigDecimal.valueOf(123));
		assertTrue(dao.updateReceipt(lastInsertedReceiptID,unchanged,newLimit),
				"Row to update not found");
	}
	
	@Test
	@Order(4)
	public void updateAllDataTest() throws SQLException
	{
		Optional<String> newName=Optional.of("test both name");
		Optional<BigDecimal> newLimit=Optional.of(BigDecimal.valueOf(1234));
		assertTrue(dao.updateReceipt(lastInsertedReceiptID,newName,newLimit),
				"Row to update not found");
	}
	
	@Test
	public void testAllReceiptsSelection() throws SQLException
	{
		assertNotEquals(0,dao.getAllReceipts().size());
	}
	
	@Test
	public void testAllReceiptsMinimalSelection() throws SQLException
	{
		assertNotEquals(0,dao.getAllReceiptsMinimal().size());
	}
	
	@Test
	@AfterAll
	public static void deleteReceiptTest() throws SQLException
	{
		assertTrue(dao.deleteReceipt(lastInsertedReceiptID),
				"Row to delete not found");
		dao.close();
	}
}
