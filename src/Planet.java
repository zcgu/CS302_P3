import java.util.ArrayList;
import java.util.Random;

/**
 * Created by zhicheng on 3/23/17.
 */
public class Planet {
    private Graphic graphic;
    private boolean isDestination;

    public Planet(float x, float y) {
        graphic = new Graphic("PLANET");
        graphic.setPosition(x, y);

        isDestination = false;
    }

    public Planet(Random rng, ArrayList<Planet> planets) {
        graphic = new Graphic("PLANET");
        boolean flag;

        do {
            graphic.setPosition(rng.nextInt(GameEngine.getWidth()), rng.nextInt(GameEngine.getHeight()));

            flag = false;
            for (Planet planet: planets) if (graphic.isCollidingWith(planet.graphic)) flag = true;
        } while (flag);

    }

    public void update(int time) {
        graphic.draw();
    }

    public boolean handleLanding(Taxi ship) {
        if (ship.checkCollision(graphic) && ship.isTravellingAtWarp()) {
            ship.crash();
        } else if (isDestination && ship.checkCollision(graphic)) {
            return true;
        }

        return false;
    }

    public void setDestination(boolean isDestination) {
        this.isDestination = isDestination;

        if (isDestination) graphic.setAppearance("DESTINATION");
        else graphic.setAppearance("PLANET");
    }
}
