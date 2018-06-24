package net.icelane.amplifire.analyzer.render.opengl;

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

import net.icelane.amplifire.Application;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GLGraph extends GLWRender {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1670118782697659663L;
	
	private int program;
	private int vao;
	private int vbo;

	float databuffer[] = new float[1000000 * 6 * 2];
	
	
	private int createProgram(){
		String shader_root = "/net/icelane/amplifire/analyzer/render/opengl/shaders";

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
	public void gl_startup() {
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

	@Override
	public void gl_render(double time) {
		float[] clearColor = Application.getColors().SETTING_color_aboutpage_background1.getRGBColorComponents(null);
		glClearBufferfv(GL_COLOR, 0, clearColor);

		glUseProgram(program);
		
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);

		// render the data arrays ...
		gl_renderLoop();
		
		// Put everything back to default (deselect)
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}

	public void gl_renderLoop() {
		gl_renderData(getData(), getNum());
	}
	
	public void gl_renderData(float[] data, int num) {
		// pass data to shader 
		glBufferSubData(GL_ARRAY_BUFFER, 0, data);
		// define what and how many to draw ..
		glDrawArrays(GL_LINE_STRIP, 0, num);   //number of points
		glPointSize(getPointSize());	
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
	public void gl_shutdown() {
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
