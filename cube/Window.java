import java.awt.*;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

class Window extends JFrame {
    JTextArea textArea;

    public Window() {

        setTitle("");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1500);
        setLocationRelativeTo(null);

        StyleConstants.setBackground(new SimpleAttributeSet(), Color.black);
        StyleConstants.setLineSpacing(new SimpleAttributeSet(), 0);

        textArea = new JTextArea(100,150);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN,6));
        textArea.setLineWrap(false);
        textArea.setEditable(false);
        textArea.setFocusable(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void UpdateFrame(String frame){
        textArea.setText(frame);
    }
}