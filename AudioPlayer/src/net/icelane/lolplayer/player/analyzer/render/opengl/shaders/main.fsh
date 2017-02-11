#version 450 core

// main fragment shader

in vec4 color_from_vshader;
out vec4 color;

void main()
{
	color = color_from_vshader;
}