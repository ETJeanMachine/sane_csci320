package model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Album extends DataType {

    private final Date release_date;
    private final String album_name;
    private final ArrayList<Song> songs = new ArrayList<>();
    private final ArrayList<Artist> artists = new ArrayList<>();
    private final ArrayList<String> genres = new ArrayList<>();

    public Album(ResultSet set) throws SQLException {
        super(set);
        this.release_date = set.getDate("release_date");
        this.album_name = set.getString("album_name");
    }

    public Date getRelease_date() {
        return release_date;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void addGenre(String genre) {
        genres.add(genre);
    }

    public void addSong(Song song) {
        songs.add(song);
        for(Artist a : song.getArtistList()) {
            if(!artists.contains(a)) {
                artists.add(a);
            }
        }
    }

    public ArrayList<Song> getSongs() {
        return this.songs;
    }

    public ArrayList<Artist> getArtistList() {
        return artists;
    }

    public String getArtists() {
        ArrayList<String> artistNames = new ArrayList<>();
        for(Artist a : artists) {
            artistNames.add(a.getArtist_name());
        }
        return Arrays.toString(artistNames.toArray()).replaceAll("[\\[\\]]", "");
    }

    public String getGenres() {
        return Arrays.toString(genres.toArray()).replaceAll("[\\[\\]]", "");
    }
}
