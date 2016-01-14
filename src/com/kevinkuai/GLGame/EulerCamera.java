package com.kevinkuai.GLGame;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.Matrix;

public class EulerCamera {
	final Vector3 position = new Vector3(0,0,-1);
	float yaw;
	float pitch;
	float fieldOfView;
	float aspectRatio;
	float near;
	float far;
	
	public EulerCamera(float fieldOfView, float aspectRatio, float near, float far){
		this.fieldOfView = fieldOfView;
		this.aspectRatio = aspectRatio;
		this.near = near;
		this.far = far;
	}
	
	public Vector3 getPosition(){
		return position;
	}
	
	public float getYaw(){
		return yaw;
	}
	
	public float getPitch(){
		return pitch;
	}
	
	public void setAngles(float yaw, float pitch){
		if(pitch<-90)
			pitch=-90;
		if(pitch>90)
			pitch=90;
		this.yaw=yaw;
		this.pitch = pitch;
	}
	
	public void rotate(float yawInc, float pitchInc){
		this.yaw += yawInc;
		this.pitch =+ pitchInc;
		if (yaw<-90)
			yaw = -90;
		if (yaw>90)
			yaw=90;
	}
	
	public void setMatrices(GL10 gl){
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, fieldOfView, aspectRatio, near, far);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glRotatef(-pitch, 1, 0, 0);
		gl.glRotatef(-yaw, 0, 1, 0);
		gl.glTranslatef(-position.x, -position.y, -position.z);
	}
	
	final float[] matrix=new float[16];
	final float[] inVex = {0,0,-1,1};
	final float[] outVex = new float[4];
	final Vector3 direction = new Vector3();
	
	public Vector3 getDirection(){
		Matrix.setIdentityM(matrix, 0);
		Matrix.rotateM(matrix, 0, yaw, 0, 1, 0);
		Matrix.rotateM(matrix, 0, pitch, 1,0,0);
		Matrix.multiplyMV(outVex, 0, matrix, 0, inVex, 0);
		direction.set(outVex[0], outVex[1], outVex[2]);
		return direction;
	}

}
