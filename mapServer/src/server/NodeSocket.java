package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 * Classe per modellare la comunicazione con una Socket generata da un server Node.js
 * La classe incorpora una classica Socket di Java e la estende, con il meccanismo di composizione, con dei metodi specifici per la comunicazione
 * con la socket di Node.js
 */
public class NodeSocket {

    private DataInputStream in;
    private PrintWriter out;
    private Socket s;

    /**
     * Costruttore della classe
     *
     * @param s - Socket standard generata dalla socket server
     */
    public NodeSocket(Socket s) {
        this.s = s;
    }

    /**
     * Invia un messaggio alla socket client
     *
     * @param message - messaggio da inviare
     */
    public void sendMessage(String message) throws IOException {
        if (out == null)
            out = new PrintWriter(s.getOutputStream(), true);

        //System.out.println("SENT: " + message + " to client");
        out.println(message);
    }

    /**
     * Legge un messaggio dalla socket, lo parsa e lo ritorna
     */
    public String readMessage() throws IOException {
        if (in == null)
            in = new DataInputStream(s.getInputStream());
        while(in.available() < 1){
            try {
                sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
                s.close();
            }
        }
        byte[] byteArrayOutputStream = new byte[in.available()];
        in.readFully(byteArrayOutputStream);

        String decoded = new String(byteArrayOutputStream);
        System.out.println("RECEIVED: " + decoded);
        return decoded;
    }

    public void close() throws IOException {
        s.close();
    }
}
