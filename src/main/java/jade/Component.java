package jade;

/**
 * Abstract component that forms the basis of the Entity-Component architecture.
 */
public abstract class Component {

    public GameObject gameObject = null;

    public void start() {

    }

    public abstract void update(float dt);
}
