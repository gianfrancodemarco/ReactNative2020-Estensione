package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe che modella e gestisce la connessione al database
 * I dati necessari alla connessione sono memorizzati come variabili statiche (bad practice - andrebbero memorizzate in un file di configurazione a parte)
 */
public class DbAccess {
    private static String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private final String DBMS = "jdbc:mysql";
    private final String SERVER = "localhost";
    private final String DATABASE = "MapDB";
    private final int PORT = 3306;
    private final String USER_ID = "MapUser";
    private final String PASSWORD = "map";

    private Connection conn;

    /**
     * Costruttore della classe.
     * Effettua la connessione al database generando un'istanza di Connection
     * In caso di errore, solleva l'eccezione DatabaseConnectionException con un messaggio parlante che ne riporta la causa (Driver per la connessione non trovato; Database non trovato; SQL Exception)
     @throws DatabaseConnectionException
     */
    public DbAccess() throws DatabaseConnectionException
    {
        try {
            Class.forName(DRIVER_CLASS_NAME).newInstance();
        } catch(ClassNotFoundException e) {
            throw new DatabaseConnectionException("[!] Driver not found: " + e.getMessage());
        } catch(InstantiationException e){
            throw new DatabaseConnectionException("[!] Error during the instantiation : " + e.getMessage());
        } catch(IllegalAccessException e){
            throw new DatabaseConnectionException("[!] Cannot access the driver : " + e.getMessage());
        }

        String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
                + "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";

        System.out.println("Connection's String: " + connectionString);


        try {
            conn = DriverManager.getConnection(connectionString);
        } catch(SQLException e) {
            throw new DatabaseConnectionException(
                    "[!] SQLException: " + e.getMessage() + "\n"
                    + "[!] SQLState: " + e.getSQLState() + "\n"
                    +"[!] VendorError: " + e.getErrorCode()
            );
        }
    }


    public Connection getConnection(){
        return conn;
    }


    /**
     * Chiude la connessione con il Database
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException{
        conn.close();
    }
}
