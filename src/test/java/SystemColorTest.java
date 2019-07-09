
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SystemColorTest {
    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            try {
                run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void run() throws Exception {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        frame.add(panel);

        GridLayout grid = new GridLayout(0, 2);
        panel.setLayout(grid);
        Class<?> clz = Class.forName("java.awt.SystemColor");
        for (Field field : clz.getDeclaredFields()) {
            if (!Modifier.isPublic(field.getModifiers())) {
                continue;
            }

            String name = field.getName();
            Object o = field.get(null);
            if (!(o instanceof SystemColor)) {
                continue;
            }
            SystemColor color = (SystemColor) o;

            panel.add(new JLabel(name));
            JPanel colorPanel = new JPanel();
            colorPanel.setSize(30, 30);
            colorPanel.setBackground(color);
            panel.add(colorPanel);
        }

        frame.pack();
        frame.setVisible(true);
    }
}
