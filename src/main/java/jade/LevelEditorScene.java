package jade;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private int vertexID, fragmentID, shaderProgram;
    private float[] vertexArray = {
            // position               // color                  // UV Coordinates
            100f,   0f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f,     1, 1, // Bottom right 0
            0f, 100f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f,     0, 0, // Top left     1
            100f, 100f, 0.0f ,      1.0f, 0.0f, 1.0f, 1.0f,     1, 0, // Top right    2
            0f,   0f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f,     0, 1  // Bottom left  3
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
    private Texture testTexture;

    GameObject testObj;
    private boolean firstTime = false;

    public LevelEditorScene() {

    }
    @Override
    public void init() {
        System.out.println("Creating 'test object'.");
        this.testObj = new GameObject("test object");
        this.testObj.addComponent(new SpriteRenderer());
        this.addGameObjectToScene(this.testObj);

        this.camera = new Camera(new Vector2f(-200, -300));
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLink();
        this.testTexture = new Texture("assets/images/testImage.png");

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

        // Add the vertex attribute pointers (3 pos, 4 color, 2 for texture co-ords).
        int positionSize = 3;
        int colourSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colourSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colourSize, GL_FLOAT, false, vertexSizeBytes,
                positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false,
                vertexSizeBytes, (positionSize + colourSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {

        defaultShader.use();

        defaultShader.uploadTexture("TEXTURE_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

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

        if (!firstTime) {
            System.out.println("Creating gameObject!");
            GameObject go = new GameObject("Game test 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
        }

        for (GameObject g: this.gameObjects) {
            g.update(dt);
        }

    }
}