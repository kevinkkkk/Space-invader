package com.kevinkuai.defender;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.kevinkuai.GLGame.Camera2D;
import com.kevinkuai.GLGame.FPSCounter;
import com.kevinkuai.GLGame.GLScreen;
import com.kevinkuai.GLGame.OverlapTest;
import com.kevinkuai.GLGame.Rectangle;
import com.kevinkuai.GLGame.SpriteBatcher;
import com.kevinkuai.GLGame.Vector;
import com.kevinkuai.framework.Game;
import com.kevinkuai.framework.Input.TouchEvent;

public class GameScreen extends GLScreen implements World.WorldListener{
	static final int GAME_RUNNING =0;
	static final int GAME_PAUSED = 1;
	static final int GAME_OVER =2;
	
	
	int state;
	Camera2D guiCam;
	Vector touchPoint;
	SpriteBatcher batcher;
	World world;
	//WorldListener listener;
	WorldRender render;
	Rectangle pausedButton;
	Rectangle resumeButton;
	Rectangle quitButton;
	Rectangle leftButton;
	Rectangle rightButton;
	Rectangle shotButton;
	Rectangle eyeCamBounds;
	int lastScore;
	int lastLives;
	int lastWaves;
	String scoreString;
	FPSCounter fpsCounter;

	public GameScreen(Game game) {
		super(game);
		// TODO Auto-generated constructor stub
		state = GAME_RUNNING;
		guiCam = new Camera2D(glGraphics, 480, 320);
		touchPoint = new Vector();
		batcher = new SpriteBatcher(glGraphics, 100);
		
		
		world = new World(this);
		render = new WorldRender(glGraphics);
		pausedButton = new Rectangle(480-64,320-64,64,64);
		resumeButton = new Rectangle(240-80,160,160,32);
		quitButton = new Rectangle(240-80,160-32,160,32);
		shotButton = new Rectangle(480-64,0,64,64);
		leftButton = new Rectangle(0,0,64,64);
		rightButton = new Rectangle(64,0,64,64);
		eyeCamBounds = new Rectangle(0,214,64,64);
		lastScore = 0;
		lastLives = world.ship.lives;
		lastWaves = world.wave;
		scoreString = "Lives: "+lastLives+" waves: "+lastWaves+" score: "+lastScore;
		fpsCounter= new FPSCounter();
		
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		switch(state){
		case GAME_PAUSED:
			updatePaused();
			break;
		
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_OVER:
			updateGameOver();
			break;
		}
		
	}

	
		
	

	private void updateGameOver() {
		// TODO Auto-generated method stub
		List<TouchEvent> touch = game.getInput().getTouchEvents();
		int len = touch.size();
		for (int i = 0; i<len; i++){
			TouchEvent te = touch.get(i);
			if(te.type==TouchEvent.TOUCH_UP){
				Assets.playSound(Assets.clickSound);
				game.setScreen(new MainMenuScreen(game));
			}
		}
		
	}

	private void updateRunning(float deltaTime) {
		// TODO Auto-generated method stub
		List<TouchEvent> touch = game.getInput().getTouchEvents();
		int len = touch.size();
		for (int i = 0; i<len; i++){
			TouchEvent te = touch.get(i);
			if (te.type!=TouchEvent.TOUCH_UP)
				continue;
			guiCam.touchToWorld(touchPoint.set(te.x, te.y));
			if(OverlapTest.pointInRec(pausedButton, touchPoint)){
				Assets.playSound(Assets.clickSound);
				state = GAME_PAUSED;
			}
			if (OverlapTest.pointInRec(eyeCamBounds, touchPoint)){
				render.rendEye(world.ship);
			}
			
			if(OverlapTest.pointInRec(shotButton, touchPoint)){
				world.shoot();
			}
		}
		
		world.update(deltaTime, getAccel(),getAccelZ());
		if (world.ship.lives!=lastLives || world.score!=lastScore || world.wave!=lastWaves){
			lastLives=world.ship.lives;
			lastScore=world.score;
			lastWaves=world.wave;
			scoreString = "Lives: "+lastLives+" waves: "+lastWaves+" score: "+lastScore;
		}
		if(world.isGameOver())
			state=GAME_OVER;
	}

	private float getAccel() {
		// TODO Auto-generated method stub
		float accelX = 0;
		if(Settings.touchEnabled){
			for(int i=0;i<2; i++){
				if(game.getInput().isTouchDown(i)){
					guiCam.touchToWorld(touchPoint.set
							(game.getInput().getTouchX(i), game.getInput().getTouchY(i)));
					if(OverlapTest.pointInRec(rightButton, touchPoint))
						accelX=Ship.SHIP_VELOCITY/5;
					
					if(OverlapTest.pointInRec(leftButton, touchPoint))
						accelX=-Ship.SHIP_VELOCITY/5;
				}
					
			}
		}else{
			accelX=game.getInput().getAccelY();
		}
		return accelX;
	}

	private void updatePaused() {
		// TODO Auto-generated method stub
		List<TouchEvent> touch = game.getInput().getTouchEvents();
		int len = touch.size();
		for (int i=0; i<len; i++){
			TouchEvent te = touch.get(i);
			if (te.type==TouchEvent.TOUCH_UP){
				guiCam.touchToWorld(touchPoint.set(te.x, te.y));
				
				if(OverlapTest.pointInRec(resumeButton, touchPoint)){
					Assets.playSound(Assets.clickSound);
					state = GAME_RUNNING;
				}
				
				if (OverlapTest.pointInRec(quitButton, touchPoint)){
					Assets.playSound(Assets.clickSound);
					game.setScreen(new MainMenuScreen(game));
				}
			}
		}
		
	}

	@Override
	public void present(float deltaTime) {
		// TODO Auto-generated method stub
		GL10 gl=glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		guiCam.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		batcher.beginBatch(Assets.background);
		batcher.drawSprite(240, 160, 480, 320, Assets.backgroundRegion);
		batcher.endBatch();
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		render.rending(world, deltaTime);
		
		switch(state){
		case GAME_RUNNING:
			presentRunning();
			break;
		case GAME_PAUSED:
			presentGamePaused();
			break;
		
		case GAME_OVER:
			presentGameOver();
			break;
		}
		fpsCounter.countFPS();
		
	}

	

	private float getAccelZ() {
		// TODO Auto-generated method stub
		return game.getInput().getAccelZ();
	}

	private void presentGameOver() {
		// TODO Auto-generated method stub
		GL10 gl = glGraphics.getGL();
		guiCam.setViewportAndMatrices();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		batcher.beginBatch(Assets.items);
		batcher.drawSprite(240, 260, 128, 64, Assets.gameOverRegion);
		Assets.font.drawText(batcher, scoreString, 10, 320-20);
		batcher.endBatch();
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		
	}

	private void presentRunning() {
		// TODO Auto-generated method stub
		GL10 gl = glGraphics.getGL();
		guiCam.setViewportAndMatrices();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		batcher.beginBatch(Assets.items);
		batcher.drawSprite(480-32, 320-32, 64, 64, Assets.pauseRegion);
		batcher.drawSprite(32, 245, 64, 64, Assets.eyeCam);
		Assets.font.drawText(batcher, scoreString, 10, 320-20);
		if(Settings.touchEnabled){
			batcher.drawSprite(32, 32, 64, 64, Assets.leftRegion);
			batcher.drawSprite(96, 32, 64, 64, Assets.rightRegion);
		}
		batcher.drawSprite(480-40, 32, 64, 64, Assets.fireRegion);
		batcher.endBatch();
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		
	}

	private void presentGamePaused() {
		// TODO Auto-generated method stub
		GL10 gl= glGraphics.getGL();
		guiCam.setViewportAndMatrices();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, scoreString, 10, 320-20);
		batcher.drawSprite(240, 160, 160, 64, Assets.pauseRegion);
		batcher.endBatch();
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		state = GAME_PAUSED;
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void explosion() {
		// TODO Auto-generated method stub
		Assets.playSound(Assets.explosionSound);
	}

	@Override
	public void shot() {
		// TODO Auto-generated method stub
		Assets.playSound(Assets.shotSound);
	}

	

	
	

}
