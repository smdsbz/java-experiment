package tabelmodels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class KsxxDao extends BaseMysqlDao implements PyzsQueryableDao {

	static final String table = "t_ksxx";	// data table name
	static final int max_ksbh = 999999;		// maximum ¿ÆÊÒ±àºÅ

//	// used by `query*()` family
//	int last_ksbh = -1;
//	String last_ksmc = null;
//	String last_pyzs = null;
//	ResultSet last_rset = null;		// using superclass's `last_rset`

	/**
	 * 
	 * @param url
	 * @param user
	 * @param passwd
	 * @throws IllegalStateException
	 * @throws SQLException
	 * @see BaseMysqlDao#BaseMysqlDao(String, String, String)
	 */
	public KsxxDao(String url, String user, String passwd) throws SQLException {
		super(url, user, passwd);
	}

	/**
	 * Check if record with <code>ksbh</code> specified already in table.
	 * @param ksbh
	 * @return
	 * @throws IllegalStateException see {@link BaseMysqlDao#executeQuery(String)}.
	 * @throws SQLException
	 */
	public boolean hasKsbh(String ksbh) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT `ksbh` from `%s` WHERE `ksbh` = ? LIMIT 1", KsxxDao.table));
		ps.setString(1, ksbh);
		this.executeQuery(ps);
		return this.last_rset.next();
	}
	
	/**
	 * 
	 * @param ksbh
	 * @return
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 * @see {@link KsxxDao#hasKsbh(String)}
	 */
	public boolean hasKsbh(int ksbh) throws IllegalStateException, IllegalArgumentException, SQLException {
		if (ksbh < 0) {
			throw new IllegalArgumentException();
		}
		return this.hasKsbh(String.format("%06d", ksbh));
	}
	
	/**
	 * Get <code>ksbh</code> with <code>ksmc</code>.
	 * @param ksmc
	 * @return <ul>
	 *           <li><code>-1</code> on not found.</li>
	 *           <li>else, i.e. on found, corresponding <code>ksbh</code>.</li>
	 *         </ul>
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public int getKsbhFromKsmc(String ksmc) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT `ksbh` FROM `%s` WHERE `ksmc` = ? LIMIT 1", KsxxDao.table));
		ps.setString(1, ksmc);
		this.executeQuery(ps);
		if (!this.nextRowInQuery()) {
			return -1;
		}
		return Integer.parseInt(this.last_rset.getString(1));
	}
	
	/**
	 * Check if record with <code>ksmc</code> specified already in table.
	 * @param ksmc
	 * @return
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public boolean hasKsmc(String ksmc) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT `ksbh` from `%s` WHERE `ksmc` = ? LIMIT 1", KsxxDao.table));
		ps.setString(1, ksmc);
		this.executeQuery(ps);
		return this.last_rset.next();
	}
	
	/**
	 * Get biggest <code>ksbh</code> in data table.
	 * @return returns <code>-1</code> on querying on an empty table.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	int getLastKsbhInt() throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT `ksbh` from `%s` ORDER BY `ksbh` DESC LIMIT 1", KsxxDao.table));
		this.executeQuery(ps);
		if (this.last_rset.next()) {
			return Integer.parseInt(this.last_rset.getString(1));
		}
		return -1;
	}
	
	/**
	 * Insert a new record to database.
	 * @param ksmc ¿ÆÊÒÃû³Æ
	 * @param pyzs Æ´Òô×ÖÊ×
	 * @return A return code defined as follow
	 *         <ul>
	 *           <li><code>"no available ksbh"</code> - too many records in table, i.e. <code>ksbh</code> exceeding
	 *                                                  designed maxima.</li>
	 *           <li><code>"overlapping ksmc"</code> - <code>ksmc</code> overlaping with existing one.</li>
	 *           <li><code>"pass"</code> - all good.</li>
	 *         </ul>
	 *         <b>Always check before modifying database!</b>
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public String insertRecord(String ksmc, String pyzs) throws IllegalStateException, SQLException {
		if (this.hasKsmc(ksmc)) {	// if name overlapping
			return "overlapping ksmc";
		}
		int ksbh = this.getLastKsbhInt() + 1;
		if (ksbh == 0) {		// if no record present in table
			ksbh = 1;
		} else if (ksbh > KsxxDao.max_ksbh) {		// if exceeding designed maxima
			return "no available ksbh";
		}
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"INSERT INTO `%s` (`ksbh`, `ksmc`, `pyzs`) VALUES (?,?,?)", KsxxDao.table));
		ps.setString(1, String.format("%06d", ksbh));
		ps.setString(2, ksmc);
		ps.setString(3, pyzs);
		this.execute(ps);
		return "pass";
	}
	
	/**
	 * Explicity releasing resources acquired by querying.
	 * @throws SQLException
	 */
	public void releaseQueryResult() throws SQLException {
		if (this.last_rset == null) {
			return;
		}
		this.last_rset.close();
		this.last_rset = null;
	}
	
	public void queryAllByKsbh(int ksbh) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT * FROM `%s` WHERE `ksbh` = ?", KsxxDao.table));
		ps.setString(1, String.format("%06d", ksbh));
		this.executeQuery(ps);
	}
	
	/**
	 * Query data table by leading <code>pyzs</code> field characters.
	 * @param pyzs_partial Æ´Òô×ÖÊ× leading characters.
	 * @param Limit Row count limit, ignored if less than or equal to 0.
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	public void queryAllByPyzsStartWith(String pyzs_partial, int limit) throws IllegalStateException, SQLException {
		boolean has_limit = limit > 0;
		PreparedStatement ps = this.getPreparedStatement(String.format(
				"SELECT * FROM `%s` WHERE `pyzs` LIKE ? %s", KsxxDao.table, has_limit ? "LIMIT ?" : ""));
		ps.setString(1, pyzs_partial + "%");
		if (has_limit) {
			ps.setInt(2, limit);
		}
		this.executeQuery(ps);
	}
	
	/**
	 * Get <code>ksbh</code> at cursor.
	 * @return
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	public int getKsbhFromQuery() throws NumberFormatException, SQLException {
		return Integer.parseUnsignedInt(this.last_rset.getString("ksbh"));
	}
	
	/**
	 * Get <code>ksmc</code> at cursor.
	 * @return
	 * @throws SQLException
	 */
	public String getKsmcFromQuery() throws SQLException {
		return this.last_rset.getString("ksmc");
	}
	
	/**
	 * Get <code>pyzs</code> at cursor.
	 * @return
	 * @throws SQLException
	 */
	public String getPyzsFromQuery() throws SQLException {
		return this.last_rset.getString("pyzs");
	}

	@Override
	public String getNameFromQuery() throws SQLException {
		return this.getKsmcFromQuery();
	}
	
	public String getKsbhByKsmc(String ksmc) throws IllegalStateException, SQLException {
		PreparedStatement ps = this.getConnection().prepareStatement(String.format(
				"SELECT `ksbh` FROM `%s` WHERE `ksmc` = ?", KsxxDao.table));
		ps.setString(1, ksmc);
		ResultSet rset = ps.executeQuery();
		if (!rset.first()) {
			return null;
		}
		String ksbh = rset.getString("ksbh");
		if (rset.next()) {
			return null;
		}
		return ksbh;
	}

}






