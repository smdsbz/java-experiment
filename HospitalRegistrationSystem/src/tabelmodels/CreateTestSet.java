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
	
	static KsxxDao ksxx = null;	// ������Ϣ
	static KsysDao ksys = null;	// ����ҽ��
	static BrxxDao brxx = null;	// ������Ϣ
	static HzxxDao hzxx = null;	// ������Ϣ
	static GhxxDao ghxx = null;	// �Һ���Ϣ
	
	
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
		ksxx.insertRecord("��Ⱦ��", "grk");
		ksxx.insertRecord("��һ��", "nyk");
		ksxx.insertRecord("�ǿ�", "gk");
	}
	
	private static void buildKsys() throws IllegalStateException, SQLException {
		ksys.insertRecord("��ʱ��", "lsz", ksxx, ksxx.getKsbhFromKsmc("��Ⱦ��"), "wslsz", true);
		ksys.insertRecord("��ȵ", "bq", ksxx, ksxx.getKsbhFromKsmc("��Ⱦ��"), "wsbq", false);
		ksys.insertRecord("��٢", "ht", ksxx, ksxx.getKsbhFromKsmc("�ǿ�"), "wsht", true);
	}
	
	private static void buildBrxx() throws IllegalStateException, SQLException {
		brxx.insertRecord("������", "wszzy", 100.1);
		brxx.insertRecord("������", "wsfbb", 100.2);
		brxx.insertRecord("���»�", "ldh", 1.2);
	}
	
	private static void buildHzxx() throws IllegalStateException, SQLException {
		hzxx.insertRecord("θ����Ⱦ", "wbgr", ksxx.getKsbhByKsmc("��Ⱦ��"), true, 2, 4.3);
		hzxx.insertRecord("���˸�Ⱦ", "wsgr", ksxx.getKsbhByKsmc("��Ⱦ��"), false, 100, 1.3);
		hzxx.insertRecord("����", "xh", ksxx.getKsbhByKsmc("��һ��"), false, 50, 2.3);
		hzxx.insertRecord("����", "gz", ksxx.getKsbhByKsmc("�ǿ�"), true, 50, 5.5);
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




















