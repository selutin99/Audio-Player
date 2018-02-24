package galua.audio.newaudioplayer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> arrayList;

    private ArrayList<HashMap<String, String>> songsList = new ArrayList();

    ListView listView;

    ListAdapter adapter;

    /*********************VIEWS*********************/
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

    private MediaPlayer mp;
    private Utilities utils;

    private Handler mHandler = new Handler();
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    /*******************END VIEWS*********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Все кнопки
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
        // Конец все кнопки

        mp = new MediaPlayer();
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }
        else{
            doStuff();
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(songTitleLabel.getText().equals("Мелодия не выбрана") && !songsList.isEmpty()){
                    playSong(0);
                }
                else if(songsList.isEmpty()){
                }
                else {
                    if (mp.isPlaying()) {
                        if (mp != null) {
                            mp.pause();
                            btnPlay.setImageResource(android.R.drawable.ic_media_play);
                        }
                    } else {
                        if (mp != null) {
                            mp.start();
                            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                        }
                    }
                }
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(songTitleLabel.getText().equals("Мелодия не выбрана")&& !songsList.isEmpty()){
                    playSong(0);
                }
                else if(songsList.isEmpty()){

                }
                else {
                    int currentPosition = mp.getCurrentPosition();
                    if(currentPosition + seekForwardTime <= mp.getDuration()){
                        mp.seekTo(currentPosition + seekForwardTime);
                    }else{
                        mp.seekTo(mp.getDuration());
                    }
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(songTitleLabel.getText().equals("Мелодия не выбрана") && !songsList.isEmpty()){
                    playSong(0);
                }
                else if(songsList.isEmpty()){

                }
                else {
                    int currentPosition = mp.getCurrentPosition();
                    if (currentPosition - seekBackwardTime >= 0) {
                        mp.seekTo(currentPosition - seekBackwardTime);
                    } else {
                        mp.seekTo(0);
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(songTitleLabel.getText().equals("Мелодия не выбрана") && !songsList.isEmpty()){
                    playSong(0);
                }
                else if(songsList.isEmpty()){

                }
                else {
                    if (currentSongIndex < (songsList.size() - 1)) {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        playSong(0);
                        currentSongIndex = 0;
                    }
                }
            }
        });

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
                        playSong(songsList.size() - 1);
                        currentSongIndex = songsList.size() - 1;
                    }
                }
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Повторение выключено", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(android.R.drawable.ic_menu_revert);
                }else{
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Повторение включено", Toast.LENGTH_SHORT).show();
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
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Перемешка включена", Toast.LENGTH_SHORT).show();
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.ic_menu_rotate_focused);
                    btnRepeat.setImageResource(android.R.drawable.ic_menu_revert);
                }
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!songsList.isEmpty()) {
                    if (isRepeat) {
                        playSong(currentSongIndex);
                    } else if (isShuffle) {
                        Random rand = new Random();
                        currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                        playSong(currentSongIndex);
                    } else {
                        if (currentSongIndex < (songsList.size() - 1)) {
                            playSong(currentSongIndex + 1);
                            currentSongIndex = currentSongIndex + 1;
                        } else {
                            playSong(0);
                            currentSongIndex = 0;
                        }
                    }
                }
            }
        });
    }

    public void doStuff(){
        listView = (ListView)findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new SimpleAdapter(this, songsList,
                R.layout.list_item, new String[] { "songTitle", "songPath" }, new int[] {
                R.id.songTitle, R.id.songPathes });
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int songIndex = position;
                currentSongIndex = position;
                playSong(songIndex);
            }
        });
    }

    public void getMusic(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor!=null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentPath = songCursor.getString(songPath);

                HashMap<String, String> songMap = new HashMap();
                songMap.put("songTitle", currentTitle.substring(0, currentTitle.length() - 4));
                songMap.put("songPath", currentPath);
                this.songsList.add(songMap);

            } while(songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSION_REQUEST:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();

                        doStuff();
                    }
                }
                else{
                    Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    public void  playSong(int songIndex){
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

                String songTitle = songsList.get(songIndex).get("songTitle");
                songTitleLabel.setText(songTitle);

                btnPlay.setImageResource(android.R.drawable.ic_media_pause);

                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);

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
        if(!songsList.isEmpty()) {
            if (isRepeat) {
                playSong(currentSongIndex);
            } else if (isShuffle) {
                Random rand = new Random();
                currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                playSong(currentSongIndex);
            } else {
                if (currentSongIndex < (songsList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    playSong(0);
                    currentSongIndex = 0;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.author_menu) {
            openSiteDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSiteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
        mHandler.removeCallbacks(mUpdateTimeTask);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(!songsList.isEmpty()) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            int totalDuration = mp.getDuration();
            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

            mp.seekTo(currentPosition);

            updateProgressBar();
        }
    }


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));

            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));

            songProgressBar.setProgress(progress);

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
