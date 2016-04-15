package cn.edu.zucc.playmusic.dealer;


import java.io.File;
import java.util.LinkedList;
import java.util.List;

import cn.edu.zucc.playmusic.model.Beat;
import cn.edu.zucc.playmusic.model.FFTData;
import cn.edu.zucc.playmusic.model.Song;
import cn.edu.zucc.playmusic.tool.SongDBTool;
import cn.edu.zucc.playmusic.tool.XMLDealer;




import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;

import android.widget.Toast;

public class AnalyzerTask extends AsyncTask<String,Integer,String> {

	private byte[] FFTbytes;
    private byte[] mBytes;
    static final int lengthOfValue=5000;
    static final int vOfS=32;				//计算个数
    static final int mDivisions=32;			//每帧间隔
    static final long distance=220;			//精灵时间差
    static final long distance2=350;		//RAW数据的前后间隔
    private int[][] value=new int[3][lengthOfValue];			//第一行标准差，第二行和，第三行时间
    private int[] tempvalue=new int[vOfS];
    private  int[] tempTime=new int[vOfS];
    private int k=0;
    private int x=0;
    private int duration;
    private Song song;
	private Visualizer mVisualizer;
    public LinkedList<Beat> details;
  //  String addrOfBeatXML ="";
    private MediaPlayer player;
    private ProgressDialog pdialog;
    private Context context;
	
	public AnalyzerTask(Context context,MediaPlayer player, Song song) 
	{
		this.context=context;
		this.song=song;
		this.player=player;
		duration=player.getDuration();
	//	player.setv
		File file=new File("/mnt/sdcard/mudemo");
		if (!file.exists()) 
		{
			file.mkdirs();
		}
		while(!file.exists())
		{;}
//		System.out.println("isplaying:"+player.isPlaying()+" id:"+player.getAudioSessionId());
		
		
		pdialog = new ProgressDialog(context, 0); 
		pdialog.setCanceledOnTouchOutside(false);
		pdialog.setOnKeyListener(new OnKeyListener() {
		
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_BACK){
					   publishProgress(-1);
					   pdialog.dismiss();
					   return true;
				}
				else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN||keyCode==KeyEvent.KEYCODE_VOLUME_UP)
				{
					showToast("正在解析文件，无法调节声音");
					return true;
				}
				return false;
			}
			
		});
        pdialog.setButton("cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int i) {
          publishProgress(-1);
           
         }
        });
        pdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
         public void onCancel(DialogInterface dialog) {
          ;
         }
        });
        pdialog.setCancelable(true);
        pdialog.setMax(100);
        pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pdialog.show();
        player.setVolume(1, 1);
		AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2+3 ,      
                AudioManager.FLAG_PLAY_SOUND);

	}
	
	
	
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		player.start();
		  int id=player.getAudioSessionId();
		  mVisualizer = new Visualizer(id);   
		    mVisualizer.setCaptureSize(mVisualizer.getCaptureSizeRange()[1]);
			    
		    Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
		    {
		      @Override
		      public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
		          int samplingRate)
		      {
		        updateVisualizerFFT(bytes);
		      }

			@Override
			public void onWaveFormDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
		    };

		    mVisualizer.setDataCaptureListener(captureListener,
		        Visualizer.getMaxCaptureRate() / 2, true, true);

		    // Enabled Visualizer and disable when we're done with the stream
		    if(player.isPlaying())
		    	mVisualizer.setEnabled(true);
		    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		    {
		      @Override
		      public void onCompletion(MediaPlayer mediaPlayer)
		      {

		        mVisualizer.setEnabled(false);

		       // System.out.println(song.toString());
		        try {
		        	Thread.sleep(50);
					XMLDealer.writeBeatsXMl(song.getBeatPath(),  getBeatslist());
					SongDBTool songdb=new SongDBTool(context);
					//System.out.println("song:"+song.getBeatPath()+'\n'+" "+song.getPlayPath());
					if(!songdb.isExist(song))
						songdb.save(song);	
					System.out.println("savefinished");				
//					Song testsong=songdb.get(song.getId());
//					if(testsong==null)
//						System.out.println("song is null"+"  song id is:"+song.getId());
//					System.out.println("test:"+testsong.toString()+"--------finishtest");
					
					 publishProgress(100);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(context, "加载失败，请重新加载", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
		        
		      }
		    });
		    return null;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
//		Toast.makeText(null, "开始生成，请确认音量已经开启", Toast.LENGTH_SHORT);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		pdialog.setProgress(values[0]);
		if(values[0]==100){
			pdialog.dismiss();
			Toast.makeText(context, "加载完成", Toast.LENGTH_SHORT).show();
		}
		if(values[0]==-1)
		{
			onCancelled();
			
			mVisualizer.release();
			player.release();
		}

	}
	
	
	
	public void updateVisualizer(byte[] bytes) {
	    mBytes = bytes;
  }
    public void updateVisualizerFFT(byte[] bytes) {
	  FFTbytes = bytes;
	  dealer();
  }
  public void dealer()
  {
	  FFTData data =new FFTData(FFTbytes);
//	  Log.i("audioR", player.isPlaying()+"");

	  int[] result=new int[2];
	  for (int i = 0; i < data.bytes.length / mDivisions; i++) {
	     // mFFTPoints[i * 4] = i * 4 * mDivisions;
	   //   mFFTPoints[i * 4 + 2] = i * 4 * mDivisions;
	      byte rfk = data.bytes[mDivisions * i];
	      byte ifk = data.bytes[mDivisions * i + 1];
	      float magnitude = (rfk * rfk + ifk * ifk);
	      int temp=(int) ( 10*Math.log10(magnitude)*2);
	      if(player.isPlaying()&&temp!=-2147483648&&temp!=0)
	      {
	    	  tempTime[k%vOfS]=player.getCurrentPosition();
	    	  tempvalue[k%vOfS]=temp;
	    	  if(k!=0&&(k%vOfS==0))
	    	  {

    			  result=  getUpest(tempvalue);//最大值
	    		  value[1][x]=result[1];			
	    		  value[0][x]=result[0];
	    		  value[2][x]= getTime(tempvalue,tempTime);
	    	     if(x>1)
			      Log.i("audioR", "max/s:"+value[1][x]+"  sum:"+value[0][x]+",time:"+value[2][x]+"   x,k="+x+","+k+" length:"+value[1].length+"  时间间隔："+(value[2][x]-value[2][x-1]));
			      ///
		    	  publishProgress((int) ((value[2][x] / (float) duration) * 100));
		    	  
	    		  x++;		    	 
	    		  k=0;
	    	  }
		      k++;
	      }
	  }
  }


  public int getTime(int[] tempvalue,int[] time)
  {
	  int index=0;
	  for(int i=0;i<tempvalue.length;i++)
		  if(tempvalue[i]>=tempvalue[index])
			  index=i;
	return time[index];
  }
  

public List<Beat> getBeatslist()
{
	
	details = new LinkedList<Beat>();
	int ave0=0; int ave1=0;
	
	
	filter(value);//对value进行一次过滤
	
	int length=0;
	while(value[2][length]!=0)//第一行平均，第二行标准差或最大值，第三行时间
	{
		length++;
	}
	for(int i=0;i<length;i++)
		ave1 +=value[1][i];

	ave1/=length;
	Beat detail=null;
	int i=0;
	
	while(i<length)
	{
		int[] tempfalling = new int[2];
		double ob=(double)value[1][i]/ave1;		
		int sum =value[0][i];
		detail=new Beat();
		if(ob>0.8&&ob<1.1)
		{
//			detail=new Beat();
			
			tempfalling[0]=((int)(ob*100))%3+1;
			detail.setMicroSecond(value[2][i]);
			detail.setTypes(tempfalling);		
			Log.i("audioR", "result:"+value[1][i]+"  sum:"+sum+",time:"+value[2][i]+" r:"+ob+" Beat:"+tempfalling[0]+tempfalling[1]+"  ob*10%3:"+((int)ob*10)%3);
			details.add(detail);
		//	Log.i("audioR","time:"+details.get(i).getMicroSecond()+" "+((details.get(i)).getTypes())[0]+((details.get(i)).getTypes())[1]+"");
		}
		else if(ob>=1.1) 
		{
			tempfalling[0]=((int)(ob*100))%3+1;
			tempfalling[1]=((int)(ob*100+1))%3+1;
			detail.setMicroSecond(value[2][i]);
			detail.setTypes(tempfalling);	
			Log.i("audioR", "result:"+value[1][i]+"  sum:"+sum+",time:"+value[2][i]+" r:"+ob+" Beat:"+tempfalling[0]+tempfalling[1]+"  detail:"+detail.getTypes()[0]+detail.getTypes()[1]);
			details.add(detail);
	//		Log.i("audioR","time:"+details.get(i).getMicroSecond()+" "+((details.get(i)).getTypes())[0]+((details.get(i)).getTypes())[1]+"");
		}
		i++;
	}
	//	System.out.println(details);
	details=filter(details);
	return details;
}	
	public void cancelLoad() throws InterruptedException
	{
		Thread.sleep(50);
		mVisualizer.release();
		player.release();
	}
	
	public void showToast(String str)
	{
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
	public LinkedList<Beat> filter(LinkedList<Beat> ds)
	{
		int length=ds.size()-1;
		for(int i=0;i<length;i++)
		{
			Beat b1=ds.get(i);
			Beat b2=ds.get(i+1);
			long tempdis = b2.getMicroSecond()-b1.getMicroSecond();
			System.out.println(tempdis);
			if(tempdis<distance)
			{
				if(true)
				ds.remove(i+1);
				i--;
				length--;
			}
		}
		
		//移除最后5组数据
		ds.remove(ds.getLast());
		ds.remove(ds.getLast());
		ds.remove(ds.getLast());
		ds.remove(ds.getLast());
		ds.remove(ds.getLast());

		return ds;
	}

	public int[] getUpest(int[] valuelist)
	{
		double max=0;
		int[] result=new int[2];
		 double ave = 0;
	        for (int i = 0; i < vOfS; i++)
	            ave += valuelist[i];
	        result[0]=(int)(ave);
	        ave /= vOfS;
	        
	       int index=0;
		for(int i=0;i<vOfS;i++)
			if(valuelist[i]>valuelist[index])
				index=i;
		max=valuelist[index];
		
		result[1]=(int)max;
		return result;
	}
	
	public void filter(int list[][])
	{
		int length=0;
		while(value[2][length]!=0)//第一行平均，第二行标准差或最大值，第三行时间
		{
			length++;
		}
		for(int i=0;i<length-1;i++)
		{
			
			if((value[2][i+1]!=0)&&(value[2][i+1]!=0)&&(value[2][i+1]-value[2][i]<distance2))
			{
				int index;
				if(value[1][i+1]-value[1][i]<0)
				{
					index=i+1;
				}
				else
				{
					index=i;
				}
				value[0][index]=0;
				value[1][index]=0;
				value[2][index]=0;
			//	i--;
			}	
		}
		reOrder(value);
	}
	
	public void reOrder(int list[][])
	{
		int index=0;
		while(list[2][index]!=0)
			index++;
		int k=0;
		for(int i=0;i<lengthOfValue;i++)
		{
			if(list[2][i]!=0&&i>index)
			{
				list[0][index]=list[0][i];
				list[1][index]=list[1][i];
				list[2][index]=list[2][i];
				list[0][i]=0;
				list[1][i]=0;
				list[2][i]=0;
				index++;
			}
			while(list[2][index]!=0)
				index++;
		}

	}

}

