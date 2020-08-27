package database;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Utility per la modellazione del database
 */
public class TableSchema implements Iterable<Column> {


	private List<Column> tableSchema = new ArrayList<Column>();

	/**
	 * Costruttore della classe
	 * Recupera le informazioni sulle colonne della tabella indicata in input
	 *
	 * @param db        - istanza che modella la connessione al database
	 * @param tableName - tabella di cui recuperare le informazioni
	 * @throws SQLException
	 */
	public TableSchema(DbAccess db, String tableName) throws SQLException {

		HashMap<String, String> mapSQL_JAVATypes = new HashMap<String, String>();

		mapSQL_JAVATypes.put("CHAR", "string");
		mapSQL_JAVATypes.put("VARCHAR", "string");
		mapSQL_JAVATypes.put("LONGVARCHAR", "string");
		mapSQL_JAVATypes.put("BIT", "string");
		mapSQL_JAVATypes.put("SHORT", "number");
		mapSQL_JAVATypes.put("INT", "number");
		mapSQL_JAVATypes.put("LONG", "number");
		mapSQL_JAVATypes.put("FLOAT", "number");
		mapSQL_JAVATypes.put("DOUBLE", "number");


		Connection con = db.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet res = meta.getColumns(null, null, tableName, null);

		while (res.next()) {

			if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
				tableSchema.add(new Column(
						res.getString("COLUMN_NAME"),
						mapSQL_JAVATypes.get(res.getString("TYPE_NAME")))
				);


		}
		res.close();


	}

	/**
	 * Esegue un batch di comandi SQL sul DB.
	 * I comandi vengono passati tramite il parametro SQLInstructions; la stringa viene splittata sul carattere ';' e ogni elemento dell'array risultante
	 * viene trattato come un comando SQL
	 *
	 * @param SQLInstructions - stringa contenente tutti i comandi SQL separati da ';'
	 */
	public static void executeSQLBatch(String SQLInstructions) throws DatabaseConnectionException, SQLException {
		DbAccess dbAccess = new DbAccess();
		Connection con = dbAccess.getConnection();
		Statement statement = con.createStatement();


		for (String op : SQLInstructions.split(";")) {
			op = op.replaceAll("\\n", "");
			System.out.println(op + ";");
			if (op.length() > 0) statement.execute(op);
		}

		con.close();
	}

	/**
	 * Riceve in input un file di tipo .sql e ne esegue il contenuto (nel contesto di questo progetto il metodo è usato per creare e popolare tabelle)

	 @param SQLFile - file di tipo SQL
	 */
	public static void createTableFromSQLFile(File SQLFile) throws DatabaseConnectionException, SQLException, IOException {

		BufferedReader bufferedReader = new BufferedReader(new FileReader(SQLFile));
		String query = "";
		String line;
		while ((line = bufferedReader.readLine()) != null && line.length() > 0) {
			query += line;
		}

		bufferedReader.close();

		executeSQLBatch(query);
	}

	/**
	 * Riceve in input un file di tipo .dat, rappresentante un dataset
	 * Lo analizza e genera l'SQL corrispondente alla creazione e popolamento del dataset descritto dal file; quindi esegue l'SQL generato
	 *
	 * @param datasetFile - file di tipo .dat contenente il dataset
	 */
	public static void createTableFromFile(File datasetFile) throws DatabaseConnectionException, SQLException, IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(datasetFile));
		String tableName = datasetFile.getName();
		String query;
		String line;

		tableName = tableName.substring(0, tableName.indexOf('.'));

		query = "CREATE TABLE " + tableName + "(\n";

		// ********** CREATE TABLE ******************************************
		String tmpLine = "";
		line = bufferedReader.readLine();
		line = bufferedReader.readLine(); //skip first line
		do {
			query += tmpLine.length() > 0 ? "," : "";
			if (line.contains("@cont")) {
				tmpLine = line.replace("@cont ", "");
				query += tmpLine.substring(0, tmpLine.indexOf(" ") > -1 ? tmpLine.indexOf(" ") : tmpLine.length()) + " float(5,2)\n";
			} else {
				tmpLine = line.replace("@desc ", "");
				query += tmpLine.substring(0, tmpLine.indexOf(" ")) + " varchar(20)\n";
			}
			line = bufferedReader.readLine();
		} while (!line.contains("target"));
		query += ", target float(5,2));\n";
		// ****** END CREATE TABLE ******************************************

		// ********** INSERTS ******************************************
		line = bufferedReader.readLine(); //skips data
		while ((line = bufferedReader.readLine()) != null && line.length() != 0) {
			int index = 0;
			query += "insert into " + tableName + " values(";
			for (String value : line.split(",")) {
				query += "'" + value.trim();
				query += index == line.split(",").length - 1 ? "')" : "',";
				index++;
			}
			query += ";\n";
		}
		// ****** END INSERTS ******************************************

		//System.out.println(query);

		bufferedReader.close();

		executeSQLBatch(query);
	}

	/**
	 * Recupera l'elenco delle tabelle memorizzate nel database
	 */
	public static ArrayList<String> getTables() throws DatabaseConnectionException, SQLException {
		DbAccess dbAccess = new DbAccess();
		Connection con = dbAccess.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet rs = meta.getTables(con.getCatalog(), null, "%", null);

		ArrayList<String> tables = new ArrayList<>();

		while (rs.next()) {
			tables.add(rs.getString(3));
		}
		return tables;
	}


	public int getNumberOfAttributes() {
		return tableSchema.size();
	}

	/**
	 * Ritorna la colonna associata a {index}
	 *
	 * @param index - indice della colonna da recuperare
	 */
	public Column getColumn(int index) {
		return tableSchema.get(index);
	}


	@Override
	public Iterator<Column> iterator() {
		// TODO Auto-generated method stub
		return tableSchema.iterator();
	}


}




