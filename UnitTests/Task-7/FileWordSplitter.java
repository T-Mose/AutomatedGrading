
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class FileWordSplitter {

    private ArrayList<String> words = new ArrayList<>();

    public FileWordSplitter(String filename) {

        try {
            // Attempt to open a text file
            BufferedReader file = new BufferedReader(new FileReader(filename));

            // Try to read the first line of the file
            String line = file.readLine();
            String[] noFilter;
            

            // Keep reading while there are lines left
            while (line != null) {
                noFilter = line.split(" ");
                for (String elem : noFilter) {
                if(!elem.isEmpty()){
                    words.add(elem);
                }
            }
                
                line = file.readLine();
            }

            // Don't forget to close the file!
            file.close();
        // Handle any errors that come up, such as the file not existing
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            // Exit the program    
            System.exit(1);
        }
    }

    public ArrayList<String> getWords() {
        return words;
    }


    public static void main(String[] args) {
        FileWordSplitter splitter = new FileWordSplitter("hamlet.txt");
        ArrayList<String> hamletWords = splitter.getWords();
        if(!hamletWords.isEmpty()){
        System.out.println(hamletWords.get(1)); // Get the fourth word in hamlet.txt
        }
    }
}