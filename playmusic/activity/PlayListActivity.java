package cn.edu.zucc.playmusic.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zucc.playmusic.model.Song;
import cn.edu.zucc.playmusic.tool.SongDBTool;
import cn.zucc.edu.playmusic.R;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PlayListActivity extends Activity {
	private ListView lsv;
	private List<Map<String, Object>> list;
	private SoundPool sp = null;
	private int musicId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		musicId = sp.load(this, R.raw.button_on, 1);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.activity_play_list);
		lsv =(ListView)findViewById(R.id.playListView);
		lsv.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				songClick(arg2);
			}
			
		});
		SongDBTool songdb =new SongDBTool(this);
		
		List<Song> songlist =songdb.getAll();
		list =chTyper(songlist);
		
		
		SimpleAdapter adapter = new SimpleAdapter(this,list, R.layout.listitem,
                new String[]{"title","artist","album","path"},
                new int[]{R.id.itemtitle,R.id.itemartist,R.id.itempic,R.id.itempath});
		lsv.setAdapter(adapter);
		lsv.setVisibility(View.VISIBLE);
		if(songlist==null)
			Toast.makeText(this, "«Îœ»ÃÌº”∏Ë«˙", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_list, menu);
		return true;
	}

	public List<Map<String, Object>> chTyper(List<Song> list)
	{
		List<Map<String, Object>> result=new ArrayList<Map<String, Object>>();
		for(int i=0;i<list.size();i++)
		{
			Song song=list.get(i);
			if(song!=null)
			{
				HashMap<String, Object> nowMap = new HashMap<String, Object>();
				nowMap.put("song",song);
				nowMap.put("title", song.getTitle());
				nowMap.put("path", song.getPlayPath());
				nowMap.put("artist", song.getArtist());
				nowMap.put("album", R.raw.pic);
				result.add(nowMap);
			}	
		}	
		return result;
	}
	public void songClick(int itemId)
	{
		sp.play(musicId, 1, 1, 0, 0, 1);
		if(list==null||list.size()==0)
			return;
		
		Map songMap=list.get(itemId);
		Song song =(Song) songMap.get("song");
		Bundle bundle = new Bundle();
		bundle.putString("songPath", song.getPlayPath());
		bundle.putString("beatPath", song.getBeatPath());
		
		Intent intent = new Intent(this,PlayActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		System.out.println("playList finish");
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		System.out.println("playList back");
	}
}
