package jade;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

/**
 * Scene used to create and edit levels.
 */
public class LevelEditorScene extends Scene {

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(0,0));

        int xOffset = 10;
        int yOffset= 10;

        float totalWidth = (float)(600 - xOffset * 2);
        float totalHeight = (float)(300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;
        float padding = 2;

        for (int x=0; x < 130; x++) {
            for (int y=0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX) + (padding * x);
                float yPos = yOffset + (y * sizeY) + (padding * y);

                GameObject g = new GameObject("Obj " + x + "/" + y,
                        new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));

                g.addComponent(new SpriteRenderer(new Vector4f(xPos / totalHeight,
                        yPos / totalWidth, 1, 1)));

                this.addGameObjectToScene(g);
            }
        }
        loadResources();
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        System.out.println("FPS: " + (1.0f/dt));

        for (GameObject g: this.gameObjects) {
            g.update(dt);
        }

        this.renderer.render();
    }
}