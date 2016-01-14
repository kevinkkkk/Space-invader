package com.kevinkuai.GLGame;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class HierObject {
	public float x,y,z;
	public float scale=1;
	public float rotationY, rotationParent;
	public boolean hasParent;
	public final List<HierObject> children = new ArrayList<HierObject>();
	public final Vertices3 cube;
	
	public HierObject(Vertices3 mesh, boolean hasParent){
		this.cube = mesh;
		this.hasParent = hasParent;
	}

	public void update(float deltaTime){
		rotationY +=45*deltaTime;
		rotationParent +=20*deltaTime;
		int len = children.size();
		for (int i=0; i<len; i++){
			children.get(i).update(deltaTime);
		}
	}
	
	public void render(GL10 gl){
		gl.glPushMatrix();
		if(hasParent)
			gl.glRotatef(rotationParent, 0,1,0);
		gl.glTranslatef(x, y, z);
		gl.glPushMatrix();
		gl.glRotatef(rotationY, 0, 1, 0);
		gl.glScalef(scale, scale, scale);
		cube.draw(GL10.GL_TRIANGLES, 0, 36);
		gl.glPopMatrix();
		int len = children.size();
		for (int i=0; i<len; i++){
			children.get(i).render(gl);
		}
		gl.glPopMatrix();
	}
}
