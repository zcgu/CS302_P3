/**
 * Created by zhicheng on 3/16/17.
 */
public class WarpStar {

    private Graphic graphic;

    public WarpStar(float x, float y) {
        graphic = new Graphic("WARP_STAR");
        graphic.setPosition(x, y);
    }

    public String update(int time) {
        graphic.draw();
        return "CONTINUE";
    }

    public void handleNavigation(Taxi taxi) {
        if (GameEngine.isKeyPressed("MOUSE") && taxi.getFuel() > 0 &&
                graphic.isCoveringPosition(GameEngine.getMouseX(), GameEngine.getMouseY())) {
            taxi.setWarp(graphic.getX(), graphic.getY());
        }
    }

}
