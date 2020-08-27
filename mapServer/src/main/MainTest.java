package main;

import database.DatabaseConnectionException;
import server.MultiServer;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Questo software permette di generare un albero di decisione, partendo da un dataset di
 * dati strutturati, composti sia da attributi discreti che continui
 * <p>Permette, inoltre, una volta appresa la struttura dell'albero, di procedere con la predizione di nuovi valori target
 *
 * @author Gianfranco Demarco
 */

public class MainTest {

    public static void main(String[] args) throws IOException, DatabaseConnectionException, SQLException {
        new MultiServer(8085);
    }
}
