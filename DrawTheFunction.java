import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A game mode where players try to draw a function matching a given mathematical expression.
 * The game provides a graph panel where players can draw their function, and then compares
 * their drawing to the actual function to calculate accuracy. Players can generate new
 * functions to practice with and track their performance over multiple attempts.
 */
public class DrawTheFunction extends JPanel {
    // Declares labels showing the function, accuracy, and score
    private JLabel functionLabel;
    private JLabel accuracyLabel;
    private JLabel scoreLabel;
    
    // The function that the player is trying to draw
    private Function currentFunction;
    
    // The panel where the player draws and the function is displayed
    private FunctionGraphPanel graphPanel;
    
    // Whether the panel is currently in drawing mode
    private boolean isDrawingMode = true;
    
    // Points drawn by the player that will be compared to the actual function
    private List<Point> userDrawnPoints = new ArrayList<>();
    
    // Total number of questions attempted
    private int totalQuestions = 0;
    
    // Sum of all accuracy scores for calculating average
    private double totalAccuracy = 0.0;
    
    /**
     * Creates a new Draw The Function game panel
     * Sets up the UI with a title, score display, function display, and buttons for generating functions and confirming drawings
     * 
     * @param onBack Callback function to return to the home screen
     */
    public DrawTheFunction(Runnable onBack) {
        // Set up the main panel layout
        setLayout(new BorderLayout());

        // Create and configure the top panel with title and score
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Welcome to Draw The Function", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        // Create the score label
        scoreLabel = new JLabel("Questions: 0 | Average Accuracy: 0%", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(scoreLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Create and configure the function display panel
        JPanel functionPanel = new JPanel(new BorderLayout());
        functionLabel = new JLabel("Click 'Generate Function' to see a random function", SwingConstants.CENTER);
        functionLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        
        // Create the accuracy label with fixed size to prevent layout shifts
        accuracyLabel = new JLabel("", SwingConstants.CENTER);
        accuracyLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        accuracyLabel.setPreferredSize(new Dimension(400, 30));
        accuracyLabel.setMinimumSize(new Dimension(400, 30));
        
        functionPanel.add(functionLabel, BorderLayout.NORTH);
        functionPanel.add(accuracyLabel, BorderLayout.SOUTH);
        
        // Create and configure the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Create and configure the generate function button
        JButton generateButton = new JButton("Generate Function");
        generateButton.addActionListener(e -> generateNewFunction());
        
        // Create the confirm button
        JButton confirmButton = new JButton("Confirm Drawing");
        confirmButton.setBackground(new Color(34, 139, 34)); // Forest green
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmButton.setOpaque(true);
        confirmButton.setBorderPainted(false);
        confirmButton.addActionListener(e -> confirmDrawing());
        
        // Create the back button
        JButton backButton = new JButton("← Back to Home");
        backButton.setBackground(new Color(139, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> onBack.run());
        
        // Add buttons to the panel
        buttonPanel.add(generateButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);
        
        // Create and configure the center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(functionPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Generate the initial function
        generateNewFunction();
    }
    
    /**
     * Generates a new random function and updates the display
     * Clears any previous drawing and resets the panel to drawing mode
     */
    private void generateNewFunction() {
        // Generate a new random function
        currentFunction = new Function();
        functionLabel.setText("f(x) = " + currentFunction.getExpressionString());
        
        // Clear previous drawing and accuracy display
        // Unicode non-breaking space
        accuracyLabel.setText("\u00A0"); 
        userDrawnPoints.clear();
        isDrawingMode = true;
        
        // Remove old graph panel if it exists
        if (graphPanel != null) {
            remove(graphPanel);
        }
        
        // Create and configure new graph panel
        graphPanel = new FunctionGraphPanel(currentFunction, false);
        graphPanel.setDrawingMode(true);
        graphPanel.setControlsEnabled(true);
        
        // Update the center panel with new components
        JPanel centerPanel = (JPanel) getComponent(1);
        centerPanel.removeAll();
        centerPanel.add(graphPanel, BorderLayout.CENTER);
        centerPanel.add(functionLabel, BorderLayout.NORTH);
        centerPanel.add(accuracyLabel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    /**
     * Confirms the player's drawing and shows the actual function
     * Calculates accuracy and updates the score display
     */
    private void confirmDrawing() {
        if (isDrawingMode) {
            // Switch to evaluation mode
            isDrawingMode = false;
            
            // Get the player's drawing and calculate accuracy
            userDrawnPoints = graphPanel.getDrawnPoints();
            double accuracy = calculateAccuracy();
            accuracyLabel.setText(String.format("Accuracy: %.1f%%", accuracy));
            
            // Update score tracking
            totalQuestions++;
            totalAccuracy += accuracy;
            double averageAccuracy = totalAccuracy / totalQuestions;
            scoreLabel.setText(String.format("Questions: %d | Average Accuracy: %.1f%%", 
                totalQuestions, averageAccuracy));
            
            // Preserve the player's drawing and show the actual function
            graphPanel.setUserDrawnPoints(userDrawnPoints);
            graphPanel.setDrawingMode(false);
            graphPanel.setDrawFunction(true);
            graphPanel.setControlsEnabled(false);
        }
    }
    
    /**
     * Calculates how well the player's drawing matches the actual function
     * Samples points along the x-axis and compares the y-values
     * 
     * @return The accuracy percentage (0-100)
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
                
                // Find the farthest drawn point to this x-coordinate (to penalize multiple y-values)
                double farthestDistance = -1;
                double drawnY = actualY; // Default to maximum error (will be replaced if point found)
                boolean pointFound = false;
                
                for (Point p : userDrawnPoints) {
                    double drawnX = graphPanel.screenToX(p.x);
                    double distance = Math.abs(drawnX - x);
                    
                    // Consider points within a certain range (1% of the x-axis width)
                    double threshold = (graphPanel.getXMax() - graphPanel.getXMin()) * 0.01; 
                    
                    if (distance < threshold) {
                        double candidateDrawnY = graphPanel.screenToY(p.y);
                        double yError = Math.abs(actualY - candidateDrawnY);
                        
                        // Select the point with the largest error (farthest from actual)
                        if (farthestDistance < 0 || yError > farthestDistance) {
                            farthestDistance = yError;
                            drawnY = candidateDrawnY;
                            pointFound = true;
                        }
                    }
                }
                
                // Calculate error based on whether a point was found
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
        
        // Calculate final accuracy score
        double averageError = totalError / validPoints;
        double maxError = (graphPanel.getYMax() - graphPanel.getYMin()) * 0.25;
        double accuracy = Math.max(0, 100 * (1 - averageError / maxError));
        
        return accuracy;
    }
}