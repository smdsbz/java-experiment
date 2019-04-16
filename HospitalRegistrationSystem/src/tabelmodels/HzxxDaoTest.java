package tabelmodels;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HzxxDaoTest {
	
	static private KsxxDao ksxx = null;
	static private HzxxDao hzxx = null;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ksxx = new KsxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		hzxx = new HzxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		hzxx.closeAll();
	}

	@Test
	void test() throws IllegalStateException, SQLException {
		System.out.println("insertRecord() returning " + hzxx.insertRecord("Õ»∂œ¡À", "tdl", "000001", false, 100, 20.19)); 
	}

}










