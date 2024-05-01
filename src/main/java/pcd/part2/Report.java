package pcd.part2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

//TO DO : da modificare con una lista che tenga in memoria tutti i siti con la parola cercata, appena capisco
// sta cosa della ricorsione.

public class Report {
    private String word;
    private HashMap<String, Integer> result;

    public Report(String word, HashMap<String, Integer> result) {
        this.word = word;
        this.result = result;
    }

    /**
     * @Return void,
     * print the result of word research
     * */
    public void logResult() {
        System.out.println("[WORD TO FIND] : " + word);
        System.out.println("[PAGE ANALYZED] : " + result.size());
        for (var entry : result.entrySet()) {
            System.out.println(entry.getKey() + "   ==> " + entry.getValue());
        }
    }


    /**
     * @Return void,
     * Make a file Report.txt about the word report
     * */
    public void getTxtReport() throws IOException {
        FileWriter report = new FileWriter("pcd-2023-2024-master/app/src/main/java/pcd/part2/Report.txt");
        report.write("[WORD TO FIND] : " + word);
        for (var entry : result.entrySet()) {
            report.write(entry.getKey() + " ==> " + entry.getValue() + "\n" );
        }
        report.close();
    }
}
