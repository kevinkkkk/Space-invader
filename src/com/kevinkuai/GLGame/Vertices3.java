package com.kevinkuai.GLGame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.kevinkuai.GLGame.GLGraphics;

public class Vertices3 {
	final GLGraphics glGraphics;
	final boolean hasColor;
	final boolean hasTexCoords;
	final boolean hasNormals;
	final int verSize;
	final IntBuffer vertices;
	final ShortBuffer indices;
	final int[] tmpBuffer;
	
	public Vertices3(GLGraphics glGraphics, int maxVer, int maxInd, boolean hasColor, 
			boolean hasTex, boolean hasNormals){
		this.glGraphics = glGraphics;
		this.hasColor = hasColor;
		this.hasTexCoords = hasTex;
		this.hasNormals = hasNormals;
		this.verSize = (3+(hasColor?4:0)+(hasTex?2:0)+(hasNormals?3:0))*4;
		this.tmpBuffer = new int[maxVer*verSize/4];
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(maxVer*verSize);
		buffer.order(ByteOrder.nativeOrder());
		vertices = buffer.asIntBuffer();
		
		if (maxInd>0){
			ByteBuffer sb = ByteBuffer.allocateDirect(maxInd*Short.SIZE/8);
			sb.order(ByteOrder.nativeOrder());
			indices = sb.asShortBuffer();
		}else{
			indices = null;
		}
	}
	
	public void setVertices(float[] vertices, int offset, int length){
		this.vertices.clear();
		int len = offset+length;
		for (int i=offset, j=0; i<len; i++, j++){
			tmpBuffer[j]=Float.floatToRawIntBits(vertices[i]);
		}
		this.vertices.put(tmpBuffer, 0, length);
		this.vertices.flip();
	}
	
	public void setIndices(short[] indices, int offset, int length){
		this.indices.clear();
		this.indices.put(indices,offset,length);
		this.indices.flip();
	}
	
	public void bind(){
		GL10 gl = glGraphics.getGL();
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		vertices.position(0);
		gl.glVertexPointer(3, GL10.GL_FLOAT, verSize, vertices);;
		
		if(hasColor){
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			vertices.position(3);
			gl.glColorPointer(4, GL10.GL_FLOAT, verSize, vertices);
		}
		
		if(hasTexCoords){
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			vertices.position(hasColor?7:3);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, verSize, vertices);
		}
		
		if(hasNormals){
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			int offset = 3;
			if (hasColor)
				offset +=4;
			if(hasTexCoords)
				offset +=2;
			vertices.position(offset);
			gl.glNormalPointer(GL10.GL_FLOAT, verSize, vertices);
		}
	}
	
	public void draw(int primitiveType, int offset, int numVertices){
		GL10 gl=glGraphics.getGL();
		
		if(indices != null){
			indices.position(offset);
			gl.glDrawElements(primitiveType, numVertices, GL10.GL_UNSIGNED_SHORT, indices);
		}else{
			gl.glDrawArrays(primitiveType, offset, numVertices);
		}
	}
	
	public void unbind(){
		GL10 gl = glGraphics.getGL();
		if(hasTexCoords)
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		if(hasColor)
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
		if(hasNormals)
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}
	
	public int getNumIndices(){
		return indices.limit();
	}
	
	public int getNumVertices(){
		return vertices.limit() / (verSize/4);
	}
}
