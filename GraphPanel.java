import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * GraphPanel is a custom JPanel that can optionally draw a mathematical function.
 */
public class GraphPanel extends JPanel {

    private final Function function;     // The function to be plotted
    private boolean drawFunction = false; // Whether the function should be drawn

    /**
     * Constructor that initializes the panel and optionally enables drawing.
     * 
     * @param function The function to graph
     * @param shouldDraw Whether the function should be drawn immediately
     */
    public GraphPanel(Function function, boolean shouldDraw) {
        this.function = function;
        setupPanel();            // Set size, background, etc.
        setDrawFunction(shouldDraw); // Explicitly decide if we start with drawing
    }

    /**
     * Sets up panel settings like size and background.
     */
    private void setupPanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
    }

    /**
     * Enables or disables drawing of the function and repaints.
     * 
     * @param shouldDraw true to draw the function, false to hide it
     */
    public void setDrawFunction(boolean shouldDraw) {
        this.drawFunction = shouldDraw;
        repaint(); // Repaint to reflect change
    }

    /**
     * Actually draws the function on the panel.
     * 
     * @param g2 Graphics2D context
     */
    private void drawFunction(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();

        double xScale = 50;
        double yScale = 50;

        int originX = width / 2;
        int originY = height / 2;

        g2.setColor(Color.BLUE);

        for (int px = -originX; px < originX - 1; px++) {
            double x1 = px / xScale;
            double x2 = (px + 1) / xScale;

            double y1 = function.evaluate(x1);
            double y2 = function.evaluate(x2);

            int screenX1 = originX + px;
            int screenY1 = originY - (int) (y1 * yScale);
            int screenX2 = originX + px + 1;
            int screenY2 = originY - (int) (y2 * yScale);

            if (Double.isFinite(y1) && Double.isFinite(y2)) {
                g2.draw(new Line2D.Double(screenX1, screenY1, screenX2, screenY2));
            }
        }
    }

    /**
     * Main paint method that handles drawing axes and optionally the function.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int originX = width / 2;
        int originY = height / 2;

        // Draw axes
        g2.setColor(Color.GRAY);
        g2.drawLine(0, originY, width, originY);   // X-axis
        g2.drawLine(originX, 0, originX, height);  // Y-axis

        // Conditionally draw the function
        if (drawFunction) {
            drawFunction(g2);
        }
    }
}
