package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User extends DataType {

    private final ArrayList<Song> songLibrary = new ArrayList<>();
    private final ArrayList<Album> albumLibrary = new ArrayList<>();
    private final ArrayList<Artist> artistLibrary = new ArrayList<>();

    public User(ResultSet set) throws SQLException {
        super(set);
    }

    /**
     *
     * @param song
     */
    public void addSong(Song song) {
        songLibrary.add(song);
        addArtists(song.getArtistList());
    }

    public void addAlbum(Album album) {
        albumLibrary.add(album);
        addArtists(album.getArtistList());
    }

    private void addArtists(ArrayList<Artist> artists) {
        for(Artist a : artists) {
            if(!artistLibrary.contains(a)) {
                artistLibrary.add(a);
            }
        }
    }

    public ArrayList<Song> getSongLibrary() {
        return this.songLibrary;
    }

    public ArrayList<Album> getAlbumLibrary() {
        return this.albumLibrary;
    }

    public ArrayList<Artist> getArtistLibrary() {
        return this.artistLibrary;
    }

    public String toString() {
        return String.format("{user_id: %d}", getID());
    }

}
