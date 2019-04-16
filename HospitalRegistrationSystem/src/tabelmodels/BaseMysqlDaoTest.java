package tabelmodels;

import static org.junit.jupiter.api.Assertions.*;

//import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import org.junit.jupiter.api.Test;


class BaseMysqlDaoTest {

	/**
	 * Prints the row pointed to by the <code>rset</code>'s cursor.
	 * @param rset
	 * @param meta <code>rset</code>'s metadata.
	 * @throws SQLException 
	 */
	static void printResultSetRow(ResultSet rset, ResultSetMetaData meta) throws SQLException {
		int col_num = meta.getColumnCount();
		for (int col = 1; col <= col_num; ++col) {
			System.out.print(col + "\t" + meta.getColumnName(col) + "\t");
			switch (meta.getColumnType(col)) {
				case java.sql.Types.CHAR: {
					System.out.println(rset.getString(col));
					break;
				}
				case java.sql.Types.DECIMAL: {
					System.out.println(rset.getFloat(col));
					break;
				}
				case java.sql.Types.INTEGER: {
					System.out.println(rset.getInt(col));
					break;
				}
			}
		}
		return;
	}
	
	static void printResultSet(ResultSet rset) throws SQLException {
		ResultSetMetaData meta = rset.getMetaData();
		rset.beforeFirst();
		while (rset.next()) {
			System.out.println("==================================");
			printResultSetRow(rset, meta);
		}
		System.out.println("==================================");
		System.out.println();
		return;
	}
	
	@Test
	void baseNormalCase() throws IllegalStateException, SQLException {
//		fail("Unimplemented! BaseMysqlDao has been made abstract!");
		System.out.println("In test sequence baseNormalCase() ...");
		BaseMysqlDao dao = new BaseMysqlDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
//		dao.getConnection("mysql://localhost/javaexp_hospital", "localroot", "123456");
		ResultSet rset = dao.executeQuery("SELECT * from `t_ksxx`");
		printResultSet(rset);
		dao.closeAll();
		System.out.println();
	}
	
	@Test
	void insertAndRemove() throws IllegalStateException, SQLException {
		fail("Unimplemented! Test use database has been changed!");
		System.out.println("In test sequence insertAndRemove() ...");
		BaseMysqlDao dao = new BaseMysqlDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
//		dao.getConnection("mysql://localhost/javaexp_hospital", "localroot", "123456");
		System.out.println("`INSERT INTO` returning " +
				dao.execute("INSERT INTO `t_ksxx` (`ksbh`, `ksmc`, `pyzs`) values ('000003', 'Ê²Ã´¹í', 'smg')"));
		System.out.println("`DELETE FROM` returning " +
				dao.execute("DELETE FROM `t_ksxx` WHERE `ksbh` = '000003'"));
		dao.closeAll();
		System.out.println();
	}

}
