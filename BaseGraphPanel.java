import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A base class for creating interactive graph panels with zoom, pan, and drawing features
 * Provides a coordinate system with axes, grid lines, and labels
 * Supports user interaction for drawing and manipulating the view
 */
public class BaseGraphPanel extends JPanel {
    // List of line segments drawn by the user for the DrawTheFunction class
    protected List<List<Point>> lineSegments = new ArrayList<>();
    
    // The current line segment being drawn for the DrawTheFunction class
    protected List<Point> currentSegment = new ArrayList<>();
    
    // Points that have been submitted and should remain visible even after going to the HomeScreen and returning
    protected List<Point> userDrawnPoints = new ArrayList<>();
    
    // Whether the panel is currently in drawing mode, used while in the DrawTheFunction mode
    protected boolean drawingMode = false;
    
    // Whether the zoom and pan controls are enabled
    protected boolean controlsEnabled = true;
    
    // The visible range of the x and y-axis
    protected double xMin = -10;
    protected double xMax = 10;
    protected double yMin = -10;
    protected double yMax = 10;
    
    // Scale factors for converting between screen and mathematical coordinates
    protected double xScale = 50;
    protected double yScale = 50;
    
    // Labels that show the current visible range of the axes, are updated when the user zooms/pans
    protected JLabel xAxisLabel;
    protected JLabel yAxisLabel;

    /**
     * Creates a new BaseGraphPanel with default settings.
     * Initializes the panel size, background, and adds mouse listeners for drawing.
     * Creates the control panel with zoom and pan buttons.
     * Sets up the axis labels to show the current view range.
     */
    public BaseGraphPanel() {
        //sets up the size, background, control panel, and creates axis labels
        setupPanel();
        createControlPanel();
        createAxisLabels();
        
        // Add mouse listeners for drawing
        addMouseListener(new MouseAdapter() {
            /*
             * If the mouse is pressed, at the points to the currentSegment, which is then added to the lineSegments to track the user's inputs 
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (drawingMode) {
                    currentSegment = new ArrayList<>();
                    currentSegment.add(e.getPoint());
                    lineSegments.add(currentSegment);
                    repaint();
                }
            }
            
            /*
             * If the mouse is released, than create a new ArrayList for the next segment
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (drawingMode && !currentSegment.isEmpty()) {
                    currentSegment = new ArrayList<>();
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            /*
             * If the mouse is already being pressed, then you add the point to the currentSegment
             */
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
     * Sets up the basic properties of the panel
     * Configures the size, background color, and layout
     */
    protected void setupPanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
    }
    
    /**
     * Creates the control panel with zoom, pan, and undo buttons
     */
    protected void createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Create zoom buttons
        JButton zoomInButton = new JButton("+");
        JButton zoomOutButton = new JButton("-");
        
        // Create pan buttons
        JButton panUpButton = new JButton("↑");
        JButton panDownButton = new JButton("↓");
        JButton panLeftButton = new JButton("←");
        JButton panRightButton = new JButton("→");
        
        // Create undo button
        JButton undoButton = new JButton("Undo");
        undoButton.setName("undoButton"); // Add a name to find it later
        
        // Add action listeners for all buttons
        zoomInButton.addActionListener(e -> zoom(0.8));
        zoomOutButton.addActionListener(e -> zoom(1.2));
        panUpButton.addActionListener(e -> pan(0, 1));
        panDownButton.addActionListener(e -> pan(0, -1));
        panLeftButton.addActionListener(e -> pan(-1, 0));
        panRightButton.addActionListener(e -> pan(1, 0));
        undoButton.addActionListener(e -> undoLastSegment());
        
        // Add buttons to panel with labels
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
     * Creates the axis labels that show the current visible range
     * The labels are updated whenever the view changes
     */
    protected void createAxisLabels() {
        JPanel labelPanel = new JPanel(new BorderLayout());
        
        // Create and style the axis labels
        xAxisLabel = new JLabel("X: " + formatRange(xMin, xMax));
        yAxisLabel = new JLabel("Y: " + formatRange(yMin, yMax));
        
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
     * Formats a range for display in the axis labels
     * Rounds the numbers to two decimal places for readability
     * 
     * @param min The minimum value of the range
     * @param max The maximum value of the range
     * @return A formatted string showing the range
     */
    protected String formatRange(double min, double max) {
        DecimalFormat df = new DecimalFormat("#.##");
        return "[" + df.format(min) + ", " + df.format(max) + "]";
    }
    
    /**
     * Updates the axis labels with the current window bounds
     * Called whenever the view changes (zoom or pan)
     */
    protected void updateAxisLabels() {
        xAxisLabel.setText("X: " + formatRange(xMin, xMax));
        yAxisLabel.setText("Y: " + formatRange(yMin, yMax));
    }
    
    /**
     * Zooms the graph by the given factor
     * The zoom is centered on the current view center
     * 
     * @param factor The zoom factor (less than 1 to zoom in, greater than 1 to zoom out)
     */
    protected void zoom(double factor) {
        // Calculate the center of the current view
        double xCenter = (xMin + xMax) / 2;
        double yCenter = (yMin + yMax) / 2;
        
        // Calculate new ranges
        double xRange = (xMax - xMin) * factor;
        double yRange = (yMax - yMin) * factor;
        
        // Update bounds while keeping the center point
        xMin = xCenter - xRange / 2;
        xMax = xCenter + xRange / 2;
        yMin = yCenter - yRange / 2;
        yMax = yCenter + yRange / 2;
        
        updateAxisLabels();
        repaint();
    }
    
    /**
     * Pans the graph in the specified direction
     * The pan distance is proportional to the current view size
     * 
     * @param xDirection Horizontal direction (-1 for left, 1 for right, 0 for none)
     * @param yDirection Vertical direction (-1 for down, 1 for up, 0 for none)
     */
    protected void pan(int xDirection, int yDirection) {
        // Calculate the current ranges
        double xRange = xMax - xMin;
        double yRange = yMax - yMin;
        
        // Calculate step sizes (10% of the current range)
        double xStep = xRange * 0.1;
        double yStep = yRange * 0.1;
        
        // Update bounds
        xMin += xDirection * xStep;
        xMax += xDirection * xStep;
        yMin += yDirection * yStep;
        yMax += yDirection * yStep;
        
        updateAxisLabels();
        repaint();
    }

    /**
     * Main paint method that handles drawing the graph
     * Draws the grid, axes, and any user drawings
     * 
     * @param g The graphics context to draw with
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw the grid first, it is behind everything else
        drawGrid(g2, width, height);

        // Draw the axes
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
    }
    
    /**
     * Draws the grid lines and their labels
     * The grid spacing is automatically adjusted based on the current zoom level
     * 
     * @param g2 The graphics context to draw with
     * @param width The width of the panel
     * @param height The height of the panel
     */
    protected void drawGrid(Graphics2D g2, int width, int height) {
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
            if (Math.abs(x) > 0.001) { 
                // Don't label zero
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
            if (Math.abs(y) > 0.001) { 
                // Don't label zero
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
     * Calculates an appropriate step size for grid lines based on the range
     * Tries to find a step size that will result in 5-20 grid lines
     * 
     * @param range The total range to divide into grid lines
     * @return The calculated step size
     */
    protected double calculateStepSize(double range) {
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
     * Public method to be used in the DrawTheFunction class
     * Sets whether the panel is in drawing mode
     * When drawing mode is enabled, mouse events will create new line segments
     * 
     * @param mode Whether drawing mode should be enabled
     */
    public void setDrawingMode(boolean mode) {
        this.drawingMode = mode;
        if (!mode) {
            currentSegment.clear();
        }
    }

    /**
     * Removes the last line segment drawn by the user
     * Called when the undo button is clicked
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
     * Gets all points from all line segments drawn by the user
     * 
     * @return A list of all points in all line segments
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
     * When disabled, the control buttons will be grayed out
     * They should be disabled after the user submits their drawing
     * 
     * @param enabled Whether the controls should be enabled
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
     * 
     * @param screenX The x-coordinate in screen pixels
     * @return The corresponding x value in the mathematical coordinate system
     */
    public double screenToX(int screenX) {
        return xMin + (screenX * (xMax - xMin)) / getWidth();
    }

    /**
     * Converts a screen y-coordinate to a mathematical y-coordinate
     * 
     * @param screenY The y-coordinate in screen pixels
     * @return The corresponding y value in the mathematical coordinate system
     */
    public double screenToY(int screenY) {
        return yMax - (screenY * (yMax - yMin)) / getHeight();
    }

    /**
     * Gets the maximum x value of the viewing window
     * 
     * @return The maximum x value currently visible
     */
    public double getXMax() {
        return xMax;
    }

    /**
     * Gets the minimum x value of the viewing window
     * 
     * @return The minimum x value currently visible
     */
    public double getXMin() {
        return xMin;
    }

    /**
     * Gets the maximum y value of the viewing window
     * 
     * @return The maximum y value currently visible
     */
    public double getYMax() {
        return yMax;
    }

    /**
     * Gets the minimum y value of the viewing window
     * 
     * @return The minimum y value currently visible
     */
    public double getYMin() {
        return yMin;
    }

    /**
     * Sets the user's drawn points to be displayed permanently
     * This is used to preserve the user's drawing when switching modes
     * 
     * @param points The points to display
     */
    public void setUserDrawnPoints(List<Point> points) {
        this.userDrawnPoints = new ArrayList<>(points);
        repaint();
    }

    /**
     * Hides the undo button and its label
     * Used in modes where drawing is not allowed (GuessTheFunction)
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