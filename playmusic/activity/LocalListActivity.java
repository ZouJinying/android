package cn.edu.zucc.playmusic.activity;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import cn.edu.zucc.playmusic.dealer.AnalyzerTask;
import cn.edu.zucc.playmusic.dealer.ListSongTask;
import cn.edu.zucc.playmusic.model.Song;
import cn.zucc.edu.playmusic.R;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;

import android.view.Menu;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.ListView;
import android.widget.SimpleAdapter;


public class LocalListActivity extends Activity implements OnItemClickListener {
	private MediaPlayer mplayer;
	private ListView lsv;
	private AnalyzerTask task;
	private List songlist;
	private SoundPool sp = null;
	private int musicId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_list);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		lsv=(ListView)findViewById(R.id.playListView);
		lsv.setOnItemClickListener(this);
		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		musicId = sp.load(this, R.raw.button_on, 1);
		loadList();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cleanUp();
	}

	private void cleanUp()
	 {
	    if (mplayer != null)
	    {
	     // mVisualizerView.release();
	      mplayer.release();
	      mplayer = null;
	    }
	 }
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	//	Log.i("audioR", arg0+" "+arg1+" "+arg2+" "+arg3);
		sp.play(musicId, 1, 1, 0, 0, 1);
		Song tempsong =(Song)((HashMap<String, Object>)songlist.get(arg2)).get("song");
		String path=tempsong.getPlayPath();
		String title=tempsong.getTitle();
	//	System.out.println(tempsong.getTitle()+" Ttile:"+title+"  Path1/2:"+path+" "+tempsong.getPlayPath());
		MediaPlayer mplayer=new MediaPlayer();
		try {
		//	System.out.println(mplayer.isPlaying()+"  "+path);
			mplayer.setDataSource(path);
			mplayer.prepare();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		task = new AnalyzerTask(this,mplayer,tempsong);
		task.execute("");	
	}

	public void loadList()
	{
		ListSongTask lstask =new ListSongTask(this);
		songlist=lstask.getSongsList();
		SimpleAdapter adapter = new SimpleAdapter(this,songlist, R.layout.listitem,
	                new String[]{"title","artist","album","path"},
	                new int[]{R.id.itemtitle,R.id.itemartist,R.id.itempic,R.id.itempath});
		lsv.setAdapter(adapter);
		lsv.setVisibility(View.VISIBLE);	
	}

}
