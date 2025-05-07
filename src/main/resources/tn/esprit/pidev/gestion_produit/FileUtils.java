

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {
    public static String copierImage(String cheminSource) throws IOException {
        File source = new File(cheminSource);
        String nomFichier = source.getName();
        File destination = new File("images/" + nomFichier);

        // Créer le dossier images s'il n'existe pas
        destination.getParentFile().mkdirs();

        // Copier le fichier
        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return nomFichier;
    }
}
