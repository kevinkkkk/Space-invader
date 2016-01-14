package com.kevinkuai.defender;

import com.kevinkuai.GLGame.DynamicGameObject3D;

public class Bad extends DynamicGameObject3D{
	static final int BAD_ALIVE = 0;
	static final int BAD_DEAD = 1;
	static final float BAD_EXPLOSION_TIME = 1.6f;
	static final float BAD_RADIUS = 0.75f;
	static final float BAD_VELOCITY = 1;
	static final int MOVE_LEFT = 0;
	static final int MOVE_RIGHT =1;
	static final int MOVE_DOWN =2;
	
	int state = BAD_ALIVE;
	float stateTime = 0;
	int move = MOVE_LEFT;
	boolean wasLastStateLeft = true;
	float movedDistance = World.WORLD_MAX_X /2;
	
	public Bad(float x, float y, float z){
		super (x, y, z, BAD_RADIUS);
	}
	
	public void update(float deltaTime, float speedMultiper){
		if (state == BAD_ALIVE){
			movedDistance += deltaTime*BAD_VELOCITY*speedMultiper;
			if (move == MOVE_LEFT){
				position.x -= BAD_VELOCITY*deltaTime*speedMultiper;
				if (movedDistance > World.WORLD_MAX_X){
					move = MOVE_DOWN;
					movedDistance = 0;
					wasLastStateLeft = true;
				}
			}
			
			if (move == MOVE_RIGHT){
				position.x += deltaTime*BAD_VELOCITY*speedMultiper;
				if (movedDistance > World.WORLD_MAX_X){
					move = MOVE_DOWN;
					movedDistance = 0;
					wasLastStateLeft = false;
				}
			}
			
			if (move == MOVE_DOWN){
				position.z += deltaTime*BAD_VELOCITY*speedMultiper;
				if (movedDistance>1){
					if (wasLastStateLeft)
						move=MOVE_RIGHT;
					else
						move=MOVE_LEFT;
					movedDistance = 0;
				}
			}
			bounds.center.set(position);
		}
		stateTime += deltaTime;
	}
	
	public void kill(){
		state = BAD_DEAD;
		stateTime =0;
	}

}
