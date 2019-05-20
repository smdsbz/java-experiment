package tabelmodels;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CreateTestSet {
	
	static KsxxDao ksxx = null;	// 科室信息
	static KsysDao ksys = null;	// 科室医生
	static BrxxDao brxx = null;	// 病人信息
	static HzxxDao hzxx = null;	// 号种信息
	static GhxxDao ghxx = null;	// 挂号信息
	
	
	private static void createAllConnections() throws SQLException {
		ksxx = new KsxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		ksys = new KsysDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		brxx = new BrxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		hzxx = new HzxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
		ghxx = new GhxxDao("mysql://localhost/javaexp_hospital", "localroot", "123456");
	}
	
	private static void closeAllConnections() throws SQLException {
		ksxx.closeAll();
		ksys.closeAll();
		brxx.closeAll();
		hzxx.closeAll();
		ghxx.closeAll();
	}
	
	private static void clearAllData() throws IllegalStateException, SQLException {
		List<String> tables = Arrays.asList("t_ghxx", "t_hzxx", "t_brxx", "t_ksys", "t_ksxx");	// reversed order
		for (String table : tables) {
			ksxx.execute("DELETE FROM " + table);
		}
	}
	
	private static void buildKsxx() throws IllegalStateException, SQLException {
		ksxx.insertRecord("感染科", "grk");
		ksxx.insertRecord("内一科", "nyk");
		ksxx.insertRecord("骨科", "gk");
	}
	
	private static void buildKsys() throws IllegalStateException, SQLException {
		ksys.insertRecord("李时珍", "lsz", ksxx, ksxx.getKsbhFromKsmc("感染科"), "wslsz", true);
		ksys.insertRecord("扁鹊", "bq", ksxx, ksxx.getKsbhFromKsmc("感染科"), "wsbq", false);
		ksys.insertRecord("华佗", "ht", ksxx, ksxx.getKsbhFromKsmc("骨科"), "wsht", true);
	}
	
	private static void buildBrxx() throws IllegalStateException, SQLException {
		brxx.insertRecord("章子怡", "wszzy", 100.1);
		brxx.insertRecord("范冰冰", "wsfbb", 100.2);
		brxx.insertRecord("刘德华", "ldh", 1.2);
	}
	
	private static void buildHzxx() throws IllegalStateException, SQLException {
		hzxx.insertRecord("胃部感染", "wbgr", ksxx.getKsbhByKsmc("感染科"), true, 2, 4.3);
		hzxx.insertRecord("外伤感染", "wsgr", ksxx.getKsbhByKsmc("感染科"), false, 100, 1.3);
		hzxx.insertRecord("消化", "xh", ksxx.getKsbhByKsmc("内一科"), false, 50, 2.3);
		hzxx.insertRecord("骨折", "gz", ksxx.getKsbhByKsmc("骨科"), true, 50, 5.5);
	}
	
	private static void buildAllTestData() throws IllegalStateException, SQLException {
		buildKsxx();
		buildKsys();
		buildBrxx();
		buildHzxx();
	}

	public static void main(String[] args) throws IllegalStateException, SQLException {
		createAllConnections();

		clearAllData();
		buildAllTestData();

		closeAllConnections();
		System.out.println("All done!");
	}

}




















