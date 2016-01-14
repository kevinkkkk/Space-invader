package com.kevinkuai.defender;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.kevinkuai.GLGame.AmbientLight;
import com.kevinkuai.GLGame.Animation;
import com.kevinkuai.GLGame.DirectionalLight;
import com.kevinkuai.GLGame.GLGraphics;
import com.kevinkuai.GLGame.LookAtCamera;
import com.kevinkuai.GLGame.SpriteBatcher;
import com.kevinkuai.GLGame.TextureRegion;
import com.kevinkuai.GLGame.Vector3;

public class WorldRender {
	GLGraphics glGraphics;
	LookAtCamera cam;
	AmbientLight amLight;
	DirectionalLight dirLight;
	SpriteBatcher batcher;
	float badAngle=0;
	boolean eyeState = false;
	
	
	public WorldRender(GLGraphics glGraphics) {
		// TODO Auto-generated constructor stub
		this.glGraphics = glGraphics;
		cam = new LookAtCamera(67,glGraphics.getWidth()/(float)glGraphics.getHeight(),0.1f,100);
		cam.getPosition().set(0, 6, 2);
		cam.getLookAt().set(0, 0, -4);
		amLight = new AmbientLight();
		amLight.setColor(0.2f, 0.2f, 0.2f, 1.0f);
		dirLight = new DirectionalLight();
		dirLight.setDirection(-1, -0.5f, 0);
		batcher = new SpriteBatcher(glGraphics,10);
	}


	public void rending(World world,float deltaTime) {
		// TODO Auto-generated method stub
		GL10 gl = glGraphics.getGL();
		cam.getPosition().x=world.ship.position.x;
		cam.getLookAt().x=world.ship.position.x;
		//cam.getLookAt().z=-4*(accelZ/10);
		cam.setMatrices(gl);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		amLight.enable(gl);
		dirLight.enable(gl, GL10.GL_LIGHT0);
		
		rendShip(gl,world.ship);
		rendBad(gl, world.bads,deltaTime);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		rendShields(gl,world.shields);
		rendShots(gl,world.shots);
		
		gl.glDisable(GL10.GL_COLOR_MATERIAL);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_DEPTH_TEST);
	}


	private void rendShots(GL10 gl, List<Shot> shots) {
		// TODO Auto-generated method stub
		gl.glColor4f(1, 1, 0, 1);
		Assets.shotModel.bind();
		int len = shots.size();
		for (int i =0; i<len; i++){
			Shot shot = shots.get(i);
			gl.glPushMatrix();
			gl.glTranslatef(shot.position.x, shot.position.y, shot.position.z);
			Assets.shotModel.draw(GL10.GL_TRIANGLES, 0, Assets.shotModel.getNumVertices());
			gl.glPopMatrix();
		}
		Assets.shotModel.unbind();
		gl.glColor4f(1, 1, 1, 1);
	}


	private void rendShields(GL10 gl, List<Shield> shields) {
		// TODO Auto-generated method stub
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(0, 0, 1, 0.4f);
		Assets.shieldModel.bind();
		int len = shields.size();
		for (int i=0;i<len;i++){
			Shield shield = shields.get(i);
			gl.glPushMatrix();
			gl.glTranslatef(shield.position.x, shield.position.y, shield.position.z);
			Assets.shieldModel.draw(GL10.GL_TRIANGLES, 0, Assets.shieldModel.getNumVertices());
			gl.glPopMatrix();
		}
		Assets.shieldModel.unbind();
		gl.glColor4f(1, 1, 1, 1f);
		gl.glDisable(GL10.GL_BLEND);
	}


	private void rendBad(GL10 gl, List<Bad> bads, float deltaTime) {
		// TODO Auto-generated method stub
		badAngle += 45*deltaTime;
		
		Assets.badTexture.bind();
		Assets.badModel.bind();
		int len = bads.size();
		for (int i=0; i<len;i++){
			Bad bad = bads.get(i);
			if(bad.state==Bad.BAD_DEAD){
				gl.glDisable(GL10.GL_LIGHTING);
				Assets.badModel.unbind();
				rendExplosion(gl,bad.position,bad.stateTime);
				Assets.badTexture.bind();
				Assets.badModel.bind();
				gl.glEnable(GL10.GL_LIGHTING);
				
			}else{
				gl.glPushMatrix();
				gl.glTranslatef(bad.position.x, bad.position.y, bad.position.z);
				gl.glRotatef(badAngle, 0, 1, 0);
				Assets.badModel.draw(GL10.GL_TRIANGLES, 0, Assets.badModel.getNumVertices());
				gl.glPopMatrix();
			}
		}
		Assets.badModel.unbind();
	}


	private void rendExplosion(GL10 gl, Vector3 position, float stateTime) {
		// TODO Auto-generated method stub
		TextureRegion frame = Assets.explosionAni.getKeyFrame(stateTime, Animation.ANIMATION_NONLOOPING);
		gl.glEnable(GL10.GL_BLEND);
		gl.glPushMatrix();
		gl.glTranslatef(position.x, position.y, position.z);
		batcher.beginBatch(Assets.explosion);
		batcher.drawSprite(0, 0, 2, 2, frame);
		batcher.endBatch();
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_BLEND);
		
	}


	private void rendShip(GL10 gl,Ship ship) {
		// TODO Auto-generated method stub
		if(ship.state==Ship.SHIP_EXPLOSION){
			gl.glDisable(GL10.GL_LIGHTING);
			rendExplosion(gl,ship.position,ship.stateTime);
			gl.glEnable(GL10.GL_LIGHTING);
		}else{
			Assets.shipTexture.bind();
			Assets.shipModel.bind();
			gl.glPushMatrix();
			gl.glTranslatef(ship.position.x, ship.position.y, ship.position.z);
			gl.glRotatef(ship.velocity.x/Ship.SHIP_VELOCITY *90, 0, 0, -1);
			//gl.glRotatef(accelZ/Ship.SHIP_VELOCITY *90, 1, 0, 0);
			Assets.shipModel.draw(GL10.GL_TRIANGLES, 0, Assets.shipModel.getNumVertices());
			gl.glPopMatrix();
			Assets.shipModel.unbind();
		}
		
	}
	
	public void rendEye(Ship ship){
		if(!eyeState){
			cam.getPosition().set(ship.position.x, ship.position.y+1, 2);
			eyeState=true;
		}else{
			cam.getPosition().set(0, 6, 2);
			eyeState=false;
		}
		
	}
	
	
	

}
