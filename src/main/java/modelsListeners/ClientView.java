package modelsListeners;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ClientView {

    private JFrame frame;
    private JPanel logPanel;
    private JPanel buttonsPanel;
    private JTextArea logTextArea;
    private JButton clearLog;
    private final JPanel framePanel;

    public ClientView() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("SMTPServer");
        framePanel = new JPanel();
        frame.setContentPane(framePanel);
        framePanel.setLayout(new BoxLayout(framePanel, BoxLayout.X_AXIS));

        logPanel = new JPanel();
        logPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        framePanel.add(logPanel);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);

        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        logPanel.add(logScrollPane);
        clearLog = new JButton("Clear log");
        clearLog.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                logTextArea.setText("");
            }
        });
        clearLog.setAlignmentX(0.5f);
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        logPanel.add(clearLog);
        frame.setVisible(true);
    }

    public void setMesage(String message) {
        logTextArea.append(message);
    }
}