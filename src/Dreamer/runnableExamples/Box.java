package Dreamer.runnableExamples;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

/**
* The vertex and fragment shaders are setup when the box object is
* constructed. They are applied to the GL state prior to the box
* being drawn, and released from that state after drawing.
* @author Stephen Jones
*/
public class Box {
    
    /*
    * if the shaders are setup ok we can use shaders, otherwise we just
    * use default settings
    */
    private boolean useShader;
    
    /*
    * program shader, to which is attached a vertex and fragment shaders.
    * They are set to 0 as a check because GL will assign unique int
    * values to each
    */
    private int program=0;

    public Box(){
    	int vertShader = 0, fragShader = 0;
    	
    	try {
            vertShader = createShader("src/Dreamer/runnableExamples/tutorial.vertex",ARBVertexShader.GL_VERTEX_SHADER_ARB);
            fragShader = createShader("src/Dreamer/runnableExamples/tutorial.fragment",ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
    	}
    	catch(Exception exc) {
    		exc.printStackTrace();
    		return;
    	}
    	finally {
    		if(vertShader == 0 || fragShader == 0)
    			return;
    	}
    	
    	program = ARBShaderObjects.glCreateProgramObjectARB();
    	
    	if(program == 0)
    		return;
        
        /*
        * if the vertex and fragment shaders setup sucessfully,
        * attach them to the shader program, link the sahder program
        * (into the GL context I suppose), and validate
        */
        ARBShaderObjects.glAttachObjectARB(program, vertShader);
        ARBShaderObjects.glAttachObjectARB(program, fragShader);
        
        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(program));
            return;
        }
        
        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
        	System.err.println(getLogInfo(program));
        	return;
        }
        
        useShader = true;
    }
    
    /*
    * If the shader was setup succesfully, we use the shader. Otherwise
    * we run normal drawing code.
    */
    public void draw(){
        if(useShader)
            ARBShaderObjects.glUseProgramObjectARB(program);
        
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -10.0f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);
        GL11.glVertex3f(-1.0f, -1.0f,1.0f);
        GL11.glColor3f(1.0f, 0.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);
        GL11.glEnd();
        
        //release the shader
        if(useShader)
        	ARBShaderObjects.glUseProgramObjectARB(0);

    }
    
    /*
    * With the exception of syntax, setting up vertex and fragment shaders
    * is the same.
    * @param the name and path to the vertex shader
    */
    private int createShader(String filename, int shaderType) throws Exception {
    	int shader = 0;
    	try {
	        shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
	        
	        if(shader == 0)
	        	return 0;
	        //DEBUG
	        ContextCapabilities caps = GLContext.getCapabilities();
	        if ( caps.OpenGL20 )
	            System.out.println("OpenGL version 2.0 supported");
	        if ( caps.GL_ARB_vertex_shader )
	            System.out.println("ARB_vertex_shader extension supported");
	        if ( caps.GL_ARB_fragment_shader )
	            System.out.println("ARB_fragment_shader extension supported");
	        //\DEBUG
	        ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
	        ARBShaderObjects.glCompileShaderARB(shader);
	        
	        if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
	            throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
	        
	        return shader;
    	}
    	catch(Exception exc) {
    		ARBShaderObjects.glDeleteObjectARB(shader);
    		throw exc;
    	}
    }
    
    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
    
    private String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        
        FileInputStream in = new FileInputStream(filename);
        
        Exception exception = null;
        
        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            
            Exception innerExc= null;
            try {
            	String line;
                while((line = reader.readLine()) != null)
                    source.append(line).append('\n');
            }
            catch(Exception exc) {
            	exception = exc;
            }
            finally {
            	try {
            		reader.close();
            	}
            	catch(Exception exc) {
            		if(innerExc == null)
            			innerExc = exc;
            		else
            			exc.printStackTrace();
            	}
            }
            
            if(innerExc != null)
            	throw innerExc;
        }
        catch(Exception exc) {
        	exception = exc;
        }
        finally {
        	try {
        		in.close();
        	}
        	catch(Exception exc) {
        		if(exception == null)
        			exception = exc;
        		else
					exc.printStackTrace();
        	}
        	
        	if(exception != null)
        		throw exception;
        }
        
        return source.toString();
    }
}
