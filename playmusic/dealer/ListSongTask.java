package cn.edu.zucc.playmusic.dealer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.zucc.playmusic.model.Song;
import cn.zucc.edu.playmusic.R;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


public class ListSongTask  {
	Context context;

	public ListSongTask(Context context){
		this.context=context;
	}
	public List getSongsList()
	{
		//Bitmap bt=new Bitmap();  
		Cursor mAudioCursor = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null,// 字段　没有字段　就是查询所有信息　相当于SQL语句中的　“ * ”
				null, // 查询条件
				null, // 条件的对应?的参数
				MediaStore.Audio.AudioColumns.TITLE);// 排序方式		
		List<Map<String, Object>> mListData = new ArrayList<Map<String, Object>>();
	//	List<String> tempList=new ArrayList<String>();
		for (int i = 0; i < mAudioCursor.getCount(); i++) {
			mAudioCursor.moveToNext();
			// 找到歌曲标题和总时间对应的列索引
			int indexTitle = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);//歌名
			int indexARTIST = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);//艺术家
			int indexDURATION = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);//专辑
			int indexPLAYPATH = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);//专辑	
			int indexALBUMART = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID);//专辑	
					
		
			String strTitle = mAudioCursor.getString(indexTitle);
			String strARTIST = mAudioCursor.getString(indexARTIST);
			int DURATION = mAudioCursor.getInt(indexDURATION);
			String playPath=mAudioCursor.getString(indexPLAYPATH);
			String BeatPath ="/mnt/sdcard/mudemo/"+strTitle+".xml";
	//		int AlbumArt=mAudioCursor.getInt(indexALBUMART);//专辑封面
	//		System.out.println(AlbumArt);
		
			Song song=new Song(0,strTitle,strARTIST,DURATION,playPath,BeatPath);
	//		song.setAlbumart(getAlbumArt(AlbumArt));
	//		System.out.println(song.getAlbumart());
			HashMap<String, Object> nowMap = new HashMap<String, Object>();
			nowMap.put("song",song);
			nowMap.put("title", strTitle);
			nowMap.put("path", playPath);
			nowMap.put("artist", strARTIST);
//			nowMap.put("album", getAlbumArt(AlbumArt));
			nowMap.put("album", R.raw.pic);

		//	nowMap.put("Title",strTitle);
			mListData.add(nowMap);		
//			tempList.add(strTitle);
//			Log.i("audioR", strTitleKey);		
		}
		mAudioCursor=null;
		return mListData;
	}	
	 private  String getAlbumArt(int album_id) {      //获取专辑封面
         String mUriAlbums = "content://media/external/audio/albums";  
         String[] projection = new String[] { "album_art" };  
         Cursor cur = context.getContentResolver().query(  
                 Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),  
                 projection, null, null, null);  
         String album_art = null;  
         if (cur.getCount() > 0 && cur.getColumnCount() > 0) {  
             cur.moveToNext();  
             album_art = cur.getString(0);  
         }  
         cur.close();  
         cur = null;  
         return album_art;  
     } 
}
