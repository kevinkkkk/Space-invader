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

public class MainMenuScreen extends GLScreen {
	
	Camera2D guiCam;
	SpriteBatcher batcher;
	Vector touchPoint;
	Rectangle playBounds;
	Rectangle settingsBounds;

	public MainMenuScreen(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
		guiCam = new Camera2D(glGraphics, 480,320);
		batcher = new SpriteBatcher(glGraphics, 10);
		touchPoint = new Vector();
		playBounds = new Rectangle(240-112,100,224,32);
		settingsBounds = new Rectangle(240-112, 100-32, 224,32);
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		List<TouchEvent> touch = game.getInput().getTouchEvents();
		int len = touch.size();
		for (int i = 0; i<len; i++){
			TouchEvent te = touch.get(i);
			if (te.type != TouchEvent.TOUCH_UP)
				continue;
			
			guiCam.touchToWorld(touchPoint.set(te.x,te.y));
			if (OverlapTest.pointInRec(playBounds, touchPoint)){
				Assets.playSound(Assets.clickSound);
				game.setScreen(new GameScreen(game));
			}
			
			if (OverlapTest.pointInRec(settingsBounds, touchPoint)){
				Assets.playSound(Assets.clickSound);
				game.setScreen(new SettingScreen(game));
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
		batcher.drawSprite(240, 260, 480, 320, Assets.backgroundRegion);
		batcher.endBatch();
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		batcher.beginBatch(Assets.items);
		batcher.drawSprite(240, 240, 383, 128, Assets.logoRegion);
		batcher.drawSprite(240, 100, 224, 64, Assets.menuRegion);
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
