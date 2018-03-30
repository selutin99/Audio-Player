package galua.audio.newaudioplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    Button VKButton;
    SeekBar seekBarVolume;
    AudioManager audioManager;
    Switch light;
    Switch dark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        light = (Switch) findViewById(R.id.SwitchLight);
        dark = (Switch) findViewById(R.id.SwitchDark);

        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                light.setChecked(true);
                dark.setChecked(false);
                Toast.makeText(getApplicationContext(),"Как только так сразу",Toast.LENGTH_SHORT).show();
            }
        });
        dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dark.setChecked(true);
                light.setChecked(false);
                Toast.makeText(getApplicationContext(),"Как только так сразу",Toast.LENGTH_SHORT).show();
            }
        });

        seekBarVolume = (SeekBar)findViewById(R.id.SeekBarVolume);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        seekBarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        VKButton = (Button) findViewById(R.id.VKButton);
        VKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Скоро сделаю", Toast.LENGTH_SHORT).show();
            }
        });
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
        else if(id == R.id.settings_menu){
            return true;
        }
        else if(id == R.id.main_menu){
            Intent main = new Intent(this, MainActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(main);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSiteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
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
    public void onBackPressed() {
        finish();
    }

}
