// REPOBEE-SANITIZER-SHRED
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An abstract class for creating a file for use with classes reading text from a file
 * This is done to ensure that we have full control of the files being used
 * @author Gabriel Skoglund
 */
public abstract class TextFileTest {

    protected static final String HAMLET_EXCERPT = """
        Nay good my lord for mine ease in good faith
        Sir here is newly come to court Laertes believe
        me an absolute gentleman full of most excellent
        differences of very soft society and great showing
        indeed to speak feelingly of him he is the card or
        calendar of gentry for you shall find in him the
        continent of what part a gentleman would see
        Sir his definement suffers no perdition in you
        though I know to divide him inventorially would
        dizzy the arithmetic of memory and yet but yaw
        neither in respect of his quick sail But in the
        verity of extolment I take him to be a soul of
        great article and his infusion of such dearth and
        rareness as to make true diction of him his
        semblable is his mirror and who else would trace
        him his umbrage nothing more
        Your lordship speaks most infallibly of him
        The concernancy sir why do we wrap the gentleman
        in our more rawer breath
        """;

    protected static final Path EMPTY_FILEPATH = Paths.get("test-empty-file.txt");
    protected static final Path HAMLET_FILEPATH = Paths.get("test-hamlet-excerpt.txt");

    protected static final int HAMLET_EXCERPT_WORD_COUNT = 163;
    protected static final int HAMLET_EXCERPT_UNIQUE_WORD_COUNT = 106;

    @BeforeClass
    public static void setUpClass() throws IOException {
        Files.createFile(EMPTY_FILEPATH);
        Files.createFile(HAMLET_FILEPATH);
        Files.writeString(HAMLET_FILEPATH, HAMLET_EXCERPT);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        Files.delete(EMPTY_FILEPATH);
        Files.delete(HAMLET_FILEPATH);
    }
}