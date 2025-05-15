import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class DrawTheFunction extends JPanel {
    private JLabel functionLabel;
    private Function currentFunction;
    private GraphPanel graphPanel;
    private JPanel graphContainer; // Container for the graph panel
    private List<Point> userDrawnPoints = new ArrayList<>();
    private boolean isDrawing = false;
    private JButton checkButton;
    private JLabel feedbackLabel;
    
    public DrawTheFunction(Runnable onBack) {
        // Setting the layout for the panel
        setLayout(new BorderLayout());

        // Creating a label with a welcome message
        JLabel titleLabel = new JLabel("Welcome to Draw The Function", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Create a panel for the function display
        JPanel topPanel = new JPanel(new BorderLayout());
        functionLabel = new JLabel("Click 'Generate Function' to see a random function", SwingConstants.CENTER);
        functionLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        topPanel.add(functionLabel, BorderLayout.CENTER);
        
        // Create the feedback label
        feedbackLabel = new JLabel("Draw the function using your mouse!", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        
        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Create the generate function button
        JButton generateButton = new JButton("Generate Function");
        generateButton.addActionListener(e -> generateNewFunction());
        
        // Create check button
        checkButton = new JButton("Check My Drawing");
        checkButton.addActionListener(e -> checkDrawing());
        checkButton.setEnabled(false);
        
        // Create clear button
        JButton clearButton = new JButton("Clear Drawing");
        clearButton.addActionListener(e -> clearDrawing());
        
        // Create back button
        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> onBack.run());
        
        // Add buttons to panel
        buttonPanel.add(generateButton);
        buttonPanel.add(checkButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);
        
        // Create graph container
        graphContainer = new JPanel(new BorderLayout());
        
        // Add components to main panel
        add(titleLabel, BorderLayout.NORTH);
        add(graphContainer, BorderLayout.CENTER);
        
        // Create panel for bottom elements
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(topPanel, BorderLayout.NORTH);
        bottomPanel.add(feedbackLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Generate initial function
        generateNewFunction();
    }
    
    /**
     * Generates a new random function and displays it
     */
    private void generateNewFunction() {
        currentFunction = new Function();
        functionLabel.setText("f(x) = " + currentFunction.getExpressionString());
        
        // Clear previous drawing
        userDrawnPoints.clear();
        
        // Create new graph panel
        graphPanel = new GraphPanel(currentFunction);
        graphPanel.setUserDrawnPoints(userDrawnPoints);
        
        // Add mouse listeners to the graph panel
        setupMouseListeners();
        
        // Replace just the graph panel in its container
        graphContainer.removeAll();
        graphContainer.add(graphPanel, BorderLayout.CENTER);
        
        checkButton.setEnabled(false);
        feedbackLabel.setText("Draw the function using your mouse!");
        
        graphContainer.revalidate();
        graphContainer.repaint();
    }
    
    /**
     * Sets up mouse listeners for drawing
     */
    private void setupMouseListeners() {
        graphPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isDrawing = true;
                userDrawnPoints.clear();
                userDrawnPoints.add(e.getPoint());
                graphPanel.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isDrawing = false;
                checkButton.setEnabled(true);
            }
        });
        
        graphPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDrawing) {
                    userDrawnPoints.add(e.getPoint());
                    graphPanel.repaint();
                }
            }
        });
    }
    
    /**
     * Checks the user's drawing against the actual function
     */
    private void checkDrawing() {
        double accuracy = calculateAccuracy();
        int score = (int)(accuracy * 100);
        
        if (score > 90) {
            feedbackLabel.setText("Excellent! Your score: " + score + "/100");
        } else if (score > 70) {
            feedbackLabel.setText("Good job! Your score: " + score + "/100");
        } else if (score > 50) {
            feedbackLabel.setText("Nice try! Your score: " + score + "/100");
        } else {
            feedbackLabel.setText("Keep practicing! Your score: " + score + "/100");
        }
    }
    
    /**
     * Calculates the accuracy of the user's drawing
     */
    private double calculateAccuracy() {
        if (userDrawnPoints.size() < 2) {
            return 0.0;
        }
        
        int width = graphPanel.getWidth();
        int height = graphPanel.getHeight();
        int originX = width / 2;
        int originY = height / 2;
        double xScale = 50;
        double yScale = 50;
        
        double totalError = 0.0;
        int validPoints = 0;
        
        for (Point p : userDrawnPoints) {
            // Convert screen coordinates to graph coordinates
            double x = (p.x - originX) / xScale;
            
            // Calculate expected y value based on the function
            double expectedY = currentFunction.evaluate(x);
            
            // Convert actual y from screen coordinates to graph coordinates
            double actualY = (originY - p.y) / yScale;
            
            // Only include points where the function is defined
            if (Double.isFinite(expectedY)) {
                double error = Math.abs(expectedY - actualY);
                totalError += error;
                validPoints++;
            }
        }
        
        if (validPoints == 0) {
            return 0.0;
        }
        
        double averageError = totalError / validPoints;
        // Convert error to an accuracy score (higher is better)
        return Math.max(0, 1.0 - Math.min(1.0, averageError / 2.0));
    }
    
    /**
     * Clears the user's drawing
     */
    private void clearDrawing() {
        userDrawnPoints.clear();
        graphPanel.repaint();
        checkButton.setEnabled(false);
        feedbackLabel.setText("Draw the function using your mouse!");
    }
}
