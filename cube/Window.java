import java.awt.*;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

class Window extends JFrame {
    JTextArea textArea;
    JTextArea infoArea;

    public Window() {

        setTitle("");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 900);
        setLocationRelativeTo(null);
        

        StyleConstants.setBackground(new SimpleAttributeSet(), Color.black);
        StyleConstants.setLineSpacing(new SimpleAttributeSet(), 0);

        textArea = new JTextArea(20,50);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN,6));
        textArea.setLineWrap(false);
        textArea.setEditable(false);
        textArea.setFocusable(true);

        infoArea = new JTextArea(1000, 30);
        infoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN,12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(textArea, BorderLayout.WEST);
        add(infoArea, BorderLayout.CENTER);
    }

    public void UpdateFrame(String frame){
        textArea.setText(frame);
    }

    public void UpdateInfo(Main.Vertex v, Main.Angle a, Main.Vertex[] vertices, Main.Polygon[] polygons){
        String frame = "Camera\nposition: "+v.x+", "+v.y+", "+v.z+'\n';
        frame += "rotation: "+a.x+", "+a.y+", "+a.z+"\nVertices:\n";
        for (int i = 0; i < vertices.length; i++) {
            frame+="0. position: "+vertices[i].x+", "+vertices[i].y+", "+vertices[i].z+'\n';
        }
        frame+="Polygons\n";
        for (int i = 0; i < polygons.length; i++) {
            frame+="0. connects: "+polygons[i].p1+", "+polygons[i].p2+", "+polygons[i].p3+'\n';
        }
        infoArea.setText(frame);
    }
}