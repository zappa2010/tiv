package jp.zappa2010.tiv.ui;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private float opacity = 0.5f;

    private BufferedImage cachedImage;
    private float cachedOpacity = opacity;

    public ImagePanel() {
        setBackground(new Color(0, 0, 0, 0));
    }

    public void setOpacity(float opacity) {
        if(opacity < 0.0f || opacity > 1.0f) {
            throw new IllegalArgumentException("opacity must be 0.0 <=> 1.0");
        }
        this.opacity = opacity;
        if (image != null) {
            repaint();
        }
    }

    public float getOpacity() {
        return opacity;
    }

    public void setImage(ImageInputStream is) throws IOException {
        image = ImageIO.read(is);
        if (image == null) {
            throw new IllegalArgumentException("Unreadable image : " + is);
        }

        Dimension dimension = new Dimension(this.image.getWidth(), this.image.getHeight());
        setPreferredSize(dimension);
    }

    private BufferedImage toTranslucent(BufferedImage srcImage, float opacity) {
        if (cachedImage != null && cachedOpacity == opacity) {
            return cachedImage;
        }

        ImageFilter filter = new RGBImageFilter() {
            @Override
            public int filterRGB(int x, int y, int rgb) {
                int transparency = 255 - (int) (255 * opacity);
                return (rgb ^ (transparency << 24));
            }
        };
        ImageProducer producer = new FilteredImageSource(srcImage.getSource(), filter);
        Image newImage =  Toolkit.getDefaultToolkit().createImage(producer);
        BufferedImage translucentImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = translucentImage.createGraphics();
        g.drawImage(newImage, 0, 0, null);
        g.dispose();

        cachedImage = translucentImage;
        cachedOpacity = opacity;
        return cachedImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) {
            super.paint(g);
            return;
        }

        BufferedImage transparentImage = toTranslucent(image, opacity);
        g.drawImage(transparentImage, 0, 0, null);
    }

}
