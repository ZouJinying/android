package cn.edu.zucc.playmusic.tool;

import java.util.List;

import cn.edu.zucc.playmusic.model.Song;

import android.content.Context;



import net.tsz.afinal.FinalDb;



public class SongDBTool {
	
	private  FinalDb db;
	private Context context;
	public SongDBTool(Context context){
		this.context=context;
	}
	
	public FinalDb getSession()
	{

		db = FinalDb.create(context,"song.db", true);

		return db; 
	}
	
	public  boolean save(Song song)
	{
		try{
			getSession().save(song);
		}
		catch(Exception e)
		{
			System.out.println("save failed");
			return false;
		}
		return true;
	}
	
	public  Song get(int id)
	{
		Song song = null;
		try {
			song = getSession().findById(id,Song.class);
			//song=getSession();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("getfailed");
			song = null;
		}
		
		return song;
	}
	
	public  boolean delete(int id)
	{
		try {
			getSession().deleteById(Song.class, id);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
	public List<Song> getAll()
	{
		try{
			return getSession().findAll(Song.class);
		}catch(Exception e)
		{
			return null;
		}
		
	}
	
	public boolean isExist(Song song)
	{
		
		List<Song> list =getAll();
		if(list!=null)
			for(int i=0;i<list.size();i++)
			{
				if(song.getBeatPath().equals(list.get(i).getBeatPath()))
					return true;
			}
		return false;
		
	}
	/*public List<Song> getall()
	{
		try {
			
			return getSession().findAllByWhere(Song.class, "select * from com_example_model_Song");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("getallFailed");
			return null;
		}
	}*/
}
