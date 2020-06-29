package il.co.ilrd.gatewayserver;

import java.sql.SQLException;

public interface DatabaseManagementInterface {
	public void createTable(String sqlCommand) throws ClassNotFoundException, SQLException;
	public void createRow(String sqlCommand) throws  ClassNotFoundException, SQLException;
	public void createIOTEvent(String rawData) throws  ClassNotFoundException, SQLException;
}