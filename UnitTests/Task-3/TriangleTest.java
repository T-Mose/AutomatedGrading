//REPOBEE-SANITIZER-SHRED
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Test.None;

public class TriangleTest {
    @Test(expected = Test.None.class)
    public void validTriangleIsValid() {
        new Triangle(1, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidTriangleIsInvalid() {
        new Triangle(1, 1, 5);
    }

    @Test
    public void testTriangleTypeEquilateral() {
        Triangle t = new Triangle(1, 1, 1);
        assertEquals(t.getTriangleType(), "Equilateral");
    }

    @Test
    public void testTriangleTypeIsosceles() {
        Triangle t = new Triangle(2, 2, 1);
        assertEquals(t.getTriangleType(), "Isosceles");
    }

    @Test
    public void testTriangleTypeScalene() {
        Triangle t = new Triangle(2, 3, 4);
        assertEquals(t.getTriangleType(), "Scalene");
    }

    @Test
    public void testGetArea() {
        double AREA_ERROR_MARGIN = 0.1;
        Triangle t = new Triangle(3, 4, 5);
        Triangle t2 = new Triangle(2, 3, 4);

        assertEquals(6, t.getArea(), AREA_ERROR_MARGIN);

        double s = (2 + 3 + 4) / 2.0;
        assertEquals(Math.sqrt(s * (s-2) * (s-3) * (s-4)), t2.getArea(), AREA_ERROR_MARGIN);
    }
}