package tabelmodels;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BrxxDaoTest {

	static BrxxDao brxx = null;
	
	@BeforeAll
	static void buildTestEnv() throws SQLException {
		brxx = new BrxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
	}
	
	@AfterEach
	void afterTest() {
		System.out.println("====================\n");
	}
	
	@Test
	void testGetLastBrxx() throws IllegalStateException, SQLException {
		System.out.println(brxx.getLastBrbh());
	}
	
	@Test
	void testInsertRecord() throws IllegalStateException, SQLException {
		// `false` if already in table
		System.out.println("brxx.insertRecord() returned " + brxx.insertRecord("������̫��", "oraoraora", 1000.17));
		System.out.println("brxx.insertRecord() returned " + brxx.insertRecord("������Ӱ", "mudamudamuda", 1000.97));
	}
	
	@Test
	void testVerifyLogin() throws IllegalStateException, SQLException {
		assertEquals("pass", brxx.verifyLogin("������Ӱ", "mudamudamuda"));
		assertEquals("wrong password", brxx.verifyLogin("������̫��", "platiumstart"));
		assertEquals("user not found", brxx.verifyLogin("�ϰ�", "konodioda"));
	}
	
	@Test
	void testGetDateTime() throws IllegalStateException, SQLException {
		System.out.println(brxx.getDateTime());
	}
	
	@AfterAll
	static void destroyTestEnv() throws SQLException {
		brxx.closeAll();
		brxx = null;
	}

}
