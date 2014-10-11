package Dreamer.runnableExamples;

import java.io.BufferedReader;
import java.io.FileReader;
 
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.*;
 
public class ShaderProgram 
{
	// OpenGL handle that will point to the executable shader program
	// that can later be used for rendering
	private int programId;
 
	public void init(String vertexShaderFilename, String fragmentShaderFilename)
	{
		// create the shader program. If OK, create vertex and fragment shaders
		programId = glCreateProgram();
 
		// load and compile the two shaders
		int vertShader = loadAndCompileShader(vertexShaderFilename, GL_VERTEX_SHADER);
		int fragShader = loadAndCompileShader(fragmentShaderFilename, GL_FRAGMENT_SHADER);
 
		// attach the compiled shaders to the program
		glAttachShader(programId, vertShader);
		glAttachShader(programId, fragShader);
 
		// now link the program
		glLinkProgram(programId);
 
		// validate linking
		if (glGetProgrami(programId, GL_LINK_STATUS) == GL11.GL_FALSE) 
		{
			throw new RuntimeException("could not link shader. Reason: " + glGetProgramInfoLog(programId, 1000));
		}
 
		// perform general validation that the program is usable
		glValidateProgram(programId);
 
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL11.GL_FALSE)
		{
			throw new RuntimeException("could not validate shader. Reason: " + glGetProgramInfoLog(programId, 1000));            
		}
	}
 
   /*
    * With the exception of syntax, setting up vertex and fragment shaders
    * is the same.
    * @param the name and path to the vertex shader
    */
	private int loadAndCompileShader(String filename, int shaderType)
	{
		//vertShader will be non zero if succefsully created
		int handle = glCreateShader(shaderType);
 
		if( handle == 0 )
		{
			throw new RuntimeException("could not created shader of type "+shaderType+" for file "+filename+". "+ glGetProgramInfoLog(programId, 1000));
		}
 
		// load code from file into String
		String code = loadFile(filename);
 
		// upload code to OpenGL and associate code with shader
		glShaderSource(handle, code);
 
		// compile source code into binary
		glCompileShader(handle);
 
		// acquire compilation status
		int shaderStatus = glGetShaderi(handle, GL20.GL_COMPILE_STATUS);
 
		// check whether compilation was successful
		if( shaderStatus == GL11.GL_FALSE)
		{
			throw new IllegalStateException("compilation error for shader ["+filename+"]. Reason: " + glGetShaderInfoLog(handle, 1000));
		}
 
		return handle;
	}
 
	/**
	 * Load a text file and return it as a String.
	 */
	private String loadFile(String filename)
	{
		StringBuilder vertexCode = new StringBuilder();
		String line = null ;
		try
		{
		    BufferedReader reader = new BufferedReader(new FileReader(filename));
		    while( (line = reader.readLine()) !=null )
		    {
		    	vertexCode.append(line);
		    	vertexCode.append('\n');
		    }
		    reader.close();
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("unable to load shader from file ["+filename+"]", e);
		}
 
		return vertexCode.toString();
	}
 
 
	public int getProgramId() {
		return programId;
	}    
}