package tabelmodels;

//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

//import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInfo;
//import org.junit.rules.TestName;

class KsxxDaoTest {

//	@Rule public TestName test_name = new TestName();
	static KsxxDao ksxx = null;

	@BeforeAll
	static void buildTestEnv() throws SQLException {
		ksxx = new KsxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
	}
	
	@AfterAll
	static void destoryTestEnv() throws SQLException {
		ksxx.closeAll();
	}
	
	@BeforeEach
	void beforeEach() throws NoSuchMethodException, SecurityException {
//		System.out.println("======== " + "" + " ========");
	}
	
	@AfterEach
	void afterEach() {
		System.out.println("================\n");
	}
	
	@Test
	void testHasKsbh() throws SQLException {
		System.out.println("======== testHasKsbh() ========");
		if (ksxx.hasKsbh(1)) {
			System.out.println("`t_ksxx` already has record with `ksbh` = '000001'!");
		} else {
			fail("KsxxDao.hasKsbh() failed!");
		}
		if (ksxx.hasKsbh("100000")) {
			fail("Test data table `t_ksxx` should not contain record with `ksbh` = '100000'!");
		} else {
			System.out.println("`t_ksxx` does not have record with `ksbh` = '100000'!");
		}
	}
	
	@Test
	void testInsertRecord() throws IllegalStateException, SQLException {
		System.out.println("======== testInsertRecord() ========");
		System.out.println("KsxxDao.insertRecord() returning " + ksxx.insertRecord("德国骨科", "dggk"));
		System.out.println("KsxxDao.insertRecord() returning " + ksxx.insertRecord("魔法科", "mfk"));
		System.out.println("KsxxDao.insertRecord() returning " + ksxx.insertRecord("法科", "fk"));
	}
	
	@Test
	void testQueryByPyzsStartWith() throws IllegalStateException, IllegalArgumentException, SQLException {
		String pyzs_test = "d";
		System.out.println("======== testQueryByPyzsStartWith() - start with '" + pyzs_test + "' ========");
		ksxx.queryAllByPyzsStartWith(pyzs_test, 50);
		while (ksxx.nextRowInQuery()) {
			System.out.println(String.format("%6d   %10s   %8s", ksxx.getKsbhFromQuery(), ksxx.getKsmcFromQuery(),
					ksxx.getPyzsFromQuery()));
		}
	}

}
