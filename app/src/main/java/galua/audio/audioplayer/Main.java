package galua.audio.audioplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import galua.audio.audioplayer.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends ListActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    // Media Player
    private MediaPlayer mp;
    private SongManager plm;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    // Songs list
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.author_menu){
                    openSiteDialog();
                    return true;
                }
                return true;
            }
        });

        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.menu_main);

        // All player buttons
        btnPlay = (ImageButton) findViewById(R.id.play);
        btnForward = (ImageButton) findViewById(R.id.next);
        btnBackward = (ImageButton) findViewById(R.id.prev);
        btnNext = (ImageButton) findViewById(R.id.nextSong);
        btnPrevious = (ImageButton) findViewById(R.id.previousSong);
        btnRepeat = (ImageButton) findViewById(R.id.repeat);
        btnShuffle = (ImageButton) findViewById(R.id.shuffle);
        songProgressBar = (SeekBar) findViewById(R.id.seekbar);
        songTitleLabel = (TextView) findViewById(R.id.selectedfile);
        songCurrentDurationLabel = (TextView) findViewById(R.id.currentTime);
        songTotalDurationLabel = (TextView) findViewById(R.id.totalTime);

        // Mediaplayer
        mp = new MediaPlayer();
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important

        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

        plm = new SongManager();
        // get all songs from sdcard
        this.songsList = plm.getPlayList();
        
        HashMap<String, String> song;

        // looping through playlist
        for (int i = 0; i < songsList.size(); i++) {
            // creating new HashMap
            song = songsList.get(i);

            // adding HashList to ArrayList
            songsListData.add(song);
        }

        // Adding menuItems to ListView
        ListAdapter adapter = new SimpleAdapter(this, songsListData,
                R.layout.listitem, new String[] { "songTitle", "songPath" }, new int[] {
                R.id.songTitle, R.id.songPathes });

        setListAdapter(adapter);

        // selecting single ListView item
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting listitem index
                int songIndex = position;
                currentSongIndex = position;
                playSong(songIndex);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(songTitleLabel.getText().equals("Мелодия не выбрана") && !songsList.isEmpty()){
                    playSong(0);
                }
                else if(songsList.isEmpty()){

                }
                else {
                    if (mp.isPlaying()) {
                        if (mp != null) {
                            mp.pause();
                            // Changing button image to play button
                            btnPlay.setImageResource(android.R.drawable.ic_media_play);
                        }
                    } else {
                        // Resume song
                        if (mp != null) {
                            mp.start();
                            // Changing button image to pause button
                            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                        }
                    }
                }
            }
        });

        /**
         * Forward button click event
         * Forwards song specified seconds
         * */
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(songTitleLabel.getText().equals("Мелодия не выбрана")&& !songsList.isEmpty()){
                    playSong(0);
                }
                else if(songsList.isEmpty()){

                }
                else {
                    // get current song position
                    int currentPosition = mp.getCurrentPosition();
                    // check if seekForward time is lesser than song duration
                    if(currentPosition + seekForwardTime <= mp.getDuration()){
                        // forward song
                        mp.seekTo(currentPosition + seekForwardTime);
                    }else{
                        // forward to end position
                        mp.seekTo(mp.getDuration());
                    }
                }
            }
        });

        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(songTitleLabel.getText().equals("Мелодия не выбрана") && !songsList.isEmpty()){
                    playSong(0);
                }
                else if(songsList.isEmpty()){

                }
                else {
                    // get current song position
                    int currentPosition = mp.getCurrentPosition();
                    // check if seekBackward time is greater than 0 sec
                    if (currentPosition - seekBackwardTime >= 0) {
                        // forward song
                        mp.seekTo(currentPosition - seekBackwardTime);
                    } else {
                        // backward to starting position
                        mp.seekTo(0);
                    }
                }
            }
        });

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(songTitleLabel.getText().equals("Мелодия не выбрана") && !songsList.isEmpty()){
                    playSong(0);
                }
                else if(songsList.isEmpty()){

                }
                else {
                    // check if next song is there or not
                    if (currentSongIndex < (songsList.size() - 1)) {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        // play first song
                        playSong(0);
                        currentSongIndex = 0;
                    }
                }
            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(songTitleLabel.getText().equals("Мелодия не выбрана") && !songsList.isEmpty()){
                    playSong(0);
                }
                else if(songsList.isEmpty()){

                }
                else {
                    if (currentSongIndex > 0) {
                        playSong(currentSongIndex - 1);
                        currentSongIndex = currentSongIndex - 1;
                    } else {
                        // play last song
                        playSong(songsList.size() - 1);
                        currentSongIndex = songsList.size() - 1;
                    }
                }
            }
        });

        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Повторение выключено", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(android.R.drawable.ic_menu_revert);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Повторение включено", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.ic_menu_revert_focused);
                    btnShuffle.setImageResource(android.R.drawable.ic_menu_rotate);
                }
            }
        });


        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Перемешка выключена", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(android.R.drawable.ic_menu_rotate);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Перемешка включена", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.ic_menu_rotate_focused);
                    btnRepeat.setImageResource(android.R.drawable.ic_menu_revert);
                }
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // check for repeat is ON or OFF
                if(!songsList.isEmpty()) {
                    if (isRepeat) {
                        // repeat is on play same song again
                        playSong(currentSongIndex);
                    } else if (isShuffle) {
                        // shuffle is on - play a random song
                        Random rand = new Random();
                        currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                        playSong(currentSongIndex);
                    } else {
                        // no repeat or shuffle ON - play next song
                        if (currentSongIndex < (songsList.size() - 1)) {
                            playSong(currentSongIndex + 1);
                            currentSongIndex = currentSongIndex + 1;
                        } else {
                            // play first song
                            playSong(0);
                            currentSongIndex = 0;
                        }
                    }
                }
            }
        });
    }

    private void openSiteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setTitle("Об авторе")
                .setMessage("Автор: Александр Селютин.\n\nПриложение предназначено для прослушивания .mp3 файлов, загружаемых с телефона.")
                .setIcon(R.drawable.music_player_logo)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public ArrayList<HashMap<String, String>> getSongsList(){
        return songsList;
    }
    public int getCurrentSongIndex(){
        return currentSongIndex;
    }

    public void  playSong(int songIndex){
        // Play song
        try {
            if(!songsList.isEmpty()) {
                if (isRepeat) {
                    mp.setLooping(true);
                } else {
                    mp.setLooping(false);
                }

                mp.reset();
                mp.setDataSource(songsList.get(songIndex).get("songPath"));
                mp.prepare();
                mp.start();
                // Displaying Song title
                String songTitle = songsList.get(songIndex).get("songTitle");
                songTitleLabel.setText(songTitle);

                // Changing Button Image to pause image
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);

                // set Progress bar values
                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);
                // Updating progress bar
                updateProgressBar();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // check for repeat is ON or OFF
        if(!songsList.isEmpty()) {
            if (isRepeat) {
                // repeat is on play same song again
                playSong(currentSongIndex);
            } else if (isShuffle) {
                // shuffle is on - play a random song
                Random rand = new Random();
                currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                playSong(currentSongIndex);
            } else {
                // no repeat or shuffle ON - play next song
                if (currentSongIndex < (songsList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(songTitleLabel.getText().equals("Мелодия не выбрана") && !songsList.isEmpty()){
            playSong(0);
        }
        else {

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(!songsList.isEmpty()) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            int totalDuration = mp.getDuration();
            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

            // forward or backward to certain seconds
            mp.seekTo(currentPosition);

            // update timer progress again
            updateProgressBar();
        }
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };
    @Override
    public void onDestroy(){
        super.onDestroy();
        mp.release();
        System.exit(0);
    }
}
