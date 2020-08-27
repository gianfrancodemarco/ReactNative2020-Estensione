package server;

import com.mysql.cj.util.Base64Decoder;
import data.Data;
import data.TrainingDataException;
import database.DatabaseConnectionException;
import database.TableSchema;
import org.json.JSONException;
import tree.RegressionTree;
import utils.FileUtils;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Classe che gestice la comunicazione 1-1 con un client.
 * Estende la classe thread in modo da poter essere eseguita su un thread dedicato
 * (ad ogni client viene associato un'istanza di ServerOneClient; grazie al multithreading possono essere attive più connessioni contemporaneamente)
 */
public class ServerOneClient extends Thread {

    private NodeSocket socketInstance;
    private int counter = 0;

    /**
     * Costruttore della classe.
     * Recupera gli Input/Output stream dalla socket di riferimento
     * @param s - la socket con cui deve interfacciarsi l'istanza di ServerOneClient
     * @throws IOException
     */
    public ServerOneClient(Socket s) {
        socketInstance = new NodeSocket(s);
        System.out.println("Connection accepted!");
        System.out.println("Starting thread: " + ++counter);
        new Thread(this::run).start();
    }

    public void finalize(){
        System.out.println("Exiting thread" + this.getName());
    }

    /**
     * Il metodo run() gestisce la comunicazione con il client associato alla socket {s}
     * Esegue il metodo serveCilent() che rappresenta una facade per le funzionalità del server
     * Gestisce le eccezioni sollevate dal metodo serverClient() e si occupa di gestire il loop della computazione, se richiesto dal client
     */
    @Override
    public void run() {
        boolean restart = true;
        while (restart) {
            try {
                serveClient();
            } catch (Exception e) {
                try {
                    socketInstance.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    e.printStackTrace();
                    restart = false;
                }
        }

        System.out.println("Interrupting");
    }

    /**
     * Legge i dataset memorizzati sul DB e li invia sulla socket; attende quindi la scelta dell'utente.
     * Crea un'istanza di Data e poi di RegressionTree con la tabella selezionata dall'utente; quindi memorizza il RT generato
     *
     * @throws DatabaseConnectionException
     * @throws SQLException
     * @throws IOException
     * @throws TrainingDataException
     */
    public RegressionTree loadRegressionTreeFromDB() throws DatabaseConnectionException, SQLException, IOException, TrainingDataException {
        Data trainingSet = null;
        String tableName = null;
        ArrayList<String> tableList;
        RegressionTree tree = null;


        tableList = TableSchema.getTables();
        String tables = String.join(";", tableList);
        socketInstance.sendMessage("[DATASETS]" + tables);
        tableName = tableList.get(Integer.parseInt(socketInstance.readMessage())); //Proceed with learning

        trainingSet = new Data(tableName); //leggo da DB

        tree = new RegressionTree(trainingSet);
        tree.salva(tableName + ".dmp");
        return tree;

    }

    /**
     * Legge i RegressionTree memorizzati sul disco e li invia sulla socket; attende quindi la scelta dell'utente.
     * Crea un'istanza di RegressionTree deserializzando quello scelto dall'utente
     *

     * @throws IOException
     * @throws ClassNotFoundException
     **/
    public RegressionTree loadRegressionTreeFromDisk() throws IOException, ClassNotFoundException {
        Data trainingSet = null;
        Integer decision = null;
        String fileName = null;
        ArrayList<String> fileList;
        RegressionTree tree = null;


        fileList = FileUtils.getFilesInPath("resources/dumps");

        String tables = String.join(";", fileList);
        socketInstance.sendMessage("[DATASETS]" + tables);
        fileName = fileList.get(Integer.parseInt(socketInstance.readMessage())); //Proceed with learning

        return RegressionTree.carica(fileName);
    }

    /**
     * Gestisce il flusso di comunicazione con il client
     *
     * Attende la scelta dell'utente:
     * a)
     *  1) leggere un dataset da DB e generare il regression tree corrispondente;
     *  2) leggere un RegressionTree da disco e deserializzarlo;
     *  3) Caricare un nuovo dataset
     *
     *  Se viene scelto 3) - esegue e ripropone lo step a)
     *
     * 2) Una volta che è stato generato il RT, invia le stringhe rappresentanti l'albero e le sue regole al client
     * 3) Avvia la comunicazione con il client per la predizione di un nuovo valore
     */
    public void serveClient() throws ClassNotFoundException, DatabaseConnectionException, SQLException, TrainingDataException, IOException, UnkownValueException, InterruptedException {

        RegressionTree tree = null;
        Integer decision = null;

        decision = Integer.parseInt(socketInstance.readMessage());

        //FILE UPLOAD
        while (decision == 3){
            //get file
            String message = socketInstance.readMessage();

            if(FileUtils.writeFileAndExecuteSql(message)){
                socketInstance.sendMessage("[UPLOAD]" + "OK");
            }else socketInstance.sendMessage("[UPLOAD]" + "KO");

            decision = Integer.parseInt(socketInstance.readMessage());
        }

        if (decision.equals(0))
            tree = loadRegressionTreeFromDB();
        else tree =  loadRegressionTreeFromDisk();

        socketInstance.sendMessage("[RULES]" + tree.printRules());
        Thread.sleep(1000);
        socketInstance.sendMessage("[TREE]" + tree.toString() + "[ENDTREE]");

        try{
            while(socketInstance.readMessage().equals("[START PREDICTION]"))
                tree.predictClass(socketInstance);
        }catch (InterruptedIOException ex){
            ex.printStackTrace();
        }
    }
}

