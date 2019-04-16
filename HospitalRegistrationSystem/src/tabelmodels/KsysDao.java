package tabelmodels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class KsysDao extends BaseMysqlDao implements PyzsQueryableDao {

	static final String table = "t_ksys";
	static final int max_ysbh = 999999;

	public KsysDao(String url, String user, String passwd) throws SQLException {
		super(url, user, passwd);
	}
	
	/**
	 * Get biggest <code>ysbh</code> in table.
	 * @return Biggest <code>ysbh</code>, or <code>-1</code> on empty table.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	int getLastYsbh() throws IllegalStateException, SQLException {
		this.executeQuery("SELECT `ysbh` from `" + KsysDao.table + "`" +
				"ORDER BY `ysbh` DESC " + "LIMIT 1");
		if (this.last_rset.next()) {
			return this.last_rset.getInt(1);
		}
		return -1;
	}
	
	/**
	 * Check if record with same <code>ysmc</code> already in table.
	 * @param ysmc
	 * @return
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public boolean hasYsmc(String ysmc) throws IllegalStateException, SQLException {
		this.executeQuery("SELECT `ysbh` from " + String.format("`%s` ", KsysDao.table) +
				"WHERE `ysmc` = " + String.format("'%s' ", ysmc) + "LIMIT 1");
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
	 * @param ysmc 医生名称
	 * @param pyzs 医生名称的拼音字首
	 * @param ksxx 科室信息表数据表管理对象
	 * @param ksbh 所属科室编号
	 * @param dlkl_raw 登陆口令
	 * @param sfzj 是否专家
	 * @return Whether this insertion is successful. May return <code>false</code> on
	 *         <ul>
	 *           <li><code>"no available ysbh"</code> - too many records in table, i.e. <code>ysbh</code> exceeding
	 *                                                  designed maxima.</li>
	 *           <li><code>"overlapping ysmc"</code> - record with same <code>ysmc</code> already in table.</li>
	 *           <li><code>"ksbh not exist"</code> - no existing <code>ksbh</code>.</li>
	 *           <li><code>"pass"</code> - all good.</li>
	 *         </ul>
	 *         <b>Always check before modifying database!</b>
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public String insertRecord(String ysmc, String pyzs, KsxxDao ksxx, int ksbh, String dlkl_raw, boolean sfzj) throws
			IllegalStateException, SQLException {
		if (this.hasYsmc(ysmc)) {
			return "overlapping ysmc";
		}
		if (!ksxx.hasKsbh(ksbh)) {
			return "ksbh not exist";
		}
		int ysbh = this.getLastYsbh() + 1;
		if (ysbh == 0) {
			ysbh = 1;
		} else if (ysbh > KsysDao.max_ysbh) {
			return "no available ysbh";
		}
		this.execute("INSERT INTO " + String.format("`%s` ", KsysDao.table) +
				"(`ysbh`, `ksbh`, `ysmc`, `pyzs`, `dlkl`, `sfzj`) values (" +
				String.format("'%06d', ", ysbh) + String.format("'%06d', ", ksbh) + String.format("'%s', ", ysmc) +
				String.format("'%s', ", pyzs) + String.format("'%s', ", KsysDao.salt(dlkl_raw)) +
				(sfzj ? "TRUE" : "FALSE") +
				")");
		return "pass";
	}
	
	String getDlklFromQuery() throws SQLException {
		return this.last_rset.getString("dlkl");
	}
	
	String getYsbhFromQuery() throws SQLException {
		return this.last_rset.getString("ysbh");
	}
	
	/**
	 * Verify login.
	 * @param ysmc
	 * @param dlkl_raw
	 * @return A return string defined as follow
	 *         <ul>
	 *           <li><code>"user not found"</code> - doctor with name <code>ysmc</code> cannot be found.</li>
	 *           <li><code>"wrong password"</code> - no <code>ysmc</code> login via this password.</li>
	 *           <li><code>"pass"</code> - all okay.</li>
	 *         </ul>
	 * @throws SQLException 
	 * @throws IllegalStateException 
	 */
	public String verifyLogin(String ysmc, String dlkl_raw) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(
				"SELECT UTC_TIMESTAMP() AS `timestamp`, `ysbh`, `dlkl` FROM `t_ksys` WHERE " +
				"`ysmc` = ?");
		ps.setString(1, ysmc);
		ResultSet rset = ps.executeQuery();
		if (!rset.first()) {
			return "user not found";
		}
		do {
			if (rset.getString("dlkl").equals(KsysDao.salt(dlkl_raw))) {
				ps = this.getConnection().prepareStatement(
						"UPDATE `t_ksys` SET `dlrq` = ? WHERE `ysbh` = ?");
				ps.setTimestamp(1, rset.getTimestamp("timestamp"));
				ps.setString(2, rset.getString("ysbh"));
				ps.executeUpdate();
				return "pass";
			}
		} while (rset.next());
		return "wrong password";
	}
	
	public String getYsmcFromQuery() throws SQLException {
		return this.last_rset.getString("ysmc");
	}
	
	/**
	 * Query data table by leading <code>pyzs</code> field characters.
	 * @param pyzs_partial 拼音字首 leading characters.
	 * @param Limit Row count limit, ignored if less than or equal to 0.
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	public void queryAllByPyzsStartWith(String pyzs_partial, int limit) throws IllegalStateException, SQLException {
		this.executeQuery("SELECT * FROM " + String.format("`%s` ", KsysDao.table) +
				"WHERE " + String.format("`pyzs` LIKE '%s%%'", pyzs_partial) +
				(limit > 0 ? String.format(" LIMIT %d", limit) : ""));
	}
	
	public LinkedList<String> queryYsmcByPyzsStartWith(String pyzs, String ksmc, boolean sfzj, int limit)
			throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(
				"SELECT `t_ksys`.`ysmc` FROM `t_ksys`, `t_ksxx` WHERE " +
				"(`t_ksxx`.`ksbh` = `t_ksys`.`ksbh`) AND " +
				"(`t_ksxx`.`ksmc` = ?) AND " +
				"(`t_ksys`.`sfzj` = ?) AND " +
				"(`t_ksys`.`pyzs` LIKE ?)" +
				(limit > 0 ? String.format(" LIMIT %d", limit) : ""));
		ps.setString(1, ksmc);
		ps.setBoolean(2, sfzj);
		ps.setString(3, pyzs + "%");
		ResultSet rset = ps.executeQuery();
		rset.beforeFirst();
		LinkedList<String> retlist = new LinkedList<String>();
		while (rset.next()) {
			retlist.add(rset.getString(1));
		}
		return retlist;
	}
	
	public boolean hasYsbh(String ysbh) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(String.format(
				"SELECT `ysbh` FROM `%s` WHERE `ysbh` = ? LIMIT 1", KsysDao.table));
		ps.setString(1, ysbh);
		ResultSet rset = ps.executeQuery();
		return rset.first() && !rset.next();
	}

	@Override
	public String getNameFromQuery() throws SQLException {
		return this.getYsmcFromQuery();
	}
	
	public String getYsbhByYsmc(String ysmc) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(String.format(
				"SELECT `ysbh` FROM `%s` WHERE `ysmc` = ?", KsysDao.table));
		ps.setString(1, ysmc);
		ResultSet rset = ps.executeQuery();
		if (!rset.first() || rset.next()) {
			return null;
		}
		rset.first();
		return rset.getString("ysbh");
	}
	
}












