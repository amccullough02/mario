package jade;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;
    private float[] vertexArray = {
            // position (x,y,z)                // colour
             0.5f, -0.5f, 0.0f,                1.0f, 0.0f, 0.0f, 1.0f, // Bottom right. 0
            -0.5f,  0.5f, 0.0f,                0.0f, 1.0f, 0.0f, 1.0f, // Top left.     1
             0.5f,  0.5f, 0.0f,                0.0f, 0.0f, 1.0f, 1.0f, // Top right.    2
            -0.5f, -0.5f, 0.0f,                1.0f, 1.0f, 0.0f, 1.0f, // Bottom left.  3
    };

    // IMPORTANT: Must be in counter-clockwise order.
    // Personal note: Just remember that it's all triangles.
    private int[] elementArray = {
            /*
                x       x


                x       x
             */
            2,1,0, // Top right triangle.
            0,1,3 // Bottom left triangle.
    };

    private int vaoId, vboId, eboId;

    public LevelEditorScene() {

    }
    @Override
    public void init() {
        // Compile and link shaders.

        // Load and compile the vertex shader.
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass in shader source code.
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        // Check for errors in compilation progress.
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // Load and compile the fragment shader.
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass in shader source code.
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        // Check for errors in compilation progress.
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link shaders and check for errors.
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        // Check for linking errors.
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tLinking shaders failed.");
            System.out.println(glGetProgramInfoLog(fragmentID, len));
            assert false : "";
        }

        // VAO, VBO, EBO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create a float buffer of vertices.
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO (upload vertex buffer).
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload.
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers (3 pos, 4 color).
        int positionSize = 3;
        int colourSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colourSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colourSize, GL_FLOAT, false, vertexSizeBytes,
                positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        // Bind shader program.
        glUseProgram(shaderProgram);
        // Bind the VAO we're using.
        glBindVertexArray(vaoId);

        // Enable the vertex attribute points.
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind.
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);
    }
}