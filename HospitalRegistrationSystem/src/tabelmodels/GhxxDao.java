package tabelmodels;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import doctor.BrlbItem;
import doctor.SrlbItem;

public class GhxxDao extends BaseMysqlDao {
	
	static final String table = "t_ghxx";
	static final int max_ghbh = 999999;

	public GhxxDao() {
		return;
	}

	public GhxxDao(String url, String user, String passwd) throws SQLException {
		super(url, user, passwd);
		return;
	}
	
	/**
	 * Get biggest <code>ghbh</code> in table.
	 * @return Biggest <code>ghbh</code>, or <code>-1</code> on empty table.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	int getLastGhbh() throws IllegalStateException, SQLException {
		this.executeQuery("SELECT `ghbh` FROM " + String.format("`%s` ", GhxxDao.table) +
				"ORDER BY `ghbh` DESC " + "LIMIT 1");
		if (this.nextRowInQuery()) {
			return Integer.parseInt(this.last_rset.getString(1));
		}
		return -1;
	}
	
	/**
	 * Get current <code>ghrc</code> for <code>hzbh</code>.
	 * @param hzbh
	 * @return Current <code>ghrc</code> for <code>hzbh</code>, <code>0</code> if none.
	 * @throws SQLException 
	 * @throws IllegalStateException 
	 */
	int getCurrentGhrc(String hzbh) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(String.format(
				"SELECT MAX(`ghrc`) AS `latest_ghrc` FROM `%s` WHERE `rqsj` >= CURDATE() AND `hzbh` = ?",
				GhxxDao.table));
		ps.setString(1, hzbh);
		ResultSet rset = ps.executeQuery();
		rset.beforeFirst();
		if (!rset.next()) {
			rset.close();
			return 0;
		}
		int ghrc = rset.getInt("latest_ghrc");
		rset.close();
		return ghrc;
	}
	
	/**
	 * Insert a new record to data table.
	 * @param hzxx 号种信息表管理对象
	 * @param hzbh 号种编号
	 * @param ksys 科室医生信息表管理对象
	 * @param ysbh 医生编号
	 * @param brxx 病人信息表管理对象
	 * @param brbh 病人编号
	 * @return <ul>
	 *           <li><code>"xxxx not found"</code> - corresponding ID field not found.</li>
	 *           <li><code>"no available ghbh"</code> - too many records in table, i.e. <code>ghbh</code> exceeding
	 *                                                  designed maxima.</li>
	 *           <li><code>"reached maximum ghrc"</code> - <code>ghrc</code> used up.</li>
	 *           <li><code>"insufficient balance"</code> - insufficient <code>ycje</code>.</li>
	 *           <li><code>"pass"</code> - all good.</li>
	 *         </ul>
	 * @throws SQLException 
	 * @throws IllegalStateException 
	 */
	public String insertRecord(HzxxDao hzxx, String hzbh, KsysDao ksys, String ysbh, BrxxDao brxx, String brbh)
			throws IllegalStateException, SQLException {
		if (!ksys.hasYsbh(ysbh)) {
			return "ysbh not found";
		}
		if (!brxx.hasBrbh(brbh)) {
			return "brbh not found";
		}
		int ghbh = this.getLastGhbh() + 1;
		if (ghbh == 0) {
			ghbh = 1;
		} else if (ghbh > GhxxDao.max_ghbh) {
			return "no available ghbh";
		}
		int ghrc = this.getCurrentGhrc(hzbh) + 1;
		if (ghrc > hzxx.getGhrs(hzbh)) {
			return "reached maximum ghrc";
		}
		Timestamp ts = null;
		BigDecimal ghfy = null;
		BigDecimal ycje = null;
		PreparedStatement ps = this.getConnection().prepareStatement(
				"SELECT UTC_TIMESTAMP() AS `timestamp`, `t_brxx`.`ycje` AS `ycje`, `t_hzxx`.`ghfy` AS `ghfy` " +
				"FROM `t_brxx`, `t_hzxx` WHERE " +
				"(`t_brxx`.`brbh` = ?) AND " +
				"(`t_hzxx`.`hzbh` = ?)");
		ps.setString(1, brbh);
		ps.setString(2, hzbh);
		ResultSet rset = ps.executeQuery();
		rset.first();
		ts = rset.getTimestamp("timestamp");
		ghfy = rset.getBigDecimal("ghfy");
		ycje = rset.getBigDecimal("ycje");
		BigDecimal ycje_new = ycje.subtract(ghfy);
		// cost balance from t_brxx
		if (ycje_new.doubleValue() < 0.0) {
			return "insufficient balance";
		}
		ps = this.getConnection().prepareStatement(
				"UPDATE `t_brxx` SET `ycje` = ? WHERE `brbh` = ?");
		ps.setBigDecimal(1, ycje_new);
		ps.setString(2, brbh);
		ps.executeUpdate();
		// insert to t_ghxx
		ps = this.getConnection().prepareStatement(
				"INSERT INTO `t_ghxx` (`ghbh`, `hzbh`, `ysbh`, `brbh`, `ghrc`, `thbz`, `ghfy`, `rqsj`) " +
				"VALUES (?,?,?,?,?,?,?,?)");
		ps.setString(1, String.format("%06d", ghbh));
		ps.setString(2, hzbh);
		ps.setString(3, ysbh);
		ps.setString(4, brbh);
		ps.setInt(5, ghrc);
		ps.setBoolean(6, false);
		ps.setBigDecimal(7, ghfy);
		ps.setTimestamp(8, ts);
		ps.execute();
		return "pass";
	}
	
	public LinkedList<BrlbItem> queryBrlbItemByYsbh(String ysbh) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(
				"SELECT t_ghxx.ghbh AS ghbh, t_brxx.brmc AS brmc, t_ghxx.rqsj AS ghrq, t_hzxx.sfzj AS hzlb " +
				"FROM t_ghxx, t_brxx, t_hzxx " +
				"WHERE t_ghxx.brbh = t_brxx.brbh " +
				"    AND t_ghxx.hzbh = t_hzxx.hzbh " +
				"    AND t_ghxx.ysbh = ? " +
				"    AND t_ghxx.kbsj IS NULL " +
				"ORDER BY t_ghxx.rqsj DESC");
		ps.setString(1, ysbh);
		ResultSet rset = ps.executeQuery();
		rset.beforeFirst();
		LinkedList<BrlbItem> retlist = new LinkedList<BrlbItem>();
		while (rset.next()) {
			retlist.add(new BrlbItem(
					rset.getString("ghbh"),
					rset.getString("brmc"),
					(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(rset.getTimestamp("ghrq")),
					(rset.getBoolean("hzlb") ? "专家号" : "非专家号")
			));
		}
		return retlist;
	}
	
	/**
	 * @param ghbh
	 * @return If only 1 row is updated (see {@link PreparedStatement#executeUpdate()}), for <code>ghbh</code> not
	 *     found, no rows are updated.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public boolean setHandledByGhbh(String ghbh) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(
				"UPDATE t_ghxx SET kbsj = (SELECT UTC_TIMESTAMP()) WHERE ghbh = ?");
		ps.setString(1, ghbh);
		return ps.executeUpdate() == 1;
	}
	
	public LinkedList<SrlbItem> querySrlbItemInRange(Timestamp start, Timestamp end) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(
				"SELECT t_ksxx.ksmc AS ksmc, t_ghxx.ysbh AS ysbh, t_ksys.ysmc AS ysmc, t_ksys.sfzj AS hzlb, " +
				"        COUNT(*) AS ghrc, SUM(t_ghxx.ghfy) AS srhj " +
				"FROM t_ksxx, t_ksys, t_ghxx " +
				"WHERE t_ksxx.ksbh = t_ksys.ysbh " +
				"    AND t_ksys.ysbh = t_ghxx.ysbh " +
				"    AND t_ghxx.rqsj BETWEEN ? AND ? " +
				"GROUP BY t_ghxx.ysbh");
		ps.setTimestamp(1, start);
		ps.setTimestamp(2, end);
		ResultSet rset = ps.executeQuery();
		rset.beforeFirst();
		LinkedList<SrlbItem> retlist = new LinkedList<SrlbItem>();
		while (rset.next()) {
			retlist.add(new SrlbItem(
					rset.getString("ksmc"),
					rset.getString("ysbh"),
					rset.getString("ysmc"),
					(rset.getBoolean("hzlb") ? "专家号" : "非专家号"),
					rset.getString("ghrc"),
					rset.getString("srhj")
			));
		}
		return retlist;
	}

}














