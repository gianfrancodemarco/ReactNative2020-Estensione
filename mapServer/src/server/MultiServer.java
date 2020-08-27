package server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe che gestisce la comunicazione client-server.
 * In particolare, il metodo run() crea una ServerSocket e rimane in attesa di una richiesta di connessione.
 * Quando un client si connette, crea una istanza di ServerOneClient e gli associa la socket generata.
 * L'istanza di ServerOneClient gestisce quindi la comunicazione con il client in un thread apposito mentre l'istanza di MultiServer torna in attesa di nuove richieste di connesione.
 */
public class MultiServer {

    private int PORT;

    public MultiServer(int port) throws IOException {
        PORT = port;
        run();
    }

    private void run() throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println(serverSocket.toString());

        while(true){
            Socket s = serverSocket.accept();
            System.out.println("Connection accepted from " + s.getRemoteSocketAddress());
            new ServerOneClient(s);
        }
    }

}
