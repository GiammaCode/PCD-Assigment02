package pcd.part2.virtualThread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindSubLinksTask implements Runnable {
    String link;
    List<String> subLinks;
    Pattern pattern;

    public FindSubLinksTask(String link, List<String> sublinks, Pattern pattern) {
        this.link = link;
        this.subLinks = sublinks;
        this.pattern = pattern;
    }

    @Override
    public void run() {
        String line;
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(this.link);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("[FIND SUBLINKS TASK] impossible to connect :" + link);
        }

        Matcher m = this.pattern.matcher(content);
        while (m.find()) {
            this.subLinks.add(m.group());
        }
    }
}
