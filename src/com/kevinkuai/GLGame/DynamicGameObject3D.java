package com.kevinkuai.GLGame;

public class DynamicGameObject3D extends GameObject3D{
	public final Vector3 velocity;
	public final Vector3 accel;
	
	public DynamicGameObject3D(float x, float y, float z, float r){
		super(x,y,z,r);
		velocity = new Vector3();
		accel = new Vector3();
	}

}
