package cn.edu.zucc.playmusic.model;

public class Beat {
	private long microSecond;
	private int[] types;
	
	public Beat(){}
	public Beat(int microSecond,int[] types)
	{
		this.microSecond = microSecond;
		this.types = types;
	}
	
	public long getMicroSecond() {
		return microSecond;
	}
	public void setMicroSecond(long microSecond) {
		this.microSecond = microSecond;
	}
	public int[] getTypes() {
		return types;
	}
	public void setTypes(int[] types) {
		this.types = types;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "time:"+getMicroSecond()+" types:"+getTypes();
	}
}
