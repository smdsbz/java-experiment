package tabelmodels;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class InitDatabase {

	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rset = null;
	
	public static void main(String[] args) {
		try {
//			Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost/javaexp_hospital?" +
					"serverTimezone=UTC&" +		// otherwise throws SQLException
					"useSSL=true&" +
					"user=localroot&password=123456");
			System.out.println("Connection esablished successfully! (" + conn + ")");
			stmt = conn.createStatement();
			if (checkTKSXX()) {
				System.out.println("Check for table `t_ksxx` passed!");
			} else {
				System.out.println("Check for table `t_ksxx` failed!");
				return;
			}
			if (checkTBRXX()) {
				System.out.println("Check for table `t_brxx` passed!");
			} else {
				System.out.println("Check for table `t_brxx` failed!");
				return;
			}
			if (checkTKSYS()) {
				System.out.println("Check for table `t_ksys` passed!");
			} else {
				System.out.println("Check for table `t_ksys` failed!");
				return;
			}
			if (checkTHZXX()) {
				System.out.println("Check for table `t_hzxx` passed!");
			} else {
				System.out.println("Check for table `t_hzxx` failed!");
				return;
			}
			if (checkTGHXX()) {
				System.out.println("Check for table `t_ghxx` passed!");
			} else {
				System.out.println("Check for table `t_ghxx` failed!");
				return;
			}
			System.out.println("All check passed for `InitDatabase`!");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex) { }
				stmt = null;
			}
			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException ex) { }
				rset = null;
			}
		}
	}
	
	/**
	 * Check if table <code>t_ksxx</code> is in database, if not, create one.
	 * @return Did check go smoothly.
	 */
	public static boolean checkTKSXX() {
		if (conn == null || stmt == null) {
			return false;
		}
		try {
			stmt.execute("CREATE TABLE IF NOT EXISTS `t_ksxx` (" +
					"`ksbh`		CHAR(6)		NOT NULL	COMMENT '���ұ�ţ�����', " +
					"`ksmc`		CHAR(10)	NOT NULL	COMMENT '��������', " +
					"`pyzs`		CHAR(8)		NOT NULL	COMMENT '�������Ƶ�ƴ������', " +
					"PRIMARY KEY (`ksbh`)" +
					")");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @see InitDatabase#checkTKSXX().
	 */
	public static boolean checkTBRXX() {
		if (conn == null || stmt == null) {
			return false;
		}
		try {
			stmt.execute("CREATE TABLE IF NOT EXISTS `t_brxx` (" +
					"`brbh`		CHAR(6)			NOT NULL	COMMENT '���˱�ţ�����', " +
					"`brmc`		CHAR(10)		NOT NULL	COMMENT '��������', " +
					"`dlkl`		CHAR(8)			NOT NULL	COMMENT '��½����', " +
					"`ycje`		DECIMAL(10,2)	NOT NULL	COMMENT '����Ԥ����', " +
					"`dlrq`		DATETIME					COMMENT '���һ�ε�½���ڼ�ʱ��', " +
					"PRIMARY KEY (`brbh`)" +
					")");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @see InitDatabase#checkTKSXX().
	 */
	public static boolean checkTKSYS() {
		if (conn == null || stmt == null) {
			return false;
		}
		try {
			stmt.execute("CREATE TABLE IF NOT EXISTS `t_ksys` (" +
					"`ysbh`		CHAR(6)		NOT NULL	COMMENT 'ҽ����ţ����֣���1����', " +
					"`ksbh`		CHAR(6)		NOT NULL	COMMENT '�������ұ�ţ����֣���2����', " +
					"`ysmc`		CHAR(10)	NOT NULL	COMMENT 'ҽ������', " +
					"`pyzs`		CHAR(4)		NOT NULL	COMMENT 'ҽ�����Ƶ�ƴ������', " +
					"`dlkl`		CHAR(8)		NOT NULL	COMMENT '��½����', " +
					"`sfzj`		BOOL		NOT NULL	COMMENT '�Ƿ�ר��', " +
					"`dlrq`		DATETIME				COMMENT '���һ�ε�½���ڼ�ʱ��', " +
					"PRIMARY KEY (`ysbh`), " +
					"INDEX `ksbh` (`ksbh`), " +
					"FOREIGN KEY (`ksbh`) REFERENCES `t_ksxx`(`ksbh`)" +
					")");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @see InitDatabase#checkTKSXX().
	 */
	public static boolean checkTHZXX() {
		if (conn == null || stmt == null) {
			return false;
		}
		try {
			stmt.execute("CREATE TABLE IF NOT EXISTS `t_hzxx` (" +
					"`hzbh`		CHAR(6)			NOT NULL	COMMENT '���ֱ�ţ����֣���1����', " +
					"`hzmc`		CHAR(12)		NOT NULL	COMMENT '��������', " +
					"`pyzs`		CHAR(8)			NOT NULL	COMMENT '�������Ƶ�ƴ������', " +
					"`ksbh`		CHAR(6)			NOT NULL	COMMENT '�����������ң���2����', " +
					"`sfzj`		BOOL			NOT NULL	COMMENT '�Ƿ�ר�Һ�', " +
					"`ghrs`		INT				NOT NULL	COMMENT 'ÿ���޶��Һ�����', " +
					"`ghfy`		DECIMAL(8,2)	NOT NULL	COMMENT '�Һŷ�', " +
					"PRIMARY KEY (`hzbh`), " +
					"INDEX `ksbh` (`ksbh`), " +
					"FOREIGN KEY (`ksbh`) REFERENCES `t_ksxx`(`ksbh`)" +
					")");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @see InitDatabase#checkTKSXX().
	 */
	public static boolean checkTGHXX() {
		if (conn == null || stmt == null) {
			return false;
		}
		try {
			stmt.execute("CREATE TABLE IF NOT EXISTS `t_ghxx` (" +
					"`ghbh`		CHAR(6)			NOT NULL	COMMENT '�Һŵ�˳���ţ�����', " +
					"`hzbh`		CHAR(6)			NOT NULL	COMMENT '���ֱ��', " +
					"`ysbh`		CHAR(6)			NOT NULL	COMMENT 'ҽ�����', " +
					"`brbh`		CHAR(6)			NOT NULL	COMMENT '���˱��', " +
					"`ghrc`		INT				NOT NULL	COMMENT '�ú��ֵĹҺ��˴�', " +
					"`thbz`		BOOL			NOT NULL	COMMENT '�˺ű�־=true Ϊ���˺���', " +
					"`ghfy`		DECIMAL(8,2)	NOT NULL	COMMENT '���˵�ʵ�ʹҺŷ���', " +
					"`rqsj`		DATETIME		NOT NULL	COMMENT '�Һŵ�����ʱ��', " +
					"`kbsj`		DATETIME					COMMENT '����ʱ�䣺���ա�δ�����ѿ��������˺�', " +
					"PRIMARY KEY (`ghbh`), " +
					"INDEX `hzbh` (`hzbh`), " +
					"INDEX `ysbh` (`ysbh`), " +
					"INDEX `brbh` (`brbh`), " +
					"INDEX `ghrc` (`ghrc`), " +
					"FOREIGN KEY (`hzbh`) REFERENCES `t_hzxx`(`hzbh`), " +
					"FOREIGN KEY (`ysbh`) REFERENCES `t_ksys`(`ysbh`), " +
					"FOREIGN KEY (`brbh`) REFERENCES `t_brxx`(`brbh`)" +
					")");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}






















