//REPOBEE-SANITIZER-SHRED
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RectangleTest {

    public static final int RECT_WIDTH = 40;
    public static final int RECT_HEIGHT = 50;
    public static final int SQUARE_SIDE = 20;

    public static final double DIAGONAL_ERROR_MARGIN = 0.1;

    private static Rectangle rectangle;
    private static Rectangle square;

    @BeforeClass
    public static void setUp() {
        rectangle = new Rectangle();
        rectangle.setWidth(RECT_WIDTH);
        rectangle.setHeight(RECT_HEIGHT);

        square = new Rectangle();
        square.setWidth(SQUARE_SIDE);
        square.setHeight(SQUARE_SIDE);
    }

    // Test for Rectangle area that works for both int and double
    @Test
    public void testRectangleArea() {
        Object actualArea = rectangle.area(); // Can be int or double
        int expectedArea = RECT_WIDTH * RECT_HEIGHT;
        
        if (actualArea instanceof Integer) {
            assertEquals(expectedArea, (int) actualArea);
        } else if (actualArea instanceof Double) {
            assertEquals(expectedArea, (double) actualArea, 0.0001);
        }
    }

    // Test for Square area that works for both int and double
    @Test
    public void testSquareArea() {
        Object actualArea = square.area(); // Can be int or double
        int expectedArea = SQUARE_SIDE * SQUARE_SIDE;
        
        if (actualArea instanceof Integer) {
            assertEquals(expectedArea, (int) actualArea);
        } else if (actualArea instanceof Double) {
            assertEquals(expectedArea, (double) actualArea, 0.0001);
        }
    }

    // Test for Rectangle diagonal length that works for both int and double
    @Test
    public void testRectangleDiagonalLength() {
        double expectedDiagonal = Math.sqrt(RECT_WIDTH * RECT_WIDTH + RECT_HEIGHT * RECT_HEIGHT);
        Object actualDiagonal = rectangle.diagonalLength(); // Can be int or double

        if (actualDiagonal instanceof Integer) {
            assertEquals((int) expectedDiagonal, (int) actualDiagonal);
        } else if (actualDiagonal instanceof Double) {
            assertEquals(expectedDiagonal, (double) actualDiagonal, DIAGONAL_ERROR_MARGIN);
        }
    }

    // Test for Square diagonal length that works for both int and double
    @Test
    public void testSquareDiagonalLength() {
        double expectedDiagonal = Math.sqrt(SQUARE_SIDE * SQUARE_SIDE * 2);
        Object actualDiagonal = square.diagonalLength(); // Can be int or double

        if (actualDiagonal instanceof Integer) {
            assertEquals((int) expectedDiagonal, (int) actualDiagonal);
        } else if (actualDiagonal instanceof Double) {
            assertEquals(expectedDiagonal, (double) actualDiagonal, DIAGONAL_ERROR_MARGIN);
        }
    }

    // Test for Rectangle isSquare (this should not require changes)
    @Test
    public void testRectangleIsSquare() {
        assertFalse(rectangle.isSquare());
    }

    // Test for Square isSquare (this should not require changes)
    @Test
    public void testSquareIsSquare() {
        assertTrue(square.isSquare());
    }
}
