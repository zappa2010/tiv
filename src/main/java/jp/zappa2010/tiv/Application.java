package jp.zappa2010.tiv;

import jp.zappa2010.tiv.ui.ImageSelectDialog;
import jp.zappa2010.tiv.ui.ImageWindow;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application {

    private List<ImageWindow> windows = new ArrayList<>();

    public static void main(String[] args) {
        if (isMacOSX()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "tiv");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "tiv");
        }

        Application me = new Application();

        SwingUtilities.invokeLater(() -> {
            me.open();
        });
    }

    public static boolean isMacOSX() {
        return System.getProperty("os.name").toUpperCase().contains("OS X");
    }

    public void open() {
        windows.forEach(w -> w.setAlwaysOnTop(false));
        ImageSelectDialog dialog;
        try {
            dialog = new ImageSelectDialog();
            int button = dialog.showOpenDialog(null);
            if (button != JFileChooser.APPROVE_OPTION) {
                if (windows.isEmpty()) {
                    System.exit(0);
                }
                return;
            }
        } finally {
            windows.forEach(w -> w.setAlwaysOnTop(true));
        }

        try {
            showImageWindow(dialog.getSelectedFile());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showImageWindow(File file) throws IOException {
        ImageWindow window = new ImageWindow(this, file.getName());
        windows.add(window);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                windows.remove(e.getWindow());
                if (windows.isEmpty()) {
                    System.exit(0);
                }
            }
        });

        ImageInputStream is = null;
        try {
            is = ImageIO.createImageInputStream(file);
            window.setImage(is);
            window.setVisible(true);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    // nop
                }
            }
        }
    }

    public List<ImageWindow> getWindows() {
        return Collections.unmodifiableList(windows);
    }
}
