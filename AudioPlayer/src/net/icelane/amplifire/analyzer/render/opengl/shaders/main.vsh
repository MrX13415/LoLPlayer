#version 450 core

// main vertext shader

//in vec2 prePos;
in vec2 vertexPos;
in vec4 color;
out vec4 color_from_vshader;

void main()
{
	gl_Position = vec4(vertexPos, 1.0, 1.0);
	color_from_vshader = color;
}