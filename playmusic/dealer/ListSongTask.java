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
				null,// �ֶΡ�û���ֶΡ����ǲ�ѯ������Ϣ���൱��SQL����еġ��� * ��
				null, // ��ѯ����
				null, // �����Ķ�Ӧ?�Ĳ���
				MediaStore.Audio.AudioColumns.TITLE);// ����ʽ		
		List<Map<String, Object>> mListData = new ArrayList<Map<String, Object>>();
	//	List<String> tempList=new ArrayList<String>();
		for (int i = 0; i < mAudioCursor.getCount(); i++) {
			mAudioCursor.moveToNext();
			// �ҵ������������ʱ���Ӧ��������
			int indexTitle = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);//����
			int indexARTIST = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);//������
			int indexDURATION = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);//ר��
			int indexPLAYPATH = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);//ר��	
			int indexALBUMART = mAudioCursor
					.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID);//ר��	
					
		
			String strTitle = mAudioCursor.getString(indexTitle);
			String strARTIST = mAudioCursor.getString(indexARTIST);
			int DURATION = mAudioCursor.getInt(indexDURATION);
			String playPath=mAudioCursor.getString(indexPLAYPATH);
			String BeatPath ="/mnt/sdcard/mudemo/"+strTitle+".xml";
	//		int AlbumArt=mAudioCursor.getInt(indexALBUMART);//ר������
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
	 private  String getAlbumArt(int album_id) {      //��ȡר������
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
