package net.icelane.amplifire.analyzer.render.opengl;

import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;

public class GL {

	public static void compileShader(int shader, CharSequence shaderSource){
		glShaderSource(shader, shaderSource);
		glCompileShader(shader);	
	}
	
	public static int loadShader_fromFile(String filename, int shaderType){
 		// load source ...
 		String source = loadFile(filename, true);
 		return loadShader(filename, source, shaderType);
	}
	
	public static int loadShader_fromPackage(String classpath, int shaderType){
		// load source ...
		String source = loadFile(classpath, true);
		return loadShader(classpath, source, shaderType);
	}

	public static int loadShader(String source, int shaderType){
		return loadShader("<shader>", source, shaderType);
	}

	public static int loadShader(String name, String source, int shaderType)
     {
 	 	/*
        .vert - a vertex shader
        .tesc - a tessellation control shader
        .tese - a tessellation evaluation shader
        .geom - a geometry shader
        .frag - a fragment shader
        .comp - a compute shader
    	 */
    	
 		//vertShader will be non zero if succefully created
 		int shader = glCreateShader(shaderType);
  
 		compileShader(shader, source);
 		
 		// acquire compilation status
 		int status = glGetShaderi(shader, GL_COMPILE_STATUS);
  
 		// check whether compilation was successful
 		if( status == GL11.GL_FALSE)
 		{
 			throw new IllegalStateException("compilation error for shader [" + name + "]. Reason: " + glGetShaderInfoLog(shader, 1000));
 		}
  
 		return shader;
     }

 	private static String loadFile(String path, boolean formPackage)
 	{
 		StringBuilder vertexCode = new StringBuilder();
 		BufferedReader reader = null;
 		try
 		{
 			if (formPackage){
 				InputStream classInput = new Object().getClass().getResourceAsStream(path);
 				reader = new BufferedReader(new InputStreamReader(classInput));
 			}else{
 				reader = new BufferedReader(new FileReader(path));
 			}
 			String line = null;
 			
 		    while( (line = reader.readLine()) != null )
 		    {
 		    	vertexCode.append(line);
 		    	vertexCode.append('\n');
 		    }
 		}
 		catch(Exception e){
 			throw new IllegalArgumentException("unable to load shader from file [" + path + "]", e);
 		}finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  
 		return vertexCode.toString();
 	}
  
    
}
