package jp.zappa2010.tiv.ui;

import jp.zappa2010.tiv.Application;

import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageWindow extends JFrame {
    private final Application application;
    private final ImagePanel imagePanel;

    public ImageWindow(Application app, String title) throws HeadlessException {
        this.application = app;

        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setLocationByPlatform(true);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        this.imagePanel = new ImagePanel();
        add(imagePanel);
        DragListener dragListener = new DragListener(this);
        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
        addKeyListener(new CursorLister(this));

        JSlider slider = createSlider();
        slider.setVisible(false);
        add(slider, BorderLayout.SOUTH);
        MouseOverListener mouseOverListener = new MouseOverListener(this, slider);
        addMouseListener(mouseOverListener);
        Timer timer = new Timer(250, mouseOverListener); // mouseExited() が発火しない場合があるので...
        timer.setRepeats(true);
        timer.start();

        if (app.isMacOSX()) {
            JMenuBar menuBar = createMenuBar();
            setJMenuBar(menuBar);
        }

        JPopupMenu popup = createPopup();
        addMouseListener(new PopupListener(popup));
    }

    private JSlider createSlider() {
        JSlider slider = new JSlider(0, 255);
        slider.setValue((int) (slider.getMaximum() * imagePanel.getOpacity()));
        slider.addChangeListener((ChangeEvent e) -> {
            imagePanel.setOpacity(slider.getValue() / ((float) slider.getMaximum()));
        });

        return slider;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open...", 'O');
        open.setAccelerator(KeyStroke.getKeyStroke('O', getToolkit().getMenuShortcutKeyMask()));
        open.addActionListener((e) -> {
            application.open();
        });
        file.add(open);
        menuBar.add(file);

        JMenu windowMenu = new JMenu("Window");
        windowMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                application.getWindows().forEach((window) -> {
                    JCheckBoxMenuItem mi = new JCheckBoxMenuItem(window.getTitle());
                    mi.addActionListener((we) -> {
                        window.requestFocus();
                    });
                    mi.setState(window.hasFocus());
                    windowMenu.add(mi);
                });
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                windowMenu.removeAll();
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                // nop
            }
        });
        menuBar.add(windowMenu);

        return menuBar;
    }

    private JPopupMenu createPopup() {
        JPopupMenu popup = new JPopupMenu();

        JMenuItem open = new JMenuItem("Open...", 'O');
        open.addActionListener((ActionEvent e) -> {
            this.application.open();
        });
        popup.add(open);

        JMenuItem close = new JMenuItem("Close", 'C');
        close.addActionListener((ActionEvent e) -> {
            setVisible(false);
            dispose();
        });
        popup.add(close);

        if (!application.isMacOSX()) {
            popup.addSeparator();

            JMenuItem quit = new JMenuItem("Quit", 'Q');
            quit.addActionListener((ActionEvent e) -> {
                System.exit(0);
            });
            popup.add(quit);
        }

        return popup;
    }

    public void setImage(ImageInputStream is) throws IOException {
        imagePanel.setImage(is);
        pack();
    }

    /**
     * Window 移動
     */
    private static class DragListener extends MouseAdapter {
        private final Window window;
        private final Point dragStart = new Point();

        public DragListener(Window window) {
            this.window = window;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            dragStart.setLocation(e.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point locationOnScreen = e.getLocationOnScreen();
            window.setLocation(locationOnScreen.x - dragStart.x, locationOnScreen.y - dragStart.y);
        }
    }
    private static class CursorLister extends KeyAdapter {
        private final Window window;

        public CursorLister(Window window) {
            this.window = window;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            Point point = window.getLocation();
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_H:
                    point.x --;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_J:
                    point.y ++;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_L:
                    point.x ++;
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_K:
                    point.y --;
                    break;
            }

            window.setLocation(point);
        }
    }

    private static class MouseOverListener extends MouseAdapter implements ActionListener {
        private final Container container;
        private final java.util.List<Component> targets = new ArrayList<>();

        public MouseOverListener(Container container, Component... targets) {
            this.container = container;
            this.targets.addAll(Arrays.asList(targets));
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            showOrHide();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            showOrHide();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showOrHide();
        }

        private void showOrHide() {
            Point p = container.getMousePosition();
            boolean contains = (p == null)
                    ? false
                    : container.contains(p);

            targets.forEach(c -> {
                c.setVisible(contains);
            });
        }
    }

    /**
     * popup
     */
    private static class PopupListener extends MouseAdapter {
        private final JPopupMenu popup;

        public PopupListener(JPopupMenu popup) {
            this.popup = popup;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

}
