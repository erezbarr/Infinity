package il.co.ilrd.databasemanagement;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseManagement{
	private final String databaseName;
	private Connection connection;
	private Statement statement;
	private final String url;
	private final String userName;
	private final String password;
	
	private static final String CREATE_DATABASE = "CREATE DATABASE IF NOT EXISTS ";
	private static final String DROP_TABLE = "DROP TABLE ";
	private static final String SELECT_FROM = "SELECT * FROM ";
	private static final String WHERE = " WHERE ";	
	private static final String EQUAL = " = ";
	private static final String USE = "USE ";
	private static final String DELETE_FROM = "DELETE FROM ";
	private static final String SEPARATOR = "\\|";
	private static final String INSERT_INTO_IOTEVENT = "INSERT INTO IOTEvent (serial_number, description, event_timestamp) VALUES ";
	private static final String OPEN_PARENTHESES = "(";
	private static final String CLOSE_PARENTHESES =	")";
	private static final String COMMA = " , ";
	private static final int SERIAL_NUMBER_INDEX = 0;
	private static final int DESCRIPTION_INDEX = 1;
	private static final int TIMESTAMP_INDEX = 2;
	
	public DatabaseManagement(String url, String userName, String password, String databaseName) throws SQLException, ClassNotFoundException {
		this.databaseName = databaseName;
		this.url = url;
		this.userName = userName;
		this.password = password;
		
		connectToSQL(); 
		statement.executeUpdate(CREATE_DATABASE + databaseName);
		closeResources();
		
	}
	
	public void createTable(String sqlCommand) throws SQLException {
		connectToSQL();
		useDatabase();
		statement.executeUpdate(sqlCommand);
		closeResources();
	}

	public void deleteTable(String tableName) throws SQLException {
		String query = DROP_TABLE + tableName;
		connectToSQL();
		useDatabase();
		statement.executeUpdate(query);
		closeResources();
	}
	
	public void createRow(String sqlCommand) throws SQLException {
		connectToSQL();
		useDatabase();
		statement.executeUpdate(sqlCommand);
		closeResources();
	}
	
	public int createRow(String sqlCommand, String table, String idColumnName) throws SQLException {
		ResultSet resultSet = null;
		int returnResult = 0;
		connectToSQL();
		useDatabase();
		System.out.println("SQL COMMAND" +sqlCommand);
		statement.executeUpdate(sqlCommand);
		resultSet = statement.executeQuery("SELECT MAX(" + idColumnName + ") FROM " + table + ";");
		resultSet.next();
		returnResult = resultSet.getInt(1);
		closeResources();
		return returnResult;
	}
	
	public void createIOTEvent(String rawData) throws SQLException { 
		String[] dataToInsert = rawData.split(SEPARATOR);
		createRow(INSERT_INTO_IOTEVENT + OPEN_PARENTHESES + dataToInsert[SERIAL_NUMBER_INDEX] + COMMA +
				  dataToInsert[DESCRIPTION_INDEX] + COMMA + dataToInsert[TIMESTAMP_INDEX] + CLOSE_PARENTHESES);	
	}
	

	public List<Map<String, Object>> readRow(String tableName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
		ResultSet result = getResultSet(tableName, primaryKeyColumnName, primaryKeyValue);

		List<Map<String, Object>> list = addResultsToList(result);
		closeResources();
		return list;
	} 
	
	private List<Map<String, Object>> addResultsToList(ResultSet sqlResultSet) throws SQLException {
		List<Map<String, Object>> resultList = new ArrayList<>();
		int numOfColumns = sqlResultSet.getMetaData().getColumnCount();
		while (sqlResultSet.next()) {
			ResultSetMetaData data = sqlResultSet.getMetaData();
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < numOfColumns; i++) {
				if(!map.containsKey(data.getColumnName(i + 1))) {
					map.put(data.getColumnName(i + 1), sqlResultSet.getObject(i + 1));
				}
			}
			resultList.add(map);
		}
		return resultList;
	}
	
	
	public Object readField(String tableName, String primaryKeyColumnName, Object primaryKeyValue, int columnIndex) throws SQLException {
		return readRow(tableName, primaryKeyColumnName, primaryKeyValue);
	}
	
	public Object readField(String tableName, String primaryKeyColumnName, Object primaryKeyValue, String columnName) throws SQLException {
		ResultSet result = getResultSet(tableName, primaryKeyColumnName, primaryKeyValue);
		result.next();
		Object objectToRetrun = result.getObject(columnName);
		closeResources();

		return objectToRetrun;
	}
	
	public void updateField(String tableName, String primaryKeyColumnName, Object primaryKeyValue, int columnIndex, Object newValue) throws SQLException {
		ResultSet result = getResultSet(tableName, primaryKeyColumnName, primaryKeyValue);
		result.next();
		result.updateObject(columnIndex, newValue);
		result.updateRow();
		closeResources();
	}
	
	public void updateField(String tableName, String primaryKeyColumnName, Object primaryKeyValue, String columnName, Object newValue) throws SQLException {
		ResultSet result = getResultSet(tableName, primaryKeyColumnName, primaryKeyValue);
		result.next();
		result.updateObject(columnName, newValue);
		result.updateRow();
		closeResources();
	}
	
	public void deleteRow(String tableName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
		connectToSQL();
		String query = DELETE_FROM + tableName + WHERE + primaryKeyColumnName + EQUAL  + primaryKeyValue;
		useDatabase();
		System.out.println("Deleteline: " + query);
		statement.executeUpdate(query);	
		closeResources();
	}
	
	private void useDatabase() throws SQLException {
		String query = USE + databaseName;
		statement.executeQuery(query);
	}
	
	public ResultSet getResultSet(String tableName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
		connectToSQL();
		String query = SELECT_FROM + tableName + WHERE + tableName + "." + primaryKeyColumnName + EQUAL  + "'" + primaryKeyValue + "'";
		useDatabase();
		ResultSet result = statement.executeQuery(query);
		
		return result;
	}
	
//	public ResultSet getJoinedResultSet(String tableName, String primaryKeyColumnName, Object primaryKeyValue, List<String[]> joinList) throws SQLException {
//		connectToSQL();
//		
//		String query = SELECT_FROM + tableName + " ";
//		for (String[] strings : joinList) {
//			query += "left join " + strings[0] + " on " + strings[2] + "." + strings[3] + " = " + strings[0] + "." + strings[1] + " ";   
//		}
//		query += WHERE + tableName + "." + primaryKeyColumnName + EQUAL  + "'" + primaryKeyValue + "'";
//		System.out.println(query);
//		useDatabase();
//		ResultSet result = statement.executeQuery(query);
//		
//		return result;
//	}
//	
	private void connectToSQL() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		connection = DriverManager.getConnection(url, userName, password);
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
	}
	
	private void closeResources() throws SQLException {
		statement.close();
		connection.close();
	}

	public void updateRow(String query) throws SQLException {
			connectToSQL();
			useDatabase();
			System.out.println("SQL UPDATE : " + query);
			statement.executeUpdate(query);
			closeResources();
	}
}