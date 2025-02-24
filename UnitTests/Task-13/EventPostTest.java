import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

public class EventPostTest {

    private EventPost eventPost;
    private NewsFeed newsFeed;

    // For capturing System.out
    private PrintStream originalOut;
    private ByteArrayOutputStream testOut;

    @Before
    public void setUp() {
        // Redirect System.out to a buffer for each test
        originalOut = System.out;
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        // Use the constructor that has four parameters, including date
        LocalDate date = LocalDate.of(2025, 1, 1);
        eventPost = new EventPost("Alice", "Birthday Party", "Paris", date);
        newsFeed = new NewsFeed();
    }

    @After
    public void tearDown() {
        // Restore original System.out after each test
        System.setOut(originalOut);
    }

    @Test
    public void eventPostShouldExtendPost() {
        // Basic inheritance check
        assertTrue("EventPost should inherit from Post",
            eventPost instanceof Post);
    }

    @Test
    public void addingEventPostToNewsFeedShouldPrintIt() {
        // Add the event post to the NewsFeed and display
        newsFeed.addPost(eventPost);
        newsFeed.show();

        // Capture what's printed
        String output = testOut.toString();

        // Verify basic info: no Hamcrest needed, just plain String checks
        assertTrue("Output should mention 'Alice'.", output.contains("Alice"));
        assertTrue("Output should mention 'Birthday Party'.", output.contains("Birthday Party"));
        assertTrue("Output should mention 'Paris'.", output.contains("Paris"));
        // This checks if the date was printed (e.g. "2025-01-01")
        assertTrue("Output should mention '2025-01-01'.", output.contains("2025-01-01"));
    }
}
