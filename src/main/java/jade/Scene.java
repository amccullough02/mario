package jade;

import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract scene class that holds the basic properties of every scene.
 */
public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();

    public Scene() {

    }

    public void init() {

    }

    public void start() {
        for (GameObject g: gameObjects) {
           g.start();
           this.renderer.add(g);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject g) {
        if (!isRunning) {
            gameObjects.add(g);
        } else {
            gameObjects.add(g);
            g.start();
            this.renderer.add(g);
        }
    }

    public abstract void update(float dt);

    public Camera camera() {
        return this.camera;
    }
}
