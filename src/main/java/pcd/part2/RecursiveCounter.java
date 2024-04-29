package pcd.part2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecursiveCounter
{
    public static Report getWordOccurrences(String entryPoint, String word, int depth) throws IOException {
        int wordCount = 0;
        HashMap<String, Integer> result = new HashMap<>();
        try {
            URLConnection urlConnection = new URI(entryPoint).toURL().openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            wordCount = 0;

            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String w : words) {
                    wordCount = w.toLowerCase().equals(word) ? wordCount + 1 : wordCount;
                }
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put(entryPoint,wordCount);

        return new Report(word, result);
    }


    //far ritornare una Lista<String>
    public static void getAllLinks(String entryPoint){
        String regex="\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        StringBuilder content = new StringBuilder();
        try
        {
            URL url = new URL(entryPoint);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Matcher m = pattern.matcher(content);
        while(m.find()) {
            System.out.println("FOUND: " + m.group());
        }
    }
}