package jp.zappa2010.tiv.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageSelectDialog extends JFileChooser {
    public ImageSelectDialog() {
        setDialogTitle("Select image");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images", ImageIO.getReaderFileSuffixes());
        setFileFilter(filter);
    }
}
