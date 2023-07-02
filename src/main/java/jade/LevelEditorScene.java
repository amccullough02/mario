package jade;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private int vertexID, fragmentID, shaderProgram;
    private float[] vertexArray = {
            // position (x,y,z)                // colour
             100.5f, -0.5f, 0.0f,                1.0f, 0.0f, 0.0f, 1.0f, // Bottom right. 0
            -0.5f,  100.5f, 0.0f,                0.0f, 1.0f, 0.0f, 1.0f, // Top left.     1
             100.5f,  100.5f, 0.0f,                0.0f, 0.0f, 1.0f, 1.0f, // Top right.    2
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

    private Shader defaultShader;

    public LevelEditorScene() {

    }
    @Override
    public void init() {
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLink();

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
        camera.position.x -= dt * 50.0f;
        camera.position.y -= dt * 50.0f;
        System.out.println("X: "+ camera.position.x);
        System.out.println("Y: "+ camera.position.y);

        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
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

        defaultShader.detach();
    }
}