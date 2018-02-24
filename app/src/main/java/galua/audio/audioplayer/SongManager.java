package galua.audio.audioplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SongManager {
    final String MEDIA_PATH = "storage/emulated/0/Music";
    private String mp3Pattern = ".mp3";
    private ArrayList<HashMap<String, String>> songsList = new ArrayList();

    public ArrayList<HashMap<String, String>> getPlayList() {
        if (MEDIA_PATH != null) {
            File[] listFiles = new File(MEDIA_PATH).listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    System.out.println(file.getAbsolutePath());
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }
                }
            }
        }
        return this.songsList;
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }
                }
            }
        }
    }

    private void addSongToList(File song) {
        if (song.getName().endsWith(this.mp3Pattern)) {
            HashMap<String, String> songMap = new HashMap();
            songMap.put("songTitle", song.getName().substring(0, song.getName().length() - 4));
            songMap.put("songPath", song.getPath());
            this.songsList.add(songMap);
        }
    }
}