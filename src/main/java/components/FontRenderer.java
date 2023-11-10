package components;

import jade.Component;

/**
 * Class responsible for the rendering of fonts, inherits from component.
 */
public class FontRenderer extends Component {

    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("Found font renderer!");
        }
    }

    @Override
    public void update(float dt) {

    }
}
