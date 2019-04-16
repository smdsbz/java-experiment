package tabelmodels;

//import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KsysDaoTest {

	static KsxxDao ksxx = null;
	static KsysDao ksys = null;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ksxx = new KsxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		ksys = new KsysDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ksys.closeAll();
		ksxx.closeAll();
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
		System.out.println("========================\n");
	}

	@Test
	void testInsertRecord() throws IllegalStateException, SQLException {
		System.out.println("ksys.insertRecord() returning " +
				ksys.insertRecord("������", "zzh", ksxx, ksxx.getKsbhFromKsmc("�¹��ǿ�"), "shixiongdijiulaikanwo", false));
		System.out.println("ksys.insertRecord() returning " +
				ksys.insertRecord("���쿩", "gtl", ksxx, ksxx.getKsbhFromKsmc("ħ����"), "jinwan8dian", true));
	}

}









