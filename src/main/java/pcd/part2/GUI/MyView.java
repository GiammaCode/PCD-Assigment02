package pcd.part2.GUI;

import pcd.part2.GUI.vt.CrawlerVT;
import pcd.part2.GUI.ev.CrawlerVertX;
import pcd.part2.cli.rx.CrawlerRx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static pcd.part2.GUI.vt.CrawlerVT.getWordOccurrences;

public class MyView extends JFrame implements ModelObserver{
    private JTextField linkField;
    private String test = "";

    CrawlerVertX crow = new CrawlerVertX();
     //CrawlerVT crow= new CrawlerVT();
    // CrawlerRx crow = new CrawlerRx();


    private HashMap<String,Integer> result = new HashMap<>();
    private JTextField depthField;
    private JTextField wordField;
    private JButton stopButton;
    private JButton countButton;
    private JTextArea resultField;

    public MyView(MyController controller){
        setTitle("Assigment02 PCD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel linkLabel = new JLabel("Link:");
        inputPanel.add(linkLabel);

        linkField = new JTextField("https://www.akwabaforli.com/",30);
        inputPanel.add(linkField);

        JLabel depthLabel = new JLabel("Depth:");
        inputPanel.add(depthLabel);

        depthField = new JTextField(5);
        inputPanel.add(depthField);

        JLabel wordLabel = new JLabel("Word:");
        inputPanel.add(wordLabel);

        wordField = new JTextField(10);
        inputPanel.add(wordField);



        stopButton = new JButton("Stop");

        countButton = new JButton("Count");


        countButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //controller.processEvent(e.getActionCommand());
                    String link = linkField.getText();
                    int depth = Integer.parseInt(depthField.getText());
                    String word = wordField.getText();
                    crow.getWordOccurrences(link,word,depth);
                    result = crow.getMap();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        inputPanel.add(countButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        inputPanel.add(stopButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        resultField = new JTextArea();
        //resultField.setSize(300,200);
        resultField.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultField);
        panel.add(scrollPane,BorderLayout.CENTER);
        add(panel);
        setVisible(true);
    }
    @Override
    public void modelUpdated(MyModel model) {
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            test =  test + "\n"+ entry.getKey() + " => " + entry.getValue();
            result.remove(entry.getKey());
            resultField.setText(test); // Stampa la coppia chiave-valore
        }
    }
}