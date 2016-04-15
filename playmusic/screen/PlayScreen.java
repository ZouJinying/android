package cn.edu.zucc.playmusic.screen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


import org.loon.framework.android.game.core.graphics.Screen;
import org.loon.framework.android.game.core.graphics.opengl.GLEx;
import org.loon.framework.android.game.core.graphics.opengl.LTexture;
import org.loon.framework.android.game.core.graphics.opengl.LTexture.Format;
import org.loon.framework.android.game.core.input.LTouch;

import org.loon.framework.android.game.core.timer.LTimerContext;
import org.loon.framework.android.game.physics.PhysicsObject;
import org.loon.framework.android.game.physics.PhysicsScreen;

import android.media.MediaPlayer;
import cn.edu.zucc.playmusic.model.Beat;
import cn.edu.zucc.playmusic.tool.XMLDealer;


import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;



public class PlayScreen  extends PhysicsScreen {

	private IPlayContext iPlayContext;
	private long allTime = 0;
	private int currentBeatIndex;

	private List<Beat> beats = null;
	private List<PhysicsObject> physicsObjects = null;
	private PhysicsObject[] scoreImage;
	private int score;
	private int fps=150;
	private MediaPlayer player;
	private int topY = 64;					//出现点距离顶部像素
	private int DX = 10; 					//轨道向中心收索像素	
	private int gravityY = 10;
	private boolean isPaused;				//现在是否暂停
	private boolean isChangeType;			//是否改变状态
	private boolean hasBeenScore;			//是否已经显示得分画面
	private boolean hasBeenDead;			//是否已经显示死亡画面
	private int currentBloodValue = 10;		//当前血量
	private PhysicsObject  scoreObject;
	private int beatImageWidth = 106;		//掉落图片的宽
	private int beatImageHigh = 66;			//掉落图片的高
	private int combo = 0;					//连击数
	private PhysicsObject bloodView = null;	//显示血量的精灵
	
	public PlayScreen(String musicPath,String configPath,IPlayContext iPlayContext) {
		super(0, 0f, true);		
		
		
		this.iPlayContext = iPlayContext;
		setGravity(0,gravityY);
		this.setRepaintMode(Screen.SCREEN_CANVAS_REPAINT);
		
		beats = new LinkedList<Beat>();
		physicsObjects = new LinkedList<PhysicsObject>();
		score = 0;
		allTime = 0;
		currentBeatIndex = 0;

		player = new MediaPlayer();
		
		try {
			player.setDataSource(musicPath);
			player.prepare();
			
			System.out.println("player ok");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			beats = XMLDealer.Reader(new FileInputStream(configPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		
		/*
		beats.add(new Beat(0, new int[]{0,1}));
		beats.add(new Beat(1000, new int[]{1,2}));
		beats.add(new Beat(2000, new int[]{1,2}));
		beats.add(new Beat(2500, new int[]{1,0}));
		beats.add(new Beat(3000, new int[]{0,2}));
		beats.add(new Beat(5000, new int[]{0,1}));
		beats.add(new Beat(6600, new int[]{1}));
		beats.add(new Beat(7100, new int[]{0,1,2}));
		beats.add(new Beat(8000, new int[]{0,2}));*/
	}
	
	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		super.onLoad();
		LTexture image = new LTexture("assets/bg.png",Format.SPEED);
		setBackground(image);
		drawBloodView(currentBloodValue);
		//drawScore(score);
	}
	
	public void onLoaded() {

		setPhysicsFPS(fps);
		setMultitouch(true);
		player.start();
		setWorldBox(this.getWidth(),this.getHeight()*2);

		
	}

	public void paint(GLEx g) {
		
	}

	public void update(LTimerContext t) 
	{
		excute(t);
	}
	private synchronized void excute(LTimerContext t)
	{
		if(isPaused)
		{
			if(!isChangeType)
			{
				player.pause();
				changeBeatObjectType(BodyType.KinematicBody);	
				isChangeType = true;
			}
			
		}
		else
		{
//			System.out.println("blood:"+currentBloodValue);
			
			if(!isChangeType)
			{
				player.start();
				changeBeatObjectType(BodyType.DynamicBody);
				isChangeType = true;
			}
			
			
			if(beats != null && currentBeatIndex<beats.size() )
			{
				if(allTime>=(beats.get(currentBeatIndex).getMicroSecond() - (long) ((getHeight()-this.beatImageHigh-topY)*1000.0/this.getPhysicsFPS()/gravityY)))
				{
					drawBeat(beats.get(currentBeatIndex));
					currentBeatIndex++;
				}
				allTime +=  t.getTimeSinceLastUpdate();
			}
			disposeBeatObject();
			
			if(currentBeatIndex==beats.size() && physicsObjects.isEmpty() && !hasBeenScore)
			{
				showScoreView();
			}
			
			if(currentBloodValue<=0 && !hasBeenDead)
			{
				showDeadView();
			}
		}
	}
	public void onDown(LTouch e) {
		
		int x = getTouchX();
		int y = getTouchY();
		int w = this.getWidth();
		int h = this.getHeight();
		
		PhysicsObject scoreObject = find(x, y, "score");
		if(scoreObject!=null && !hasBeenDead)
		{
			isChangeType = false;
			isPaused = !isPaused;
		}
		
		if((y+64+64>h)&&(!isPaused))
		{
			PhysicsObject beat = find(x, y, "beat");
			if(beat != null)
			{
				score++;
				combo++;
				
				drawScore(score);
				PhysicsObject beat2 = bindTo("assets/beat2.png",BodyType.DynamicBody,(int)beat.getPosition().x,(int)beat.getPosition().y);
				beat2.setTag("beatTouched");
				removeObject(beat);
				physicsObjects.remove(beat);
				physicsObjects.add(beat2);
				beat2.make();
				
				if((combo%5==0)&&(currentBloodValue<10))
				{
					currentBloodValue++;
					drawBloodView(currentBloodValue);
				}

			}
		}
		if(find((this.getWidth()-320)/2,(this.getHeight()-120)/2,"scoreView") != null)
		{
			System.out.println("try close(score)");
			this.close();
		}
		if(find((this.getWidth()-320)/2,(this.getHeight()-120)/2,"deadView") != null)
		{
			System.out.println("try close(dead)");
			this.close();
		}
		
	}

	public void onMove(LTouch e) {
		
	}

	public void onUp(LTouch e) {
		
	}
	
	private synchronized void  changeBeatObjectType(BodyType type)
	{
//		System.out.println("total:"+physicsObjects.size());
		
		for (int i=0;i<physicsObjects.size();i++) 
		{
			PhysicsObject oldObject = physicsObjects.get(i);
			
//			if(oldObject.getBody()==null || oldObject.getBody().getType().equals(type))
//			{
//				continue;
//			}
			
			int x = (int)oldObject.getPosition().x;
			int y = (int)oldObject.getPosition().y;
			
			PhysicsObject newObject = bindTo(oldObject.getBitmap(),type,x,y);
			newObject.setTag(oldObject.getTag());
			removeObject(oldObject);
			newObject.make();
			
			physicsObjects.remove(oldObject);
			physicsObjects.add(i, newObject);
		}
//		System.out.println("total2:"+physicsObjects.size());
	}
	
	
	private void disposeBeatObject()
	{
		for (int i=0;i<physicsObjects.size();i++) 
		{
			PhysicsObject o = physicsObjects.get(i);
			if(o.getPosition().y+7>this.getHeight())
			{
				if(o.getTag().equals("beat"))
				{
					currentBloodValue--;
					combo = 0;
					drawBloodView(currentBloodValue);
				}
				removeObject(o);
				physicsObjects.remove(o);
			}
		}
	}
	private void drawScore(int score)
	{
		String str = ""+score;
		
		int len = str.length();
		if(scoreImage!=null)
		{
			for(PhysicsObject o : scoreImage)
			{
				o.dispose();
			}
		}
		scoreImage = new PhysicsObject[len];
		
		for(int i=0;i<len;i++)
		{
			PhysicsObject num = bindTo("assets/"+str.charAt(i)+".png", BodyType.KinematicBody, this.getWidth()-38*len+38*i, 0);
			num.setTag("score");
			scoreImage[i] = num;
			num.make();
		}
		
	}
	private void drawBeat(Beat beat)
	{
		int h = this.getHeight();
		int w = this.getWidth();
		
		int[] widths = {DX,(w-this.beatImageWidth-2*DX)/2+10,w-this.beatImageWidth-DX};
		if(beat.getTypes() == null || beat.getTypes().length==0)
			return;
		
		for(int i=0;i<beat.getTypes().length;i++)
		{
			int flag = beat.getTypes()[i];
			if(flag!=0)
			{
				PhysicsObject o1 = bindTo("assets/beat.png", BodyType.DynamicBody, widths[flag-1], topY);
				o1.setTag("beat");
				physicsObjects.add(o1);
				o1.make();
			}
		}
		
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		if(player != null)
		{
			player.stop();
			player.release();
			player = null;
		}
		System.out.println("dispose");
	}

	public void thisPaused() 
	{
		if(!this.isPaused)
		{
			if(player!=null)
				player.pause();
			isChangeType = false;
			this.isPaused = true;
		}
		
	}
	
	public void thisResume()
	{
		if(!this.isPaused)
		{
//			if(player!=null)
//				player.pause();
			isChangeType = false;
			this.isPaused = false;
		}
	}
	public void drawBloodView(int currentBloodValue)
	{
		if(bloodView!=null)
		{
			removeObject(bloodView);
		}
		if(currentBloodValue<0)
		{
			currentBloodValue = 0;
		}
		bloodView = bindTo("assets/blood_"+currentBloodValue+".png", BodyType.StaticBody,10,10);
		bloodView.make();
	}
	public void showScoreView()
	{
		System.out.println("score:"+score);
		hasBeenScore = true;
		this.thisPaused();
		if(scoreObject!=null)
			removeObject(scoreObject);
		scoreObject = bindTo("assets/passed.png", BodyType.StaticBody,(this.getWidth()-320)/2,(this.getHeight()-120)/2);
		scoreObject.setTag("scoreView");
		scoreObject.make();
		
	}
	public void showDeadView()
	{
		hasBeenDead = true;
		System.out.println("you dead");
		thisPaused();
		scoreObject = bindTo("assets/gameover.png", BodyType.StaticBody,(this.getWidth()-320)/2,(this.getHeight()-120)/2);
		scoreObject.setTag("deadView");
		scoreObject.make();
	}
	public void close()
	{
//		this.setClose(true);
		for (PhysicsObject image : scoreImage)
		{
			this.removeObject(image);
		}
		
		this.removeObject(bloodView);

		if(this.iPlayContext != null)
		{
			this.iPlayContext.doClose();
		}
	}
}
