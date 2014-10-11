package Dreamer.runnableExamples;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;
 
import java.nio.FloatBuffer;
 
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
 
public class ShaderExample 
{
	/**
	 * General initialization stuff for OpenGL
	 */
	public void initGl() throws LWJGLException
	{
		// width and height of window and view port
		int width = 640;
		int height = 480;
 
		// set up window and display
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setVSyncEnabled(true);
		Display.setTitle("Shader Example");
 
		// set up OpenGL to run in forward-compatible mode
		// so that using deprecated functionality will
		// throw an error.
		PixelFormat pixelFormat = new PixelFormat();
		ContextAttribs contextAtrributes = new ContextAttribs(3, 2);
		contextAtrributes.withForwardCompatible(true);
		contextAtrributes.withProfileCore(true);
		Display.create(pixelFormat, contextAtrributes);
 
		// initialize basic OpenGL stuff
		GL11.glViewport(0, 0, width, height);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}
 
	public void run()
	{
		// compile and link vertex and fragment shaders into
		// a "program" that resides in the OpenGL driver
		ShaderProgram shader = new ShaderProgram();
 
		// do the heavy lifting of loading, compiling and linking
		// the two shaders into a usable shader program
		shader.init("src/Dreamer/runnableExamples/simple.vertex", "src/Dreamer/runnableExamples/simple.fragment");		
 
		int vaoHandle = constructVertexArrayObject();
 
		while( Display.isCloseRequested() == false )
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
 
			// tell OpenGL to use the shader
			GL20.glUseProgram(shader.getProgramId());
 
			// bind vertex and color data
			GL30.glBindVertexArray(vaoHandle);
			GL20.glEnableVertexAttribArray(0); // VertexPosition
			GL20.glEnableVertexAttribArray(1); // VertexColor
 
			// draw VAO
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
 
			// check for errors
			if( glGetError() != GL_NO_ERROR )
			{
				throw new RuntimeException("OpenGL error: "+GLU.gluErrorString(glGetError()));
			}
 
			// swap buffers and sync frame rate to 60 fps
			Display.update();
			Display.sync(60);
		}
 
		Display.destroy();
	}
	/**
	 * Create Vertex Array Object necessary to pass data to the shader
	 */
	int constructVertexArrayObject(float[] positionData, float[] colorData)
	{
 
		// convert vertex array to buffer
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(positionData.length);
		positionBuffer.put(positionData);
		positionBuffer.flip();
 
		// convert color array to buffer
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colorData.length);
		colorBuffer.put(colorData);
		colorBuffer.flip();		
 
		// create vertex buffer object (VBO) for vertices
		int positionBufferHandle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
 
		// create VBO for color values
		int colorBufferHandle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
 
		// unbind VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
 
		// create vertex array object (VAO)
		int vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
 
		// assign vertex VBO to slot 0 of VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
 
		// assign vertex VBO to slot 1 of VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);
 
		// unbind VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
 
		return vaoHandle;
	}
	/**
	 * Create Vertex Array Object necessary to pass data to the shader
	 */
	int constructVertexArrayObject()
	{
		// create vertex data 
		float[] positionData = new float[] {
		    	0f,		0f,		0f,
		    	-1f,	0f, 	0f,
		    	0f,		1f,		0f
		};
 
		// create color data
		float[] colorData = new float[]{
				0f,			0f,			1f,
				1f,			0f,			0f,
				0f,			1f,			0f
		};
 
		// convert vertex array to buffer
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(positionData.length);
		positionBuffer.put(positionData);
		positionBuffer.flip();
 
		// convert color array to buffer
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colorData.length);
		colorBuffer.put(colorData);
		colorBuffer.flip();		
 
		// create vertex buffer object (VBO) for vertices
		int positionBufferHandle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
 
		// create VBO for color values
		int colorBufferHandle = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
 
		// unbind VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
 
		// create vertex array object (VAO)
		int vaoHandle = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoHandle);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
 
		// assign vertex VBO to slot 0 of VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
 
		// assign vertex VBO to slot 1 of VAO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);
 
		// unbind VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
 
		return vaoHandle;
	}
 
	/**
	 * main method to run the example
	 */
	public static void main(String[] args) throws LWJGLException
	{
		ShaderExample example = new ShaderExample();
		example.initGl();
		example.run();
	}
}