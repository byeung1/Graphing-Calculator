import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DrawTheFunction extends JPanel {
    private JLabel functionLabel;
    private JLabel accuracyLabel;
    private JLabel scoreLabel;
    private Function currentFunction;
    private FunctionGraphPanel graphPanel;
    private boolean isDrawingMode = true;
    private List<Point> userDrawnPoints = new ArrayList<>();
    private int totalQuestions = 0;
    private double totalAccuracy = 0.0;
    
    public DrawTheFunction(Runnable onBack) {
        // Setting the layout for the panel
        setLayout(new BorderLayout());

        // Create top panel with title and score
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Welcome to Draw The Function", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        // Create score label
        scoreLabel = new JLabel("Questions: 0 | Average Accuracy: 0%", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(scoreLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Create a panel for the function display
        JPanel functionPanel = new JPanel(new BorderLayout());
        functionLabel = new JLabel("Click 'Generate Function' to see a random function", SwingConstants.CENTER);
        functionLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        
        // Set a consistent size for the accuracy label to prevent layout shifts
        accuracyLabel = new JLabel("", SwingConstants.CENTER);
        accuracyLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        accuracyLabel.setPreferredSize(new Dimension(400, 30)); // Fixed height
        accuracyLabel.setMinimumSize(new Dimension(400, 30));
        
        functionPanel.add(functionLabel, BorderLayout.NORTH);
        functionPanel.add(accuracyLabel, BorderLayout.SOUTH);
        
        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Create the generate function button
        JButton generateButton = new JButton("Generate Function");
        generateButton.addActionListener(e -> generateNewFunction());
        
        // Create confirm button
        JButton confirmButton = new JButton("Confirm Drawing");
        // Style the confirm button to be green
        confirmButton.setBackground(new Color(34, 139, 34)); // Forest green
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmButton.setOpaque(true);
        confirmButton.setBorderPainted(false);
        confirmButton.addActionListener(e -> confirmDrawing());
        
        // Create back button
        JButton backButton = new JButton("← Back to Home");
        // Style the back button to be dark red
        backButton.setBackground(new Color(139, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> onBack.run());
        
        // Add buttons to panel
        buttonPanel.add(generateButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);
        
        // Create a panel to hold both the graph and function info
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(functionPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Generate initial function
        generateNewFunction();
    }
    
    /**
     * Generates a new random function and displays it
     */
    private void generateNewFunction() {
        currentFunction = new Function();
        functionLabel.setText("f(x) = " + currentFunction.getExpressionString());
        // Use a non-breaking space to maintain consistent height
        accuracyLabel.setText("\u00A0"); // Unicode non-breaking space
        userDrawnPoints.clear();
        isDrawingMode = true;
        
        if (graphPanel != null) {
            remove(graphPanel);
        }
        
        graphPanel = new FunctionGraphPanel(currentFunction, false);
        graphPanel.setDrawingMode(true);
        graphPanel.setControlsEnabled(true);
        
        // Get the center panel and update its contents
        JPanel centerPanel = (JPanel) getComponent(1);
        centerPanel.removeAll();
        centerPanel.add(graphPanel, BorderLayout.CENTER);
        centerPanel.add(functionLabel, BorderLayout.NORTH);
        centerPanel.add(accuracyLabel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    /**
     * Confirms the user's drawing and shows the actual function
     */
    private void confirmDrawing() {
        if (isDrawingMode) {
            isDrawingMode = false;
            userDrawnPoints = graphPanel.getDrawnPoints();
            double accuracy = calculateAccuracy();
            accuracyLabel.setText(String.format("Accuracy: %.1f%%", accuracy));
            
            // Update score tracking
            totalQuestions++;
            totalAccuracy += accuracy;
            double averageAccuracy = totalAccuracy / totalQuestions;
            scoreLabel.setText(String.format("Questions: %d | Average Accuracy: %.1f%%", 
                totalQuestions, averageAccuracy));
            
            // Important: First set the user's drawn points before changing other modes
            // This ensures the drawn points are preserved with their original coordinates
            graphPanel.setUserDrawnPoints(userDrawnPoints);
            
            // Then change the mode and show the function
            graphPanel.setDrawingMode(false);
            graphPanel.setDrawFunction(true);
            graphPanel.setControlsEnabled(false);
            
            // No need to call repaint explicitly, the above methods will handle it
            // in a way that preserves the coordinate system
        }
    }
    
    /**
     * Calculates the accuracy of the user's drawing compared to the actual function
     */
    private double calculateAccuracy() {
        if (userDrawnPoints.isEmpty()) return 0.0;
        
        int width = graphPanel.getWidth();
        int height = graphPanel.getHeight();
        double totalError = 0;
        int validPoints = 0;
        
        // Sample points along the x-axis to check for entire function coverage
        int numSamples = 100; // Number of points to sample
        double xStep = (graphPanel.getXMax() - graphPanel.getXMin()) / numSamples;
        
        for (int i = 0; i <= numSamples; i++) {
            double x = graphPanel.getXMin() + i * xStep;
            
            try {
                double actualY = currentFunction.evaluate(x);
                
                // Skip if the actual y-value is invalid or outside the visible range
                if (Double.isNaN(actualY) || Double.isInfinite(actualY) || 
                    actualY < graphPanel.getYMin() || actualY > graphPanel.getYMax()) {
                    continue;
                }
                
                // Find the closest drawn point to this x-coordinate
                double closestDistance = Double.MAX_VALUE;
                double drawnY = actualY; // Default to maximum error (will be replaced if point found)
                boolean pointFound = false;
                
                for (Point p : userDrawnPoints) {
                    double drawnX = graphPanel.screenToX(p.x);
                    double distance = Math.abs(drawnX - x);
                    
                    // Consider points within a certain range (1% of the x-axis width)
                    double threshold = (graphPanel.getXMax() - graphPanel.getXMin()) * 0.01; 
                    
                    if (distance < closestDistance && distance < threshold) {
                        closestDistance = distance;
                        drawnY = graphPanel.screenToY(p.y);
                        pointFound = true;
                    }
                }
                
                // If no point was drawn near this x-coordinate, use maximum error
                double error;
                if (pointFound) {
                    error = Math.abs(actualY - drawnY);
                } else {
                    // Penalize missing sections with maximum error (equal to 25% of y-range)
                    error = (graphPanel.getYMax() - graphPanel.getYMin()) * 0.25;
                }
                
                totalError += error;
                validPoints++;
                
            } catch (ArithmeticException e) {
                // Skip points where the function evaluation fails (e.g., division by zero)
                continue;
            }
        }
        
        if (validPoints == 0) return 0.0;
        
        double averageError = totalError / validPoints;
        // Strict scoring with 25% y-range as maximum error
        double maxError = (graphPanel.getYMax() - graphPanel.getYMin()) * 0.25;
        double accuracy = Math.max(0, 100 * (1 - averageError / maxError));
        
        return accuracy;
    }
}