import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


class Skudd extends GameObject {

    Skudd(double x, double y, double r, Color c) {
        super(new Circle(x, y, r, c));
    }

}
