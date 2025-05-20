import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DrawTheFunction extends JPanel {
    private JLabel functionLabel;
    private JLabel accuracyLabel;
    private Function currentFunction;
    private GraphPanel graphPanel;
    private boolean isDrawingMode = true;
    private List<Point> userDrawnPoints = new ArrayList<>();
    
    public DrawTheFunction(Runnable onBack) {
        // Setting the layout for the panel
        setLayout(new BorderLayout());

        // Creating a label with a welcome message
        JLabel titleLabel = new JLabel("Welcome to Draw The Function", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Create a panel for the function display
        JPanel functionPanel = new JPanel(new BorderLayout());
        functionLabel = new JLabel("Click 'Generate Function' to see a random function", SwingConstants.CENTER);
        functionLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        accuracyLabel = new JLabel("", SwingConstants.CENTER);
        accuracyLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        functionPanel.add(functionLabel, BorderLayout.NORTH);
        functionPanel.add(accuracyLabel, BorderLayout.SOUTH);
        
        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Create the generate function button
        JButton generateButton = new JButton("Generate Function");
        generateButton.addActionListener(e -> generateNewFunction());
        
        // Create confirm button
        JButton confirmButton = new JButton("Confirm Drawing");
        confirmButton.addActionListener(e -> confirmDrawing());
        
        // Create back button
        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> onBack.run());
        
        // Add buttons to panel
        buttonPanel.add(generateButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);
        
        // Create a panel to hold both the graph and function info
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Add components to main panel
        add(titleLabel, BorderLayout.NORTH);
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
        accuracyLabel.setText("");
        userDrawnPoints.clear();
        isDrawingMode = true;
        
        if (graphPanel != null) {
            remove(graphPanel);
        }
        
        graphPanel = new GraphPanel(currentFunction, false);
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
            graphPanel.setDrawingMode(false);
            graphPanel.setDrawFunction(true);
            graphPanel.setUserDrawnPoints(userDrawnPoints);
            graphPanel.setControlsEnabled(false);
            repaint();
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
        
        // Sample points along the x-axis to check for missing y-values
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
                
                // Find the closest point in the user's drawing for this x value
                double drawnY = 0; // Default to y=0 if no point found
                double minXDiff = Double.MAX_VALUE;
                
                for (Point p : userDrawnPoints) {
                    double drawnX = graphPanel.screenToX(p.x);
                    double xDiff = Math.abs(drawnX - x);
                    
                    if (xDiff < minXDiff) {
                        minXDiff = xDiff;
                        drawnY = graphPanel.screenToY(p.y);
                    }
                }
                
                // Calculate error for this point
                double error = Math.abs(actualY - drawnY);
                totalError += error;
                validPoints++;
                
            } catch (ArithmeticException e) {
                // Skip points where the function evaluation fails (e.g., division by zero)
                continue;
            }
        }
        
        if (validPoints == 0) return 0.0;
        
        double averageError = totalError / validPoints;
        double maxError = (graphPanel.getYMax() - graphPanel.getYMin()) * 0.5; // 50% of the y-range
        double accuracy = Math.max(0, 100 * (1 - averageError / maxError));
        
        return accuracy;
    }
}