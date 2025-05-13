import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.*;

/**
 * GraphPanel is a custom JPanel that plots a mathematical Function.
 */
public class GraphPanel extends JPanel {

    private final Function function; // The function to plot
    private final ArrayList<Function> functionList;

    // Constructor that accepts a Function object
    public GraphPanel(Function function) {
        this.function = function;
        functionList = null;
        setPreferredSize(new Dimension(800, 600)); // Set default size
        setBackground(Color.WHITE); // Set background color
    }

    public GraphPanel(ArrayList<Function> functionList) {
        this.functionList = functionList;
        function = null;
        setPreferredSize(new Dimension(800, 600)); // Set default size
        setBackground(Color.WHITE); // Set background color
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Coordinate scaling: pixels per unit
        double xScale = 50;
        double yScale = 50;

        // Center of the panel as origin
        int originX = width / 2;
        int originY = height / 2;

        // Draw x and y axes
        g2.setColor(Color.GRAY);
        g2.drawLine(0, originY, width, originY);   // X-axis
        g2.drawLine(originX, 0, originX, height);  // Y-axis

        // Plot the function in blue
        g2.setColor(Color.BLUE);

        if (function == null) {
            for (Function function : functionList) {
                for (int px = -originX; px < originX - 1; px++) {
                double x1 = px / xScale;
                double x2 = (px + 1) / xScale;

                double y1 = function.evaluate(x1);
                double y2 = function.evaluate(x2);

                int screenX1 = originX + px;
                int screenY1 = originY - (int) (y1 * yScale);
                int screenX2 = originX + px + 1;
                int screenY2 = originY - (int) (y2 * yScale);

                // Only draw finite values
                if (Double.isFinite(y1) && Double.isFinite(y2)) {
                    g2.draw(new Line2D.Double(screenX1, screenY1, screenX2, screenY2));
                }
            }
            }
        }
        else {
            for (int px = -originX; px < originX - 1; px++) {
                double x1 = px / xScale;
                double x2 = (px + 1) / xScale;

                double y1 = function.evaluate(x1);
                double y2 = function.evaluate(x2);

                int screenX1 = originX + px;
                int screenY1 = originY - (int) (y1 * yScale);
                int screenX2 = originX + px + 1;
                int screenY2 = originY - (int) (y2 * yScale);

                // Only draw finite values
                if (Double.isFinite(y1) && Double.isFinite(y2)) {
                    g2.draw(new Line2D.Double(screenX1, screenY1, screenX2, screenY2));
                }
            }
        }
    }

}
