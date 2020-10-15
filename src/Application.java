import model.Song;
import model.Database;

import java.util.Collection;
import java.util.Iterator;

public class Application {

    public static void main(String[] args) {
        Database db = new Database();
        Collection<Song> songs = db.getDatabaseSongs();
        for (Song song : songs) {
            System.out.println(song);
        }
    }
}
