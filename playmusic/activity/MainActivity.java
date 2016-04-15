package cn.edu.zucc.playmusic.activity;


import cn.zucc.edu.playmusic.R;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;



public class MainActivity extends Activity {
	
	private ImageButton btnStartPlay = null;
	private ImageButton btnStartLocalList = null;
	private SoundPool sp = null;
	private int musicId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		init();
	}

	private void init()
	{
		btnStartPlay = (ImageButton)findViewById(R.id.btnPlayList);
		btnStartLocalList = (ImageButton)findViewById(R.id.btnNewSongs);
		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		musicId = sp.load(this, R.raw.button_on, 1);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		btnStartPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sp.play(musicId, 1, 1, 0, 0, 1);
				startPlayScreen();
				
			}
		});
		
		btnStartLocalList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sp.play(musicId, 1, 1, 0, 0, 1);
				startLocalListActivity();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

	public void startPlayScreen()
	{
		Intent intent = new Intent(this,PlayListActivity.class);
		startActivity(intent);
	}
	
	public void startLocalListActivity()
	{
		Intent intent = new Intent(this,LocalListActivity.class);
		startActivity(intent);
	}
	

}
