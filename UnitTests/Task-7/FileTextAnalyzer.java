
import java.util.HashMap;

class FileTextAnalyzer{
    private FileWordSplitter words;
    private HashMap<String, Integer> wordOccurrences;

    /**
     * Constructor for setting up a hashmap of all word occurrences 
     * @param filename The name of the file to be analyzed
     */
    public FileTextAnalyzer(String filename){
        words = new FileWordSplitter(filename);
        wordOccurrences = new HashMap<>();
        if (words.getWords() != null) {
        for (String word : words.getWords()) {
            String wordLowerCase = word.toLowerCase();
            wordOccurrences.merge(wordLowerCase, 1, Integer::sum);
        }
    }
    }

    /**
     * @return total amount of words
     */
   public int wordCount(){
    return words.getWords() != null ? words.getWords().size() : 0;
   } 

   /**
    * @return amount occurrences of each word
    */
    public HashMap<String, Integer> getWordOccurrences() {
        return wordOccurrences;
    }

    /**
    * @param word The word whose occurrences are to be counted
    * @return occurrences of a certain word (Case-insensitive)
     */
   public int occurrencesOf(String word) {
    return wordOccurrences.getOrDefault(word.toLowerCase(), 0);
   }

   /**
    * Returns the frequency of a word in the file as a proportion of the total word count.(occurrences of word / total words).
    * @param word The word whose occurrences are to be counted.
    * @return frequency of word in percent
    */
   public double frequencyOf(String word){
    return (double)occurrencesOf(word)/Math.max(1, wordCount());
   }

   /**
    * @return amount of unique words
    */
   public int uniqueWordCount(){
    return getWordOccurrences().size();
   }

   public static void main(String[] args) {
    FileTextAnalyzer test = new FileTextAnalyzer("hamlet.txt");
    System.out.println("Total word count: " + test.wordCount());
    System.out.println("Occurrences of 'test': " + test.occurrencesOf("test"));
    System.out.println("Frequency of 'apa': " + test.frequencyOf("apa"));
    System.out.println("Unique word count: " + test.uniqueWordCount());
   } 
}