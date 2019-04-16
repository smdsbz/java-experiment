package tabelmodels;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class BrxxDao extends BaseMysqlDao {
	
	static final String table = "t_brxx";	// data table name
	static final int max_brbh = 999999;		// maximum 病人编号
//	String login_user = null;				// 当前会话
	
	public BrxxDao() {
		return;
	}

	public BrxxDao(String url, String user, String passwd) throws SQLException {
		super(url, user, passwd);
		return;
	}
	
	/**
	 * @see KsxxDao#getLastKsbhInt()
	 */
	int getLastBrbh() throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT `brbh` FROM `%s` ORDER BY `brbh` DESC LIMIT 1", BrxxDao.table));
		this.executeQuery(ps);
		if (this.last_rset.next()) {
			return Integer.parseInt(this.last_rset.getString(1));
		}
		return -1;
	}
	
	/**
	 * Check if record with <code>brmc</code> already in table.
	 * @param brmc
	 * @return
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public boolean hasBrmc(String brmc) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT `brbh` FROM `%s` WHERE `brmc` = ?", BrxxDao.table));
		ps.setString(1, brmc);
		this.executeQuery(ps);
		return this.nextRowInQuery();
	}
	
	/**
	 * Add salt to raw passcode
	 * @param dlkl
	 * @return Hashed passcode of length 8.
	 */
	static String salt(String dlkl) {
		return (new StringBuffer(String.format("%08x", dlkl.hashCode()))).reverse().toString().substring(0, 8);
	}
	
	/**
	 * Insert a new record to database.
	 * @param brmc 病人名称
	 * @param dlkl_raw 登陆口令
	 * @param ycje 预存金额
	 * @return Whether this insertion is successful. May return <code>false</code> on
	 *         <ul>
	 *           <li><code>"no available brbh"</code> - too many records in table, i.e. <code>brbh</code> exceeding
	 *                                                  designed maxima.</li>
	 *           <li><code>"overlapping brmc"</code> - record with same <code>brmc</code> already in table.</li>
	 *           <li><code>"pass"</code> - all good.</li>
	 *         </ul>
	 *         <b>Always check before modifying database!</b>
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public String insertRecord(String brmc, String dlkl_raw, double ycje) throws IllegalStateException, SQLException {
		if (this.hasBrmc(brmc)) {
			return "overlapping brmc";
		}
		int brbh = this.getLastBrbh() + 1;
		if (brbh == 0) {
			brbh = 1;
		} else if (brbh > BrxxDao.max_brbh) {
			return "no available brbh";
		}
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"INSERT INTO `%s` (`brbh`, `brmc`, `dlkl`, `ycje`) VALUES (?,?,?,?)", BrxxDao.table));
		ps.setString(1, String.format("%06d", brbh));
		ps.setString(2, brmc);
		ps.setString(3, BrxxDao.salt(dlkl_raw));
		ps.setBigDecimal(4, new BigDecimal(String.format("%.2f", ycje)));
		this.execute(ps);
		return "pass";
	}
	
	/**
	 * Get <code>dlkl</code> at cursor.
	 * @return Salted <code>dlkl</code> stored in database.
	 * @throws SQLException
	 */
	String getDlklFromQuery() throws SQLException {
		return this.last_rset.getString("dlkl");
	}
	
	/**
	 * Get <code>brbh</code> at cursor.
	 * @return
	 * @throws SQLException
	 */
	String getBrbhFromQuery() throws SQLException {
		return this.last_rset.getString("brbh");
	}
	
	/**
	 * Verify login.
	 * @param brmc
	 * @param dlkl_raw
	 * @return A <code>String</code> define as follow
	 *         <ul>
	 *           <li><code>"user not found"</code> - No user named <code>brmc</code>.</li>
	 *           <li><code>"wrong password"</code> - Wrong password.</li>
	 *           <li><code>"pass"</code> - Match.</li>
	 *         </ul>
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public String verifyLogin(String brmc, String dlkl_raw) throws IllegalStateException, SQLException {
		Timestamp timestamp = this.getTimestamp();
		PreparedStatement psselect = this.getPreparedStatement(String.format(
				"SELECT `brbh`, `dlkl` FROM `%s` WHERE `brmc` = ? LIMIT 1", BrxxDao.table));
		psselect.setString(1, brmc);
		this.executeQuery(psselect);
		if (!this.nextRowInQuery()) {
			return "user not found";
		}
		// NOTE though it is guaranteed field <code>brmc</code> is unique, the following block does work with multiple
		//      match.
		do {
			if (this.getDlklFromQuery().equals(BrxxDao.salt(dlkl_raw))) {
				PreparedStatement psupdate = this.getPreparedStatement(String.format(
						"UPDATE `%s` SET `dlrq` = ? WHERE `brbh` = ?", BrxxDao.table));
				psupdate.setTimestamp(1, timestamp);
				psupdate.setString(2, this.getBrbhFromQuery());
				this.execute(psupdate);
				return "pass";
			}
		} while (this.nextRowInQuery());
		return "wrong password";
	}
	
	public boolean hasBrbh(String brbh) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT `brbh` FROM `%s` WHERE `brbh` = ?", BrxxDao.table));
		ps.setString(1, brbh);
		ResultSet rset = ps.executeQuery();
		return rset.first() && !rset.next();	// return true on only 1 match found
	}
	
	/**
	 * @param brmc
	 * @return <code>brbh</code>, else <code>null</code> on not found.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public String getBrbhByBrmc(String brmc) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT `brbh` FROM `%s` WHERE `brmc` = ?", BrxxDao.table));
		ps.setString(1, brmc);
		ResultSet rset = ps.executeQuery();
		if (!rset.first() || rset.next()) {
			return null;
		}
		rset.first();
		return rset.getString("brbh");
	}
	
	/**
	 * @param brbh
	 * @return <code>0.0</code> on <code>brbh</code> not exists.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public double getYcjeByBrbh(String brbh) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(String.format(
				"SELECT `ycje` FROM `%s` WHERE `brbh` = ? LIMIT 1", BrxxDao.table));
		ps.setString(1, brbh);
		ResultSet rset = ps.executeQuery();
		if (!rset.first()) {
			return 0.0;
		}
		return rset.getBigDecimal("ycje").doubleValue();
	}
	
}






