package interpreter;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JFrame;


class Screen extends JComponent {

	/**
     *
     */
    private static final long serialVersionUID = 6067592964997947204L;

    private static final Color[] COLORS = { Color.WHITE, Color.ORANGE, Color.MAGENTA, new Color(173, 216, 230),
            Color.YELLOW, new Color(192, 255, 0), Color.PINK, Color.GRAY, Color.LIGHT_GRAY, Color.CYAN, Color.BLUE,
            new Color(128, 0, 128), Color.GREEN, new Color(165, 42, 42), Color.RED, Color.BLACK };

    private JFrame frame;
    private byte[][] grid;

    private byte[][][] savedGrids;

    public Screen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defaultSize = 2 * Math.min(screenSize.width, screenSize.height) / 3;
        this.setPreferredSize(new Dimension(defaultSize, defaultSize));
        this.setMinimumSize(new Dimension(100, 100));
        frame = new JFrame();
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();

        grid = new byte[128][128];
        savedGrids = new byte[64][128][128];
        frame.setBackground(new Color(255, 0, 0));
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }

    @Override
    public void paintComponent(Graphics g) {
        int size = grid.length;
        double width = (double)this.getWidth() / size;
        double height = (double)this.getHeight() / size;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = (int)(i * width);
                int y = (int)(j * height);
                g.setColor(COLORS[grid[i][j]]);
                g.fillRect(x, y, (int)Math.ceil(width), (int)Math.ceil(height));
            }
        }
    }

    /**
     * used with gpu.clear
     * 
     * Fills the whole display with the given colour
     * @param color - the new color of the screen
     */
    public void clear(int color) {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++)
                grid[i][j] = (byte)color;
        repaint();
    }

    /**
     * used with gpu.screen
     * 
     * @param index - the index of the saved screen 0 <= index <= 63
     */
    public void saveScreen(int index) {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid.length; j++)
                savedGrids[index][i][j] = grid[i][j];
        repaint();
    }

    /**
     * used with gpu.pixel
     * 
     * @param x - the x coordinate of the pixel
     * @param y - the y coordinate of the pixel
     * @param color - the new color of the pixel
     */
    public void pixel(int x, int y, int color) {
        grid[x][y] = (byte)color;
        repaint();
    }

    /**
     * used with gpu.line
     * 
     * draws a line from (x0, y0) to (x1, y1) using Bresenham's line algorithm
     * @param x0 - the x coordinate of the first point
     * @param y0 - the y coordinate of the first point
     * @param x1 - the x coordinate of the second point
     * @param y1 - the y coordinate of the second point
     * @param color - the color of the line
     */
    public void line(int x0, int y0, int x1, int y1, int color) {
        //implementation of Bresenham's line algorithm
        int dx = Math.abs(x1 - x0);
        int sx = (x0 < x1)? 1 : -1;
        int dy = -Math.abs(y1 - y0);
        int sy = (y0 < y1)? 1 : -1;
        int error = dx + dy;

        while (true) {
            grid[x0][y0] = (byte)color;
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * error;
            if (e2 >= dy) {
                error += dy;
                x0 += sx;
            }
            if (e2 <= dx) {
                error += dx;
                y0 += sy;
            }
        }
        repaint();
    }

    public void fill(int x0, int y0, int x1, int y1, int color) {
        if (x0 == x1 || y0 == y1) {
            line(x0, y0, x1, y1, color);
        } else {
            int sx = (x0 < x1)? 1 : -1;
            int sy = (y0 < y1)? 1 : -1;
            for (int x = x0; x != x1; x += sx) {
                for (int y = y0; y != y1; y += sy) {
                    grid[x][y] = (byte)color;
                }
            }
            repaint();
        }
    }
}
