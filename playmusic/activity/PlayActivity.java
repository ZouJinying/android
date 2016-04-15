package cn.edu.zucc.playmusic.activity;
import org.loon.framework.android.game.LGameAndroid2DActivity;
import android.content.Intent;
import android.os.Bundle;
import cn.edu.zucc.playmusic.screen.IPlayContext;
import cn.edu.zucc.playmusic.screen.PlayScreen;


public class PlayActivity extends LGameAndroid2DActivity implements IPlayContext{


	private PlayScreen playScreen = null;
	@Override
	public void onMain() {
		// TODO Auto-generated method stub
		this.initialization(false,LMode.Fill);
		this.setShowLogo(false);
		this.setShowFPS(false);
		this.setFPS(60);
		this.setDestroy(false);
		Intent intent = this.getIntent();
		Bundle bundle= intent.getExtras();
		playScreen = new PlayScreen(bundle.getString("songPath"),bundle.getString("beatPath"),this);
		this.setScreen(playScreen);
		this.showScreen();
	}
	
	@Override
	public void onGamePaused() {
		// TODO Auto-generated method stub
		if(playScreen != null)
		{
			System.out.println("pause");
			playScreen.thisPaused();
		}
	}

	@Override
	public void onGameResumed() {
		// TODO Auto-generated method stub
		if(playScreen != null)
		{
			System.out.println("resume");
			playScreen.thisResume();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		this.close();
	}

	@Override
	public void doClose() {
		// TODO Auto-generated method stub
		this.close();
	}
	
	
}
