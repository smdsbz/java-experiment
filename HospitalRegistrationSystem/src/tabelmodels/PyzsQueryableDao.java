package tabelmodels;

import java.sql.SQLException;

public interface PyzsQueryableDao {
	
	public void queryAllByPyzsStartWith(String pyzs_partial, int limit) throws IllegalStateException, SQLException;
	
	/**
	 * @return
	 * @throws IllegalStateException
	 * @throws SQLException
	 * @see BaseMysqlDao#nextRowInQuery()
	 */
	public boolean nextRowInQuery() throws IllegalStateException, SQLException;
	public String getNameFromQuery() throws SQLException;
	
}
