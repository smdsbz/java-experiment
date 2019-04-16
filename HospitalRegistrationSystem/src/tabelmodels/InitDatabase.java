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
					"`ksbh`		CHAR(6)		NOT NULL	COMMENT '科室编号，数字', " +
					"`ksmc`		CHAR(10)	NOT NULL	COMMENT '科室名称', " +
					"`pyzs`		CHAR(8)		NOT NULL	COMMENT '科室名称的拼音字首', " +
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
					"`brbh`		CHAR(6)			NOT NULL	COMMENT '病人编号，数字', " +
					"`brmc`		CHAR(10)		NOT NULL	COMMENT '病人名称', " +
					"`dlkl`		CHAR(8)			NOT NULL	COMMENT '登陆口令', " +
					"`ycje`		DECIMAL(10,2)	NOT NULL	COMMENT '病人预存金额', " +
					"`dlrq`		DATETIME					COMMENT '最后一次登陆日期及时间', " +
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
					"`ysbh`		CHAR(6)		NOT NULL	COMMENT '医生编号，数字，第1索引', " +
					"`ksbh`		CHAR(6)		NOT NULL	COMMENT '所属科室编号，数字，第2索引', " +
					"`ysmc`		CHAR(10)	NOT NULL	COMMENT '医生名称', " +
					"`pyzs`		CHAR(4)		NOT NULL	COMMENT '医生名称的拼音字首', " +
					"`dlkl`		CHAR(8)		NOT NULL	COMMENT '登陆口令', " +
					"`sfzj`		BOOL		NOT NULL	COMMENT '是否专家', " +
					"`dlrq`		DATETIME				COMMENT '最后一次登陆日期及时间', " +
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
					"`hzbh`		CHAR(6)			NOT NULL	COMMENT '号种编号，数字，第1索引', " +
					"`hzmc`		CHAR(12)		NOT NULL	COMMENT '号种名称', " +
					"`pyzs`		CHAR(8)			NOT NULL	COMMENT '号种名称的拼音字首', " +
					"`ksbh`		CHAR(6)			NOT NULL	COMMENT '号种所属科室，第2索引', " +
					"`sfzj`		BOOL			NOT NULL	COMMENT '是否专家号', " +
					"`ghrs`		INT				NOT NULL	COMMENT '每日限定挂号人数', " +
					"`ghfy`		DECIMAL(8,2)	NOT NULL	COMMENT '挂号费', " +
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
					"`ghbh`		CHAR(6)			NOT NULL	COMMENT '挂号的顺序编号，数字', " +
					"`hzbh`		CHAR(6)			NOT NULL	COMMENT '号种编号', " +
					"`ysbh`		CHAR(6)			NOT NULL	COMMENT '医生编号', " +
					"`brbh`		CHAR(6)			NOT NULL	COMMENT '病人编号', " +
					"`ghrc`		INT				NOT NULL	COMMENT '该号种的挂号人次', " +
					"`thbz`		BOOL			NOT NULL	COMMENT '退号标志=true 为已退号码', " +
					"`ghfy`		DECIMAL(8,2)	NOT NULL	COMMENT '病人的实际挂号费用', " +
					"`rqsj`		DATETIME		NOT NULL	COMMENT '挂号的日期时间', " +
					"`kbsj`		DATETIME					COMMENT '看病时间：“空”未看。已看病则不能退号', " +
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






















