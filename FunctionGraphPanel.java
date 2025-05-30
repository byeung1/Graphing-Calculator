import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * A specialized graph panel that can display mathematical functions
 * Extends BaseGraphPanel to add functionality for plotting functions while maintaining all the base panel's zoom, pan, and drawing capabilities
 * Handles function discontinuities and invalid points appropriately
 */
public class FunctionGraphPanel extends BaseGraphPanel {
    // The mathematical function to be plotted
    private Function function;
    
    // Whether the function should be drawn on the panel, should be hidden for some time in DrawTheFunction
    private boolean drawFunction = false;

    /**
     * Creates a new FunctionGraphPanel with the specified function
     * 
     * @param function The mathematical function to graph
     * @param shouldDraw Whether the function should be drawn immediately
     */
    public FunctionGraphPanel(Function function, boolean shouldDraw) {
        super();
        this.function = function;
        setDrawFunction(shouldDraw);
    }

    /**
     * Enables or disables drawing of the function
     * 
     * @param shouldDraw true to draw the function, false to hide it
     */
    public void setDrawFunction(boolean shouldDraw) {
        this.drawFunction = shouldDraw;
        repaint();
    }

    /**
     * Draws the function on the panel by sampling points along the x-axis and connects them with line segments
     * 
     * @param g2 The graphics context to draw with
     */
    private void drawFunction(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();

        // Set up the drawing style
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));

        // Prepare arrays for storing points
        int numPoints = width;
        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];
        int pointCount = 0;
        
        double prevY = Double.NaN;
        
        // Sample points along the x-axis
        for (int i = 0; i < numPoints; i++) {
            double x = xMin + i * (xMax - xMin) / numPoints;
            double y = function.evaluate(x);
            
            // Handle invalid points (NaN or infinite)
            if (Double.isNaN(y) || Double.isInfinite(y)) {
                // Draw current segment if we have enough points
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
                
                // Handle discontinuities (large jumps in y-value)
                if (!Double.isNaN(prevY) && Math.abs(y - prevY) > (yMax - yMin) / 4) {
                    // Draw current segment and start a new one
                    if (pointCount > 1) {
                        g2.drawPolyline(Arrays.copyOf(xPoints, pointCount), 
                                      Arrays.copyOf(yPoints, pointCount), 
                                      pointCount);
                    }
                    pointCount = 0;
                }
                
                // Add point to current segment
                xPoints[pointCount] = screenX;
                yPoints[pointCount] = screenY;
                pointCount++;
                prevY = y;
            } else {
                // Draw current segment if point is outside visible range
                if (pointCount > 1) {
                    g2.drawPolyline(Arrays.copyOf(xPoints, pointCount), 
                                  Arrays.copyOf(yPoints, pointCount), 
                                  pointCount);
                }
                pointCount = 0;
                prevY = Double.NaN;
            }
        }
        
        // Draw the final segment if there are points left
        if (pointCount > 1) {
            g2.drawPolyline(Arrays.copyOf(xPoints, pointCount), 
                          Arrays.copyOf(yPoints, pointCount), 
                          pointCount);
        }
    }

    /**
     * Overrides the base panel's paint method to add function drawing
     * Draws the function on top of the grid and axes if enabled
     * 
     * @param g The graphics context to draw with
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
     * Changes the function being plotted
     * The panel will be repainted to show the new function
     * 
     * @param newFunction The new function to plot
     */
    public void setFunction(Function newFunction) {
        this.function = newFunction;
        repaint();
    }
} 