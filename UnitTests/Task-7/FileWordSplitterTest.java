// REPOBEE-SANITIZER-SHRED
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the {@link FileWordSplitter} class
 * @author Gabriel Skoglund
 */
public class FileWordSplitterTest extends TextFileTest {

    @Test
    public void getWordsGivesExpectedResultForNonEmptyFile() {
        FileWordSplitter hamletSplitter = new FileWordSplitter(HAMLET_FILEPATH.toString());
        String[] excerptWords = HAMLET_EXCERPT.split("\\s+");
        assertEquals(excerptWords.length, hamletSplitter.getWords().size());
        assertArrayEquals(excerptWords, hamletSplitter.getWords().toArray());
    }

    @Test
    public void getWordsGivesExpectedResultForEmptyFile() {
        FileWordSplitter emptyFileSplitter = new FileWordSplitter(EMPTY_FILEPATH.toString());
        assertEquals(0, emptyFileSplitter.getWords().size());
    }
}