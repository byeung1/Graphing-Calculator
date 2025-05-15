import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * GraphPanel is a custom JPanel that plots a mathematical Function.
 */
public class GraphPanel extends JPanel {

    private final Function function; // The function to plot
    private ArrayList<Function> functionList;
    private List<Point> userDrawnPoints; // Points drawn by the user

    // Constructor that accepts a Function object
    public GraphPanel(Function function) {
        this.function = function;
        functionList = null;
        userDrawnPoints = new ArrayList<>();
        setPreferredSize(new Dimension(800, 600)); // Set default size
        setBackground(Color.WHITE); // Set background color
    }

    public GraphPanel(ArrayList<Function> functionList) {
        this.functionList = functionList;
        function = null;
        userDrawnPoints = new ArrayList<>();
        setPreferredSize(new Dimension(800, 600)); // Set default size
        setBackground(Color.WHITE); // Set background color
    }

    public void addFunction(Function f) {
        functionList.add(f);
    }
    
    /**
     * Sets the list of points drawn by the user
     * @param points The list of points
     */
    public void setUserDrawnPoints(List<Point> points) {
        this.userDrawnPoints = points;
        repaint();
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
        
        // Draw grid lines
        g2.setColor(new Color(230, 230, 230));
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                     10.0f, new float[]{2.0f}, 0.0f));
        
        // Draw horizontal grid lines
        for (int y = originY - (int)yScale; y >= 0; y -= yScale) {
            g2.drawLine(0, y, width, y);
        }
        for (int y = originY + (int)yScale; y <= height; y += yScale) {
            g2.drawLine(0, y, width, y);
        }
        
        // Draw vertical grid lines
        for (int x = originX - (int)xScale; x >= 0; x -= xScale) {
            g2.drawLine(x, 0, x, height);
        }
        for (int x = originX + (int)xScale; x <= width; x += xScale) {
            g2.drawLine(x, 0, x, height);
        }
        
        // Reset stroke
        g2.setStroke(new BasicStroke(1.0f));

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
        
        // Draw the user's drawn points
        if (userDrawnPoints != null && userDrawnPoints.size() > 1) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2.5f));
            
            for (int i = 0; i < userDrawnPoints.size() - 1; i++) {
                Point p1 = userDrawnPoints.get(i);
                Point p2 = userDrawnPoints.get(i + 1);
                g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
            }
        }
    }
}
