package com.kevinkuai.defender;

import com.kevinkuai.GLGame.DynamicGameObject3D;

public class Shot extends DynamicGameObject3D{
	static float SHOT_RADIUS = 0.1f;
	static float SHOT_VELOCITY = 10.0f;

	public Shot(float x, float y, float z, float velocityZ) {
		super(x, y, z, SHOT_RADIUS);
		// TODO Auto-generated constructor stub
		velocity.z = velocityZ;
	}
	
	public void update(float deltaTime){
		position.z += velocity.z * deltaTime;
		bounds.center.set(position);
	}

}
