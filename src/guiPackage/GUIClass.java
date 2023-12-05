package guiPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIClass implements ActionListener {
    private JPanel grandPanel;
    private JLabel textLabel;

    public JPanel getGrandPanel() {
        return grandPanel;
    }

    public JLabel getTextLabel() {
        return textLabel;
    }

    public void setText(String textie) {
        getTextLabel().setText(textie);
    }

    public GUIClass() {
        JFrame frame = new JFrame();
        this.grandPanel = new JPanel();
        this.textLabel = new JLabel();
        getTextLabel().setFont(new Font("Consolas", Font.PLAIN, 16));
        getGrandPanel().setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        getGrandPanel().setLayout(new GridLayout(10, 1));
        getGrandPanel().add(getTextLabel());

        frame.add(getGrandPanel(), BorderLayout.CENTER);
        frame.setTitle("SA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setVisible(true);
        frame.pack();
        this.setText("ASDA");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
