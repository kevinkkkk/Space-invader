package com.kevinkuai.defender;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kevinkuai.GLGame.OverlapTest;

public class World {
	
	public interface WorldListener{
		public void explosion();
		public void shot();
	}
	
	final static float WORLD_MAX_X =14;
	final static float WORLD_MIN_X = -14;
	final static float WORLD_MIN_Z = -15;
	
	WorldListener listener;
	int wave = 1;
	int score = 0;
	float speedMultiper =1;
	
	Ship ship;
	List<Bad> bads = new ArrayList<Bad>();
	List<Shot> shots = new ArrayList<Shot>();
	List<Shield> shields = new ArrayList<Shield>();
	
	long lastShotTime;
	Random random;
	
	public World(WorldListener listener){
		this.listener = listener;
		ship = new Ship(0,0,0);
		generatorBads();
		generatorShields();
		lastShotTime = System.nanoTime();
		random = new Random();
	}

	private void generatorShields() {
		// TODO Auto-generated method stub
		for (int i = 0; i<3; i++){
			shields.add(new Shield(-10+i*10-1,0,-3));
			shields.add(new Shield(-10+i*10+0,0,-3));
			shields.add(new Shield(-10+i*10+1,0,-3));
			shields.add(new Shield(-10+i*10-1,0,-2));
			shields.add(new Shield(-10+i*10+1,0,-2));
		}
	}

	private void generatorBads() {
		// TODO Auto-generated method stub
		for (int row = 0; row<4; row++){
			for (int colum =0; colum<8; colum++){
				Bad bad = new Bad(-WORLD_MAX_X/2 +colum*2f,0,WORLD_MIN_Z+row*2f);
				bads.add(bad);
			}
		}
		
	}
	
	public void update(float deltaTime, float accelX, float accelZ){
		ship.update(deltaTime, accelX, accelZ);
		updateBads(deltaTime);
		updateShots(deltaTime);
		
		checkShotCollision();
		checkBadCollision();
		
		if(bads.size()==0){
			generatorBads();
			wave++;
			speedMultiper += 0.5f;
		}
	}

	private void checkShotCollision() {
		// TODO Auto-generated method stub
		int len = shots.size();
		for (int i= 0; i<len; i++){
			Shot shot = shots.get(i);
			boolean shotRemoved = false;
			 
			int len2 = shields.size();
			for (int j=0; j<len2;j++){
				Shield shield = shields.get(j);
				if(OverlapTest.overlapSphere(shield.bounds, shot.bounds)){
					shields.remove(j);
					j--;
					len2--;
					shotRemoved = true;
					break;
				}
			}
			if(shotRemoved)
				continue;
			
			if(shot.velocity.z < 0){
				len2=bads.size();
				for (int j=0; j<len2;j++){
					Bad bad = bads.get(j);
					if(OverlapTest.overlapSphere(bad.bounds, shot.bounds)&&
							bad.state==Bad.BAD_ALIVE){
						bad.kill();
						listener.explosion();
						score += 10;
						shots.remove(i);
						i--;
						len--;
						break;
					}
				}
			}else {
				if(OverlapTest.overlapSphere(ship.bounds, shot.bounds)
					&& ship.state==Ship.SHIP_ALIVE){
					ship.kill();
					listener.explosion();
					shots.remove(i);
					i--;
					len--;
				}
			}
		}
		
	}

	private void checkBadCollision() {
		// TODO Auto-generated method stub
		if (ship.state == Ship.SHIP_EXPLOSION)
			return;
		
		int len = bads.size();
		for (int i=0; i<len; i++){
			Bad bad = bads.get(i);
			if(OverlapTest.overlapSphere(ship.bounds, bad.bounds)){
				ship.lives =1;
				ship.kill();
				return;
			}
		}
		
	}

	private void updateShots(float deltaTime) {
		// TODO Auto-generated method stub
		int len = shots.size();
		for (int i=0; i<len;i++){
			Shot shot = shots.get(i);
			shot.update(deltaTime);
			if (shot.position.z >0 || shot.position.z < WORLD_MIN_Z){
				shots.remove(i);
				i--;
				len--;
			}
		}
	}

	private void updateBads(float deltaTime) {
		// TODO Auto-generated method stub
		int len = bads.size();
		for (int i = 0; i<len; i++){
			Bad bad = bads.get(i);
			bad.update(deltaTime, speedMultiper);
			
			if (bad.state == Bad.BAD_ALIVE){
				if (random.nextFloat()<0.001f){
					Shot shot = new Shot(bad.position.x,bad.position.y,bad.position.z,
							Shot.SHOT_VELOCITY);
					shots.add(shot);
					listener.shot();
				}
				
			}
			
			if (bad.state == Bad.BAD_DEAD&&bad.stateTime>Bad.BAD_EXPLOSION_TIME){
				bads.remove(i);
				i--;
				len--;
			}
		}
		
	}
	
	public boolean isGameOver(){
		return ship.lives ==0;
	}
	
	public void shoot(){
		if(ship.state == Ship.SHIP_EXPLOSION)
			return;
		
		int friends= 0;
		int len = shots.size();
		for (int i=0; i<len; i++){
			Shot shot = shots.get(i);
			if (shot.velocity.z<0)
				friends++;
		}
		
		if(System.nanoTime()-lastShotTime>1000000000 || friends ==0){
			shots.add(new Shot(ship.position.x,ship.position.y,ship.position.z,-Shot.SHOT_VELOCITY));
			lastShotTime = System.nanoTime();
			listener.shot();
		}
		
	}

}
