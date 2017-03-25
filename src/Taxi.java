
/**
 * Created by zhicheng on 3/16/17.
 */
public class Taxi
{
    public Graphic graphic;
    private float thrusterSpeed;
    private float fuel;

    private float warpSpeed; // initialized to 0.2f
    private boolean isTravellingAtWarp; // initialized to false

    private boolean hasCrashed;

    public Taxi(float x, float y) {
        graphic = new Graphic("TAXI");
        graphic.setPosition(x, y);
        thrusterSpeed = 0.1f;
        fuel = 300000;

        warpSpeed = 0.2f;
        isTravellingAtWarp = false;

        hasCrashed = false;
    }

    public boolean update(int time) {
        if (hasCrashed) {
            graphic.draw();
            return GameEngine.isKeyPressed("SPACE");
        }

        float distance = -1;
        if (fuel >= 0) {
            distance = Math.min(fuel, thrusterSpeed * time);
        }

        if (GameEngine.isKeyHeld("D") || GameEngine.isKeyHeld("RIGHT")) {
            System.out.println("D");
            if (distance > 0) {
                graphic.setX(graphic.getX() + distance);
                fuel -= distance;
            }
            graphic.setDirection(0);
            isTravellingAtWarp = false;
        } else if (GameEngine.isKeyHeld("A") || GameEngine.isKeyHeld("LEFT")) {
            System.out.println("A");
            if (distance > 0) {
                graphic.setX(graphic.getX() - distance);
                fuel -= distance;
            }
            graphic.setDirection((float) Math.PI);
            isTravellingAtWarp = false;
        } else if (GameEngine.isKeyHeld("W") || GameEngine.isKeyHeld("UP")) {
            System.out.println("W");
            if (distance > 0) {
                graphic.setY(graphic.getY() - distance);
                fuel -= distance;
            }
            graphic.setDirection((float) Math.PI / 2);
            isTravellingAtWarp = false;
        } else if (GameEngine.isKeyHeld("S") || GameEngine.isKeyHeld("DOWN")) {
            System.out.println("S");
            if (distance > 0) {
                graphic.setY(graphic.getY() + distance);
                fuel -= distance;
            }
            graphic.setDirection((float) Math.PI * 3 / 2);
            isTravellingAtWarp = false;
        }

        if (isTravellingAtWarp) {
            graphic.setPosition(graphic.getX() + warpSpeed * graphic.getDirectionX() * time,
                    graphic.getY() + warpSpeed * graphic.getDirectionY() * time);
        }

        graphic.setX((graphic.getX() + GameEngine.getWidth()) % GameEngine.getWidth());
        graphic.setY((graphic.getY() + GameEngine.getHeight()) % GameEngine.getHeight());

        graphic.draw();

        return fuel < 0.000001 && GameEngine.isKeyPressed("SPACE");
    }

    public boolean checkCollision(Graphic other) {
        return graphic.isCollidingWith(other);
    }

    public float getFuel() {
        return fuel;
    }

    public void addFuel(float num) {
        fuel += num;
    }

    public void setWarp(float x, float y) {
        graphic.setDirection(x, y);
        isTravellingAtWarp = true;
    }

    public boolean isTravellingAtWarp() {
        return isTravellingAtWarp;
    }

    public void crash() {
        hasCrashed = true;
        graphic.setAppearance("EXPLOSION");
    }

    public boolean hasCrashed() {
        return hasCrashed;
    }
}