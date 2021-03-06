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

/**
 * Класс визуального представлении сессии.
 *
 * @author Климашевич Николай, 621702
 * @version 1.0
 */
public class ClientView {

    private final JFrame frame;
    private final JPanel logPanel;
    private JTextArea logTextArea;
    private final JButton clearLog;
    private final JPanel framePanel;

     /**
     * Инициализация необходимых структур Swing для GUI
     *     
     */
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

     /**
     * Запись сообщения в представление
     * @param message - записываемое сообщение
     *     
     */
    public void setMesage(String message) {
        logTextArea.append(message);
    }
}