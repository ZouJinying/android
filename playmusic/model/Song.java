package cn.edu.zucc.playmusic.model;

import net.tsz.afinal.annotation.sqlite.*;

public class Song {
	@Id 
	private int id;
	private String title;
	private String artist;
	private int duration;
	private String playPath;
	private String beatPath;
	
	public Song(){}
	public Song(int id,String title, String artist,int duration, String playPath,String beatPath)
	{
		this.id=id;
		this.title=title;
		this.artist=artist;
		this.duration=duration;
		this.playPath=playPath;
		this.beatPath=beatPath;
	}
	

	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getArtist() {
		return artist;
	}
	public int getDuration() {
		return duration;
	}
	public String getPlayPath() {
		return playPath;
	}
	public String getBeatPath() {
		return beatPath;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public void setPlayPath(String playPath) {
		this.playPath = playPath;
	}
	public void setBeatPath(String beatPath) {
		this.beatPath = beatPath;
	}
	
	@Override
	public String toString() {
		return title;
	}
	
	
	
}
