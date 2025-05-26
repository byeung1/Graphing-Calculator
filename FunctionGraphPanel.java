import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * FunctionGraphPanel extends BaseGraphPanel to add functionality for plotting mathematical functions.
 */
public class FunctionGraphPanel extends BaseGraphPanel {
    private Function function;     // The function to be plotted
    private boolean drawFunction = false; // Whether the function should be drawn

    /**
     * Constructor that initializes the panel and optionally enables drawing.
     * 
     * @param function The function to graph
     * @param shouldDraw Whether the function should be drawn immediately
     */
    public FunctionGraphPanel(Function function, boolean shouldDraw) {
        super();
        this.function = function;
        setDrawFunction(shouldDraw);
    }

    /**
     * Enables or disables drawing of the function and repaints.
     * 
     * @param shouldDraw true to draw the function, false to hide it
     */
    public void setDrawFunction(boolean shouldDraw) {
        this.drawFunction = shouldDraw;
        repaint();
    }

    /**
     * Actually draws the function on the panel.
     */
    private void drawFunction(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();

        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));

        // Calculate points for the function
        int numPoints = width;
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];
        int pointCount = 0;
        
        double prevY = Double.NaN;
        
        for (int i = 0; i < numPoints; i++) {
            double x = xMin + i * (xMax - xMin) / numPoints;
            double y = function.evaluate(x);
            
            // Skip invalid points
            if (Double.isNaN(y) || Double.isInfinite(y)) {
                // End current segment if we hit an invalid point
                if (pointCount > 1) {
                    g2.drawPolyline(Arrays.copyOf(xPoints, pointCount), 
                                  Arrays.copyOf(yPoints, pointCount), 
                                  pointCount);
                }
                pointCount = 0;
                prevY = Double.NaN;
                continue;
            }
            
            // Only draw points within the visible range
            if (y >= yMin && y <= yMax) {
                int screenX = i;
                int screenY = (int) ((yMax - y) / (yMax - yMin) * height);
                
                // Skip if there's a discontinuity (large jump)
                if (!Double.isNaN(prevY) && Math.abs(y - prevY) > (yMax - yMin) / 4) {
                    // Start a new segment
                    if (pointCount > 1) {
                        g2.drawPolyline(Arrays.copyOf(xPoints, pointCount), 
                                      Arrays.copyOf(yPoints, pointCount), 
                                      pointCount);
                    }
                    pointCount = 0;
                }
                
                xPoints[pointCount] = screenX;
                yPoints[pointCount] = screenY;
                pointCount++;
                prevY = y;
            } else {
                // End current segment if we hit a point outside range
                if (pointCount > 1) {
                    g2.drawPolyline(Arrays.copyOf(xPoints, pointCount), 
                                  Arrays.copyOf(yPoints, pointCount), 
                                  pointCount);
                }
                pointCount = 0;
                prevY = Double.NaN;
            }
        }
        
        // Draw the last segment
        if (pointCount > 1) {
            g2.drawPolyline(Arrays.copyOf(xPoints, pointCount), 
                          Arrays.copyOf(yPoints, pointCount), 
                          pointCount);
        }
    }

    /**
     * Override paintComponent to add function drawing capability
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw function last so it appears on top
        if (drawFunction) {
            drawFunction((Graphics2D) g);
        }
    }

    /**
     * Sets a new function to be plotted
     */
    public void setFunction(Function newFunction) {
        this.function = newFunction;
        repaint();
    }
} 