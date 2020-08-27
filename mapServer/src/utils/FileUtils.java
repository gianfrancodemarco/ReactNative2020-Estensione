package utils;

import database.TableSchema;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Base64;

public class FileUtils {

    /**
     * Mostra tutti i file presenti in una path
     *
     * @param path - la path da cui recuperare la lista dei file
     * @return la lista di stringhe con i nomi dei file
     *
     */
    public static ArrayList<String> getFilesInPath(String path){
        ArrayList<String> filesInPath = new ArrayList<>();
        final File folder = new File(path);

        for (final File fileEntry : folder.listFiles()) {
            filesInPath.add(fileEntry.getName());
        }

        return filesInPath;
    }

    /**
     * Legge un messaggio strutturato nella maniera seguente: {filename: [data], file: [data]}
     * Genera il file con nome {filename} e contenuto {file}
     * Se il file ha estensione .sql, esegue l'sql contenuto;
     * se il file ha estensione .dat, genera l'sql corrispondente e poi lo esegue
     *
     * @param message - il payload contenente le informazioni necessarie al metodo (filename, file)
     */
    public static boolean writeFileAndExecuteSql(String message) {

        //max 65537 chars
        boolean successful = true;

        try {
            JSONObject jsonObject = new JSONObject(message);

            String fileBytes = new String(Base64.getDecoder().decode(jsonObject.getString("file")));
            String filename = jsonObject.getString("filename");
            String ext = filename.substring(filename.indexOf('.')).toLowerCase();

            File file = new File(filename + ".tmp");

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(fileBytes);
            fileWriter.close();

            if(ext.equals(".sql"))
                TableSchema.createTableFromSQLFile(file);
            else if(ext.equals(".dat"))
                TableSchema.createTableFromFile(file);
            else successful = false;

            file.delete();
        }catch (Exception ex){
            ex.printStackTrace();
            successful = false;
        }

        return successful;
    }
}
