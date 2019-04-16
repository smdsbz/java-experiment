package tabelmodels;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


public class BaseMysqlDao {
	
//	String driver = null;
	String url = null;			// url to database
//	String table = null;		// name of data table
//	String username = null;		// user name
//	String passwd = null;		// user password
	Connection conn = null;
	Statement stmt = null;
	protected ResultSet last_rset = null;

	/**
	 * A constructor that dose literally <b>nothing</b>.
	 */
	public BaseMysqlDao() {
		return;
	}
	
	/**
	 * Short-hand for creating an instance and call <code>getConnection()</code>
	 * @param url
	 * @param user
	 * @param passwd
	 * @throws SQLException 
	 * @see BaseMysqlDao#getConnection(String, String, String)
	 */
	public BaseMysqlDao(String url, String user, String passwd) throws SQLException {
		this.getConnection(url, user, passwd);
	}

	/**
	 * Get reference to the existing <code>Connection</code> object.
	 * @return
	 * @throws IllegalStateException
	 */
	protected Connection getConnection() throws IllegalStateException {
		if (this.conn == null) {
			throw new IllegalStateException();
		}
		return this.conn;
	}
	
	protected PreparedStatement getPreparedStatement(String sql) throws IllegalStateException, SQLException {
		return this.getConnection().prepareStatement(sql);
	}
	
	/**
	 * Establish a new <code>Connection</code> and return it.
	 * @param username
	 * @param passwd
	 * @param url
	 * @return
	 * @throws IllegalStateException on existing established connection.
	 * @throws SQLException
	 */
	protected Connection getConnection(String url, String user, String passwd) throws IllegalStateException, SQLException {
		// NOTE: Let's just assume all input parameters are not null!
		if (this.conn != null && !this.conn.isClosed()) {
			throw new IllegalStateException();
		}
		if (url != null) {
			this.url = url;
		} else {
			url = this.url;
		}
		// NOTE: Setting <code>useSSL</code> to <code>false</code> hides <code>javax.net.ssl.SSLException: closing inbound before receiving peer's close_notify</code>
		this.conn = DriverManager.getConnection("jdbc:" + url + "?serverTimezone=UTC&useSSL=false", user, passwd);
		this.stmt = conn.createStatement();
		return this.conn;
	}

	/**
	 * Execute a query SQL command, and set cursor to one-before-first.
	 * @param sql
	 * @return The result set internally maintained by {@link BaseMysqlDao}.
	 * @throws IllegalStateException on no existing connection.
	 * @throws SQLException
	 */
	protected ResultSet executeQuery(String sql) throws IllegalStateException, SQLException {
		if (this.conn == null || this.stmt == null) {
			throw new IllegalStateException();
		}
		this.last_rset = this.stmt.executeQuery(sql);
		this.last_rset.beforeFirst();
		return this.last_rset;
	}
	
	/**
	 * @param ps
	 * @return
	 * @throws SQLException
	 * @see {@link BaseMysqlDao#executeQuery(String)}
	 */
	protected ResultSet executeQuery(PreparedStatement ps) throws SQLException {
		if (this.conn == null) {
			throw new IllegalStateException();
		}
		this.last_rset = ps.executeQuery();
		this.last_rset.beforeFirst();
		return this.last_rset;
	}

	/**
	 * Close connection and release resources.
	 * @throws SQLException
	 */
	protected void closeAll() throws SQLException {
		if (this.last_rset != null) {
			this.last_rset.close();
			this.last_rset = null;
		}
		if (this.stmt != null) {
			this.stmt.close();
			this.stmt = null;
		}
		if (this.conn != null) {
			this.conn.close();
			this.conn = null;
		}
	}
	
	protected void finalize() throws SQLException {
		this.closeAll();
	}

	/**
	 * Execute a SQL command.
	 * @param sql
	 * @return see {@link java.sql.Statement#execute(String)}
	 * @throws IllegalStateException on no existing connection.
	 * @throws SQLException
	 */
	protected boolean execute(String sql) throws IllegalStateException, SQLException {
		if (this.conn == null || this.stmt == null) {
			throw new IllegalStateException();
		}
		return this.stmt.execute(sql);
	}
	
	/**
	 * @param ps
	 * @return
	 * @throws SQLException
	 * @see {@link BaseMysqlDao#execute(String)}
	 */
	protected boolean execute(PreparedStatement ps) throws SQLException {
		if (this.conn == null) {
			throw new IllegalStateException();
		}
		return ps.execute();
	}

	/**
	 * Go to next row of last query result set.
	 * @return see {@link java.sql.ResultSet#next()}.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public boolean nextRowInQuery() throws IllegalStateException, SQLException {
		if (this.last_rset == null) {
			throw new IllegalStateException();
		}
		return this.last_rset.next();
	}

	/**
	 * Get UTC datetime (timestamp) from database.
	 * @return UTC timestamp.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public Timestamp getTimestamp() throws IllegalStateException, SQLException {
		if (this.conn == null) {
			throw new IllegalStateException();
		}
		this.executeQuery("SELECT UTC_TIMESTAMP()");
		this.nextRowInQuery();
		return this.last_rset.getTimestamp(1);
	}

	/**
	 * @return SQL-format datetime, in UTC.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public String getDateTime() throws IllegalStateException, SQLException {
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(this.getTimestamp());
	}
	
	/**
	 * @return SQL-format datetime, in UTC.
	 * @throws IllegalStateException
	 * @throws SQLException
	 */
	public String getDateTime(Timestamp ts) {
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(ts);
	}

}






