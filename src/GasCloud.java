/**
 * Created by zhicheng on 3/18/17.
 */
public class GasCloud {
    private Graphic graphic;
    private float rotationSpeed;
    private boolean shouldRemove;

    public GasCloud(float x, float y, float direction) {
        graphic = new Graphic("GAS_CLOUD");
        graphic.setPosition(x, y);
        graphic.setDirection(direction);

        shouldRemove = false;
        rotationSpeed = 0.001f;
    }

    public void handleFueling(Taxi taxi) {
        if (taxi.checkCollision(graphic)) {
            taxi.addFuel(20);
            shouldRemove = true;
        }
    }

    public boolean	shouldRemove() {
        return shouldRemove;
    }

    public void update(int time) {
        graphic.setDirection(graphic.getDirection() + rotationSpeed * time);
        graphic.draw();
    }
}
