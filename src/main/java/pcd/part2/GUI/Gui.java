package pcd.part2.GUI;

import pcd.part2.cli.vt.RecursiveWordCountTask;
import pcd.part2.MyMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Gui extends JFrame {
    private JTextField linkField;
    private JTextField depthField;
    private JTextField wordField;
    private JButton countButton;
    private JTextField resultField;

    public Gui() {
        setTitle("Assigment02 PCD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel linkLabel = new JLabel("Link:");
        inputPanel.add(linkLabel);

        linkField = new JTextField(30);
        inputPanel.add(linkField);

        JLabel depthLabel = new JLabel("Depth:");
        inputPanel.add(depthLabel);

        depthField = new JTextField(5);
        inputPanel.add(depthField);

        JLabel wordLabel = new JLabel("Word:");
        inputPanel.add(wordLabel);

        wordField = new JTextField(10);
        inputPanel.add(wordField);

        countButton = new JButton("Count");
        countButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String link = linkField.getText();
                int depth = Integer.parseInt(depthField.getText());
                String word = wordField.getText();
                try {
                    HashMap<String, Integer> result = getWordOccurrences(link, depth, word);
                    for (Map.Entry<String, Integer> entry : result.entrySet()) {
                        resultField.setText(entry.getKey() + " => " + entry.getValue()); // Stampa la coppia chiave-valore
                    }


                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        inputPanel.add(countButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        resultField = new JTextField();
        resultField.setEditable(false);
        panel.add(resultField, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    // Placeholder method for counting occurrences
    private HashMap<String,Integer> getWordOccurrences(String link, int depth, String word) throws InterruptedException {
        HashMap<String, Integer> result = new HashMap<>();
        String regex = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        Pattern pattern = Pattern.compile(regex);
        MyMonitor monitor = new MyMonitor();

        Thread vt = Thread.ofVirtual().unstarted(new RecursiveWordCountTask(link, word, depth, result,pattern,monitor));
        vt.start();
        vt.join();

        return result;
    }

}
