package tabelmodels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class HzxxDao extends BaseMysqlDao implements PyzsQueryableDao {

	static final String table = "t_hzxx";
	static final int max_hzbh = 999999;
	
	public HzxxDao() {
		return;
	}

	public HzxxDao(String url, String user, String passwd) throws SQLException {
		super(url, user, passwd);
		return;
	}
	
	/**
	 * Get biggest <code>hzbh</code> in table.
	 * @return Biggest <code>hzbh</code>, or <code>-1</code> on empty table.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	int getLastHzbh() throws IllegalStateException, SQLException {
		this.executeQuery("SELECT `hzbh` FROM " + String.format("`%s` ", HzxxDao.table) +
				"ORDER BY `hzbh` DESC " + "LIMIT 1");
		if (this.nextRowInQuery()) {
			return Integer.parseInt(this.last_rset.getString(1));
		}
		return -1;
	}
	
	/**
	 * Check if record with same <code>hzmc</code> already in table.
	 * @param hzmc
	 * @return
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public boolean hasHzmc(String hzmc) throws IllegalStateException, SQLException {
		this.executeQuery("SELECT `hzbh` FROM " + String.format("`%s` ", HzxxDao.table) +
				"WHERE `hzmc` = " + String.format("'%s' ", hzmc) +
				"LIMIT 1");
		return this.nextRowInQuery();
	}
	
	/**
	 * Insert a new record to database.
	 * @param hzmc 号种名称
	 * @param pyzs 号种名称的拼音字首
	 * @param ksbh 号种所属科室
	 * @param sfzj 是否专家号
	 * @param ghrs 每日限定的挂号人数
	 * @param ghfy 挂号费
	 * @return <ul>
	 *           <li><code>"no available hzbh"</code> - too many records in table, i.e. <code>hzbh</code> exceeding
	 *                                                  designed maxima.</li>
	 *           <li><code>"overlapping hzmc"</code> - record with same <code>hzmc</code> already in table.</li>
	 *           <li><code>"ksbh not exist"</code> - [Not Used] no existing <code>ksbh</code>.</li>
	 *           <li><code>"pass"</code> - all good.</li>
	 *         </ul>
	 *         <b>Always check before modifying database!</b>
	 * @throws SQLException 
	 * @throws IllegalStateException 
	 */
	public String insertRecord(String hzmc, String pyzs, String ksbh, boolean sfzj, int ghrs,
			double ghfy) throws IllegalStateException, SQLException {
		if (this.hasHzmc(hzmc)) {
			return "overlapping hzmc";
		}
		// NOTE Handled by FOREIGN KEY constraint.
//		if (!ksxx.hasKsbh(ksbh)) {
//			return "ksbh not exist";
//		}
		int hzbh = this.getLastHzbh() + 1;
		if (hzbh == 0) {
			hzbh = 1;
		} else if (hzbh > HzxxDao.max_hzbh) {
			return "no available hzbh";
		}
		this.execute("INSERT INTO " + String.format("`%s` ", HzxxDao.table) +
				"(`hzbh`, `hzmc`, `pyzs`, `ksbh`, `sfzj`, `ghrs`, `ghfy`) VALUES (" +
				String.format("'%06d', ", hzbh) + String.format("'%s', ", hzmc) + String.format("'%s', ", pyzs) +
				String.format("'%s', ", ksbh) + (sfzj ? "TRUE" : "FALSE") + ", " + String.format("%d, ", ghrs) +
				String.format("%.2f", ghfy) +
				")");
		return "pass";
	}
	
	public boolean hasHzbh(int hzbh) throws IllegalStateException, SQLException {
		this.executeQuery("SELECT `hzbh` FROM " + String.format("`%s` ", HzxxDao.table) +
				"WHERE `hzbh` = " + String.format("'%06d' ", hzbh) +
				"LIMIT 1");
		return this.nextRowInQuery();
	}
	
	/**
	 * Get <code>ghrs</code> of <code>hzbh</code>.
	 * @param hzbh
	 * @return <code>ghrs</code> or <code>-1</code> if no existing <code>hzbh</code>.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public int getGhrs(String hzbh) throws IllegalStateException, SQLException {
		this.executeQuery("SELECT `ghrs` FROM " + String.format("`%s` ", HzxxDao.table) +
				"WHERE `hzbh` = " + String.format("'%s' ", hzbh) + "LIMIT 1");
		if (!this.nextRowInQuery()) {
			return -1;
		}
		return this.last_rset.getInt("ghrs");
	}
	
	/**
	 * Get <code>ghfy</code> of <code>hzbh</code>.
	 * @param hzbh
	 * @return A <code>ResultSet</code> with columns <code>timestamp</code> and <code>ghfy</code>, or <code>null</code>
	 *         if no <code>hzbh</code>.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public ResultSet getGhfy(String hzbh) throws IllegalStateException, SQLException {
		this.executeQuery("SELECT UTC_TIMESTAMP() AS `timestamp`, `ghfy` FROM " + String.format("`%s` ", HzxxDao.table) +
				"WHERE `hzbh` = " + String.format("'%06d' ", hzbh) + "LIMIT 1");
		if (!this.nextRowInQuery()) {
			return null;
		}
		return this.last_rset;
	}

	@Override
	public void queryAllByPyzsStartWith(String pyzs_partial, int limit) throws IllegalStateException, SQLException {
		this.executeQuery(String.format(
				"SELECT * FROM `%s` WHERE `pyzs` LIKE '%s%%'%s",
				HzxxDao.table, pyzs_partial, (limit > 0 ? String.format(" LIMIT %d", limit) : "")
		));
	}
	
	public LinkedList<String> queryHzmcByPyzsStartWith(String pyzs, String ksmc, boolean sfzj, int limit)
			throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(
				"SELECT `t_hzxx`.`hzmc` FROM `t_hzxx`, `t_ksxx` WHERE " +
				"(`t_hzxx`.`ksbh` = `t_ksxx`.`ksbh`) AND " +
				"(`t_ksxx`.`ksmc` = ?) AND " +
				"(`t_hzxx`.`sfzj` = ?) AND " +
				"(`t_hzxx`.`pyzs` LIKE ?)");
		ps.setString(1, ksmc);
		ps.setBoolean(2, sfzj);
		ps.setString(3, pyzs + "%");
		ResultSet rset = ps.executeQuery();
		rset.beforeFirst();
		LinkedList<String> hzmc = new LinkedList<String>();
		while (rset.next()) {
			hzmc.add(rset.getString(1));
		}
		return hzmc;
	}
	
	public String getHzmcFromQuery() throws SQLException {
		return this.last_rset.getString("hzmc");
	}

	@Override
	public String getNameFromQuery() throws SQLException {
		return this.getHzmcFromQuery();
	}
	
	/**
	 * @param hzmc
	 * @return <code>null</code> on not found.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public String getHzbhByHzmc(String hzmc) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(String.format(
				"SELECT `hzbh` FROM `%s` WHERE `hzmc` = ?", HzxxDao.table));
		ps.setString(1, hzmc);
		ResultSet rset = ps.executeQuery();
		if (!rset.first() || rset.next()) {
			return null;
		}
		rset.first();
		return rset.getString("hzbh");
	}
	
	public double getGhfyByHzbh(String hzbh) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(String.format(
				"SELECT `ghfy` FROM `%s` WHERE `hzbh` = ? LIMIT 1", HzxxDao.table));
		ps.setString(1, hzbh);
		ResultSet rset = ps.executeQuery();
		if (!rset.first()) {
			return 0.0;
		}
		return rset.getBigDecimal("ghfy").doubleValue();
	}
	
}













