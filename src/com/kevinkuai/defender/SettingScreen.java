package com.kevinkuai.defender;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.kevinkuai.GLGame.Camera2D;
import com.kevinkuai.GLGame.GLScreen;
import com.kevinkuai.GLGame.OverlapTest;
import com.kevinkuai.GLGame.Rectangle;
import com.kevinkuai.GLGame.SpriteBatcher;
import com.kevinkuai.GLGame.Vector;
import com.kevinkuai.framework.Game;
import com.kevinkuai.framework.Input.TouchEvent;

public class SettingScreen extends GLScreen {
	
	Camera2D guiCam;
	SpriteBatcher batcher;
	Vector touchPoint;
	Rectangle touchBounds;
	Rectangle accelBounds;
	Rectangle soundBounds;
	Rectangle backBounds;
	

	public SettingScreen(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
		guiCam = new Camera2D(glGraphics, 480,320);
		batcher = new SpriteBatcher(glGraphics, 10);
		touchPoint = new Vector();
		touchBounds = new Rectangle(120-32,160-32,64,64);
		accelBounds = new Rectangle(240-32,160-32,64,64);
		soundBounds = new Rectangle(360-32,160-32,64,64);
		backBounds = new Rectangle(32,32,64,64);
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-geneTouchEventod stub
		List<TouchEvent> touch = game.getInput().getTouchEvents();
		int len = touch.size();
		for (int i= 0; i<len; i++){
			TouchEvent te = touch.get(i);
			if (te.type != TouchEvent.TOUCH_UP)
				continue;
			
			guiCam.touchToWorld(touchPoint.set(te.x,te.y));
			if (OverlapTest.pointInRec(touchBounds, touchPoint)){
				Assets.playSound(Assets.clickSound);
				Settings.touchEnabled = true;
				Settings.save(game.getFileIO());
			}
			
			if (OverlapTest.pointInRec(accelBounds, touchPoint)){
				Assets.playSound(Assets.clickSound);
				Settings.touchEnabled = false;
				Settings.save(game.getFileIO());
			}
			
			if (OverlapTest.pointInRec(soundBounds, touchPoint)){
				Assets.playSound(Assets.clickSound);
				Settings.soundEnabled = !Settings.soundEnabled;
				if(Settings.soundEnabled){
					Assets.music.play();
				}else
					Assets.music.pause();
				
				Settings.save(game.getFileIO());
			}
			
			if (OverlapTest.pointInRec(backBounds, touchPoint)){
				Assets.playSound(Assets.clickSound);
				game.setScreen(new MainMenuScreen(game));
			}
		}
		
	}

	@Override
	public void present(float deltaTime) {
		// TODO Auto-generated method stub
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		guiCam.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		batcher.beginBatch(Assets.background);
		batcher.drawSprite(240, 160, 480, 320, Assets.backgroundRegion);
		batcher.endBatch();
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		batcher.beginBatch(Assets.items);
		batcher.drawSprite(240, 280, 224, 32, Assets.settingsRegion);
		batcher.drawSprite(120, 160, 64, 64, 
				Settings.touchEnabled?Assets.touchEnableRegion:Assets.touchRegion);
		batcher.drawSprite(240, 160, 64, 64, 
				Settings.touchEnabled?Assets.accelRegion:Assets.accelEnableRegion);
		batcher.drawSprite(360, 160, 64, 64, 
				Settings.soundEnabled?Assets.soundEnableRegion:Assets.soundRegion);
		batcher.drawSprite(32, 32, 64, 64, Assets.leftRegion);
		batcher.endBatch();
		
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	

}
