package ru.reactiveturtle.game.engine.base;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.material.Texture;
import ru.reactiveturtle.game.engine.model.Model;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {
    protected static final String SQUARE_VERTEX_SHADER = GameContext.RESOURCE_PATH + "shader/square_vertex_shader.glsl";
    protected static final String SQUARE_FRAGMENT_SHADER = GameContext.RESOURCE_PATH + "shader/square_fragment_shader.glsl";
    protected static final String MODEL_VERTEX_SHADER = GameContext.RESOURCE_PATH + "shader/model_vertex_shader.glsl";
    protected static final String MODEL_FRAGMENT_SHADER = GameContext.RESOURCE_PATH + "shader/model_fragment_shader.glsl";
    protected final static String SHADOW_VERTEX_SHADER = GameContext.RESOURCE_PATH + "shader/shadow_vertex_shader.glsl";
    protected final static String SHADOW_FRAGMENT_SHADER = GameContext.RESOURCE_PATH + "shader/shadow_fragment_shader.glsl";

    private final int programID;

    private int vertexShaderID;

    private int fragmentShaderID;

    private String vertexShaderFile, fragmentShaderFile;

    protected boolean isBinded = false;

    protected Shader(String vertexShaderFile, String fragmentShaderFile) {
        programID = glCreateProgram();
        if (programID == 0) {
            System.err.println("Could not create Shader");
        }

        this.vertexShaderFile = vertexShaderFile;
        this.fragmentShaderFile = fragmentShaderFile;
    }

    public void create() {
        createVertexShader();
        createFragmentShader();

        bindAllAttributes();

        link();

        getAllUniforms();
    }

    private void createVertexShader() {
        if (vertexShaderFile != null)
            vertexShaderID = createShader(vertexShaderFile, GL_VERTEX_SHADER);
    }


    private void createFragmentShader() {
        if (fragmentShaderFile != null)
            fragmentShaderID = createShader(fragmentShaderFile, GL_FRAGMENT_SHADER);
    }

    private int createShader(String shaderFile, int shaderType) {
        int shaderID = GL20.glCreateShader(shaderType);

        if (shaderID == 0) {
            System.err.println("Error creating " + shaderTypeToString(shaderType) + " shader");
            System.exit(-1);
        }

        GL20.glShaderSource(shaderID, readFile(shaderFile));

        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Error compiling " + shaderTypeToString(shaderType) + " shader - " + GL20.glGetShaderInfoLog(shaderID));
            System.exit(-1);
        }

        GL20.glAttachShader(programID, shaderID);

        return shaderID;
    }

    private void link() {
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.err.println("Error: Program Linking - " + glGetProgramInfoLog(programID));
            System.exit(-1);
        }

        if (vertexShaderID != 0) {
            glDetachShader(programID, vertexShaderID);
        }
        if (fragmentShaderID != 0) {
            glDetachShader(programID, fragmentShaderID);
        }

        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Error: Program Validating - " + glGetProgramInfoLog(programID));
            System.exit(-1);
        }
    }

    public void bind() {
        if (!isBinded) {
            glUseProgram(programID);
            isBinded = true;
        }
    }

    public void unbind() {
        glUseProgram(0);
        isBinded = false;
    }

    public void clear() {
        unbind();
        if (programID != 0) {
            glDeleteProgram(programID);
        }
    }

    public abstract void bindAllAttributes();

    protected void bindAttribute(int index, String varName) {
        GL20.glBindAttribLocation(programID, index, varName);
    }

    public abstract void getAllUniforms();

    protected int getUniform(String name) {
        return GL20.glGetUniformLocation(programID, name);
    }

    protected void loadIntUniform(int location, int value) {
        glUniform1i(location, value);
    }

    protected void loadFloatUniform(int location, float value) {
        glUniform1f(location, value);
    }

    void loadFloatArrayUniform(int location, float[] values) {
        glUniform1fv(location, values);
    }

    protected void loadVector3fUniform(int location, Vector3f value) {
        glUniform3f(location, value.x, value.y, value.z);
    }

    void loadVector3fArrayUniform(int location, Vector3f[] values) {
        float[] buffer = new float[values.length * 3];
        for (int i = 0; i < buffer.length; i += 3) {
            Vector3f vector3f = values[(i - i % 3) / 3];
            buffer[i] = vector3f.x;
            buffer[i + 1] = vector3f.y;
            buffer[i + 2] = vector3f.z;

        }
        glUniform3fv(location, buffer);
    }

    protected void loadVector4fUniform(int location, Vector4f value) {
        glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    protected void loadMatrix3fUniform(int location, Matrix3f matrix3f) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        matrix3f.get(buffer);
        glUniformMatrix3fv(location, false, buffer);
        buffer.flip();
    }

    protected void loadMatrix4fUniform(int location, Matrix4f matrix4f) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix4f.get(buffer);
        glUniformMatrix4fv(location, false, buffer);
        buffer.flip();
    }

    public abstract void load(Matrix4f modelMatrix, Mesh mesh);

    private String readFile(String file) {
        StringBuilder string = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                string.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error: Couldn't find file " + new File(file).getAbsolutePath());
            System.exit(-1);
        }
        return string.toString();
    }

    private String shaderTypeToString(int shaderType) {
        switch (shaderType) {
            case GL_VERTEX_SHADER:
                return "'" + vertexShaderFile + "' vertex";
            case GL_FRAGMENT_SHADER:
                return "'" + fragmentShaderFile + "' fragment";
            default:
                return "unknown";
        }
    }
}
