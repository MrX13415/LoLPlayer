package net.icelane.lolplayer.player.analyzer.render.opengl;

import static org.lwjgl.opengl.GL11.GL_COLOR;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import net.icelane.lolplayer.Application;

public class GLGraph extends GLWRender implements GLRender{

	private int program;
	private int vao;
	private int vbo;
	

	float databuffer[] = new float[1000000 * 6 * 2];
	
	@Override
	public void startup() {
		this.program = createProgram();
		
		// Bind index 0 to the shader input variable "vertexPos"
		glBindAttribLocation(program, 0, "vertexPos");
		
		// Create the buffer objects
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
	
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, databuffer, GL_DYNAMIC_DRAW);

		// The size of float, in bytes (will be 4)
		final int sizeOfFloat = Float.SIZE / Byte.SIZE;

		// The sizes of the vertex and color components
		final int vertexSize = 2 * sizeOfFloat;
		final int colorSize  = 4 * sizeOfFloat;

		// The 'stride' is the sum of the sizes of individual components
		final int stride = vertexSize + colorSize;

		// The 'offset is the number of bytes from the start of the tuple
		final long offsetPosition = 0;
		final long offsetColor    = 2 * sizeOfFloat;

		// position data
		int position_attribute = glGetAttribLocation(this.program, "vertexPos");
		glVertexAttribPointer(position_attribute, 2, GL_FLOAT, false, stride, offsetPosition);
		glEnableVertexAttribArray(position_attribute);
		
		// color data
		int color_attribute = glGetAttribLocation(this.program, "color");
		glVertexAttribPointer(color_attribute, 4, GL_FLOAT, false,  stride, offsetColor);
		glEnableVertexAttribArray(color_attribute);
	}
	
	private int createProgram(){
		String shader_root = "/net/icelane/lolplayer/player/analyzer/render/opengl/shaders";

		int vertex_shader   = GL.loadShader_fromPackage(shader_root + "/main.vsh", GL_VERTEX_SHADER); 
		int fragment_shader = GL.loadShader_fromPackage(shader_root + "/main.fsh", GL_FRAGMENT_SHADER); 
		
		int program = glCreateProgram();
		glAttachShader(program, vertex_shader);
		glAttachShader(program, fragment_shader);
		glLinkProgram(program);
		
		glDeleteShader(vertex_shader);
		glDeleteShader(fragment_shader);
		
		return program;
	}

	@Override
	public void render(double time) {
		float[] clearColor = Application.getColors().SETTING_color_aboutpage_background1.getRGBColorComponents(null);
		glClearBufferfv(GL_COLOR, 0, clearColor);

		glUseProgram(program);
		
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		
		//x
		//positionData[0] = ((float)Math.sin(time));
		//y
		//positionData[1] = 0; //Math.abs(((float)Math.sin(time + (Math.PI/2)))) - 0.5f;
		
		//pass data to shader 
		glBufferSubData(GL_ARRAY_BUFFER, 0, getData());
	
		glDrawArrays(GL_POINTS, 0, getNum());   //number of points
		glPointSize(getPointSize());
		
		// Put everything back to default (deselect)
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
	
	public float getPointSize(){
		return 0.5f;
	}
	
	public int getNum(){
		return 1;	
	}
	
	public float[] getData(){
		return new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};	
	}

	@Override
	public void shutdown() {
		// Disable the VBO index from the VAO attributes list
		glDisableVertexAttribArray(0);
		 
		// Delete the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vbo);
		 
		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vao);
		
		glDeleteVertexArrays(vao);
		glDeleteProgram(program);
		glDeleteVertexArrays(vao);
	}

}
