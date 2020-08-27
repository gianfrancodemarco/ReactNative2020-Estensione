package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;


public class TableData {

	private DbAccess db;


	public TableData(DbAccess db) {
		this.db = db;
	}

	/**
	 * Recupera tutte le righe memorizzate nel db della tabella {table}
	 *
	 * @throws SQLException
	 * @throws EmptySetException
	 */
	public List<Example> getTransazioni(String table) throws SQLException, EmptySetException {
		LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		TableSchema tSchema = new TableSchema(db, table);


		String query = "select ";

		for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
			Column c = tSchema.getColumn(i);
			if (i > 0)
				query += ",";
			query += c.getColumnName();
		}
		if (tSchema.getNumberOfAttributes() == 0)
			throw new SQLException();
		query += (" FROM " + table);

		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty = true;
		while (rs.next()) {
			empty = false;
			Example currentTuple = new Example();
			for (int i = 0; i < tSchema.getNumberOfAttributes(); i++)
				if (tSchema.getColumn(i).isNumber())
					currentTuple.add(rs.getDouble(i + 1));
				else
					currentTuple.add(rs.getString(i + 1));
			transSet.add(currentTuple);
		}
		rs.close();
		statement.close();
		if (empty) throw new EmptySetException();


		return transSet;

	}

	/**
	 * Recupera tutte il valori distinti della colonna {column} presenti nella tabella {table}
	 *
	 *
	 * @param column - colonna da cui recuperare i valori
	 * @param table - tabella a cui appartiene la colonna
	 * @throws SQLException
	 * @throws EmptySetException
	 */
	public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException, EmptySetException {

		Set<Object> transSet = new HashSet<>();
		Statement statement;

		TableSchema tSchema = new TableSchema(db, table);

		if (tSchema.getNumberOfAttributes() == 0)
			throw new SQLException();

		String query = "SELECT  DISTINCT " + column.getColumnName() + " FROM " + table + " ORDER BY " + column.getColumnName() + " ASC";

		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty = true;
		while (rs.next()) {
			empty = false;
			transSet.add(rs.getObject(1));
		}
		rs.close();
		statement.close();
		if (empty) throw new EmptySetException();


		return transSet;
	}
}
