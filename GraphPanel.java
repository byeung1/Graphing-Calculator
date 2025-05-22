import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GraphPanel is a custom JPanel that can optionally draw a mathematical function.
 */
public class GraphPanel extends JPanel {

    private Function function;     // The function to be plotted
    private boolean drawFunction = false; // Whether the function should be drawn
    private boolean drawingMode = false;  // Whether we're in drawing mode
    private List<List<Point>> lineSegments = new ArrayList<>(); // List of line segments
    private List<Point> currentSegment = new ArrayList<>(); // Current line segment being drawn
    private List<Point> userDrawnPoints = new ArrayList<>(); // Points to keep visible after submission
    private boolean controlsEnabled = true; // Whether zoom/pan controls are enabled
    
    // Window bounds
    private double xMin = -10;
    private double xMax = 10;
    private double yMin = -10;
    private double yMax = 10;
    
    // Scale factors
    private double xScale = 50;
    private double yScale = 50;
    
    // Labels for axes
    private JLabel xAxisLabel;
    private JLabel yAxisLabel;

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
        
        // Create control panel with buttons
        createControlPanel();
        
        // Create axis labels
        createAxisLabels();
        
        // Add mouse listeners for drawing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (drawingMode) {
                    currentSegment = new ArrayList<>();
                    currentSegment.add(e.getPoint());
                    lineSegments.add(currentSegment);
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (drawingMode && !currentSegment.isEmpty()) {
                    currentSegment = new ArrayList<>();
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawingMode && !currentSegment.isEmpty()) {
                    currentSegment.add(e.getPoint());
                    repaint();
                }
            }
        });
    }

    /**
     * Sets up panel settings like size and background.
     */
    private void setupPanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
    }
    
    /**
     * Creates the control panel with zoom and pan buttons.
     */
    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Zoom buttons
        JButton zoomInButton = new JButton("+");
        JButton zoomOutButton = new JButton("-");
        
        // Pan buttons
        JButton panUpButton = new JButton("↑");
        JButton panDownButton = new JButton("↓");
        JButton panLeftButton = new JButton("←");
        JButton panRightButton = new JButton("→");
        
        // Undo button
        JButton undoButton = new JButton("Undo");
        undoButton.setName("undoButton"); // Add a name to find it later
        
        // Add action listeners
        zoomInButton.addActionListener(e -> zoom(0.8));
        zoomOutButton.addActionListener(e -> zoom(1.2));
        panUpButton.addActionListener(e -> pan(0, 1));
        panDownButton.addActionListener(e -> pan(0, -1));
        panLeftButton.addActionListener(e -> pan(-1, 0));
        panRightButton.addActionListener(e -> pan(1, 0));
        undoButton.addActionListener(e -> undoLastSegment());
        
        // Add buttons to panel
        controlPanel.add(new JLabel("Zoom:"));
        controlPanel.add(zoomInButton);
        controlPanel.add(zoomOutButton);
        controlPanel.add(new JLabel("Pan:"));
        controlPanel.add(panUpButton);
        controlPanel.add(panDownButton);
        controlPanel.add(panLeftButton);
        controlPanel.add(panRightButton);
        controlPanel.add(new JLabel("Drawing:"));
        controlPanel.add(undoButton);
        
        // Add control panel to the top of the graph panel
        add(controlPanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates the axis labels that will be updated dynamically.
     */
    private void createAxisLabels() {
        JPanel labelPanel = new JPanel(new BorderLayout());
        
        // Create axis labels
        xAxisLabel = new JLabel("X: " + formatRange(xMin, xMax));
        yAxisLabel = new JLabel("Y: " + formatRange(yMin, yMax));
        
        // Style the labels
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        xAxisLabel.setFont(labelFont);
        yAxisLabel.setFont(labelFont);
        
        // Add labels to panel
        labelPanel.add(xAxisLabel, BorderLayout.SOUTH);
        labelPanel.add(yAxisLabel, BorderLayout.EAST);
        
        // Add label panel to the graph panel
        add(labelPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Formats a range for display in the axis labels.
     */
    private String formatRange(double min, double max) {
        DecimalFormat df = new DecimalFormat("#.##");
        return "[" + df.format(min) + ", " + df.format(max) + "]";
    }
    
    /**
     * Updates the axis labels with current window bounds.
     */
    private void updateAxisLabels() {
        xAxisLabel.setText("X: " + formatRange(xMin, xMax));
        yAxisLabel.setText("Y: " + formatRange(yMin, yMax));
    }
    
    /**
     * Zooms the graph by the given factor.
     * 
     * @param factor Zoom factor (less than 1 to zoom in, greater than 1 to zoom out)
     */
    private void zoom(double factor) {
        double xCenter = (xMin + xMax) / 2;
        double yCenter = (yMin + yMax) / 2;
        
        double xRange = (xMax - xMin) * factor;
        double yRange = (yMax - yMin) * factor;
        
        xMin = xCenter - xRange / 2;
        xMax = xCenter + xRange / 2;
        yMin = yCenter - yRange / 2;
        yMax = yCenter + yRange / 2;
        
        updateAxisLabels();
        repaint();
    }
    
    /**
     * Pans the graph in the specified direction.
     * 
     * @param xDirection Horizontal direction (-1 for left, 1 for right, 0 for none)
     * @param yDirection Vertical direction (-1 for down, 1 for up, 0 for none)
     */
    private void pan(int xDirection, int yDirection) {
        double xRange = xMax - xMin;
        double yRange = yMax - yMin;
        
        double xStep = xRange * 0.1;
        double yStep = yRange * 0.1;
        
        xMin += xDirection * xStep;
        xMax += xDirection * xStep;
        yMin += yDirection * yStep;
        yMax += yDirection * yStep;
        
        updateAxisLabels();
        repaint();
    }

    /**
     * Enables or disables drawing of the function and repaints.
     * 
     * @param shouldDraw true to draw the function, false to hide it
     */
    public void setDrawFunction(boolean shouldDraw) {
        this.drawFunction = shouldDraw;
        repaint(); // Repaint to reflect change, this will preserve the current viewport
    }

    /**
     * Actually draws the function on the panel.
     * 
     * @param g2 Graphics2D context
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
     * Main paint method that handles drawing axes and optionally the function.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw grid first
        drawGrid(g2, width, height);

        // Draw axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        
        // Draw x-axis
        int yZero = (int) ((yMax) / (yMax - yMin) * height);
        g2.drawLine(0, yZero, width, yZero);
        
        // Draw y-axis
        int xZero = (int) ((0 - xMin) / (xMax - xMin) * width);
        g2.drawLine(xZero, 0, xZero, height);

        // Draw user's drawing (either current drawing or submitted drawing)
        if (!userDrawnPoints.isEmpty()) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            for (int i = 1; i < userDrawnPoints.size(); i++) {
                Point p1 = userDrawnPoints.get(i - 1);
                Point p2 = userDrawnPoints.get(i);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        } else if (drawingMode) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            // Draw all line segments
            for (List<Point> segment : lineSegments) {
                for (int i = 1; i < segment.size(); i++) {
                    Point p1 = segment.get(i - 1);
                    Point p2 = segment.get(i);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        // Draw function last so it appears on top
        if (drawFunction) {
            drawFunction(g2);
        }
    }
    
    /**
     * Draws the grid lines on the graph.
     */
    private void drawGrid(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(240, 240, 240));
        
        // Calculate step size based on current zoom level
        double xStep = calculateStepSize(xMax - xMin);
        double yStep = calculateStepSize(yMax - yMin);
        
        // Get y-zero position for drawing x-axis labels
        int yZeroPos = (int) ((yMax) / (yMax - yMin) * height);
        int labelYPos = height - 5; // Default position at bottom
        
        // If zero is visible, draw labels just below the x-axis
        if (yZeroPos >= 0 && yZeroPos <= height) {
            labelYPos = yZeroPos + 15;
        }
        
        // Draw vertical grid lines
        for (double x = Math.ceil(xMin / xStep) * xStep; x <= xMax; x += xStep) {
            int screenX = (int) ((x - xMin) / (xMax - xMin) * width);
            g2.draw(new Line2D.Double(screenX, 0, screenX, height));
            
            // Draw x-axis labels
            if (Math.abs(x) > 0.001) { // Don't label zero
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                String label = String.format("%.1f", x);
                FontMetrics fm = g2.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                g2.drawString(label, screenX - labelWidth/2, labelYPos);
                g2.setColor(new Color(240, 240, 240));
            }
        }
        
        // Get x-zero position for drawing y-axis labels
        int xZeroPos = (int) ((0 - xMin) / (xMax - xMin) * width);
        int labelXPos = 5; // Default position at left
        
        // If zero is visible, draw labels just to the left of the y-axis
        if (xZeroPos >= 0 && xZeroPos <= width) {
            labelXPos = xZeroPos + 5;
        }
        
        // Draw horizontal grid lines
        for (double y = Math.ceil(yMin / yStep) * yStep; y <= yMax; y += yStep) {
            int screenY = (int) ((yMax - y) / (yMax - yMin) * height);
            g2.draw(new Line2D.Double(0, screenY, width, screenY));
            
            // Draw y-axis labels
            if (Math.abs(y) > 0.001) { // Don't label zero
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.PLAIN, 10));
                String label = String.format("%.1f", y);
                FontMetrics fm = g2.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                g2.drawString(label, labelXPos, screenY + fm.getAscent()/2);
                g2.setColor(new Color(240, 240, 240));
            }
        }
        
        // Draw origin label (0,0) if visible
        if (xZeroPos >= 0 && xZeroPos <= width && yZeroPos >= 0 && yZeroPos <= height) {
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 10));
            g2.drawString("0", xZeroPos + 5, yZeroPos + 15);
        }
    }
    
    /**
     * Calculates an appropriate step size for grid lines based on the range.
     */
    private double calculateStepSize(double range) {
        double[] possibleSteps = {0.1, 0.2, 0.5, 1.0, 2.0, 5.0, 10.0, 20.0, 50.0, 100.0};
        
        // Find the first step size that gives between 5 and 20 grid lines
        for (double step : possibleSteps) {
            int numLines = (int) (range / step);
            if (numLines >= 5 && numLines <= 20) {
                return step;
            }
        }
        
        // Default to a reasonable step size
        return range / 10;
    }

    /**
     * Sets whether the panel is in drawing mode
     */
    public void setDrawingMode(boolean mode) {
        this.drawingMode = mode;
        if (!mode) {
            // Don't clear drawn segments when turning off drawing mode
            // This preserves the user's drawing when showing the function
            currentSegment.clear();
        }
        // No need to call repaint() here as it will be called when setDrawFunction is called
    }

    /**
     * Undoes the last line segment drawn
     */
    public void undoLastSegment() {
        if (!lineSegments.isEmpty()) {
            lineSegments.remove(lineSegments.size() - 1);
            if (!currentSegment.isEmpty()) {
                currentSegment.clear();
            }
            repaint();
        }
    }

    /**
     * Gets all drawn points from all line segments
     */
    public List<Point> getDrawnPoints() {
        List<Point> allPoints = new ArrayList<>();
        for (List<Point> segment : lineSegments) {
            allPoints.addAll(segment);
        }
        return allPoints;
    }

    /**
     * Enables or disables the zoom and pan controls
     */
    public void setControlsEnabled(boolean enabled) {
        this.controlsEnabled = enabled;
        Component[] controls = ((JPanel)getComponent(0)).getComponents();
        for (Component c : controls) {
            if (c instanceof JButton) {
                c.setEnabled(enabled);
            }
        }
    }

    /**
     * Converts a screen x-coordinate to a mathematical x-coordinate
     */
    public double screenToX(int screenX) {
        return xMin + (screenX * (xMax - xMin)) / getWidth();
    }

    /**
     * Converts a screen y-coordinate to a mathematical y-coordinate
     */
    public double screenToY(int screenY) {
        return yMax - (screenY * (yMax - yMin)) / getHeight();
    }

    /**
     * Gets the maximum x value of the viewing window
     */
    public double getXMax() {
        return xMax;
    }

    /**
     * Gets the minimum x value of the viewing window
     */
    public double getXMin() {
        return xMin;
    }

    /**
     * Gets the maximum y value of the viewing window
     */
    public double getYMax() {
        return yMax;
    }

    /**
     * Gets the minimum y value of the viewing window
     */
    public double getYMin() {
        return yMin;
    }

    /**
     * Sets the user's drawn points to be displayed permanently
     */
    public void setUserDrawnPoints(List<Point> points) {
        this.userDrawnPoints = new ArrayList<>(points);
        // No need to clear lineSegments here, as they should be preserved
        // for accurate representation of what the user drew
        repaint();
    }

    /**
     * Sets a new function to be plotted
     */
    public void setFunction(Function newFunction) {
        this.function = newFunction;
        repaint();
    }

    /**
     * Hides the undo button for modes where it's not needed
     */
    public void hideUndoButton() {
        JPanel controlPanel = (JPanel) getComponent(0);
        for (Component component : controlPanel.getComponents()) {
            if (component instanceof JButton && "undoButton".equals(component.getName())) {
                component.setVisible(false);
            }
            if (component instanceof JLabel && "Drawing:".equals(((JLabel)component).getText())) {
                component.setVisible(false);
            }
        }
    }
}