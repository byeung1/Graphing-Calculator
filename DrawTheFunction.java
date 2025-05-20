import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawTheFunction extends JPanel {
    private JLabel functionLabel;
    private Function currentFunction;
    
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
        functionPanel.add(functionLabel, BorderLayout.CENTER);
        
        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Create the generate function button
        JButton generateButton = new JButton("Generate Function");
        generateButton.addActionListener(e -> generateNewFunction());
        
        // Create back button
        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> onBack.run());
        
        // Add buttons to panel
        buttonPanel.add(generateButton);
        buttonPanel.add(backButton);
        
        // Add components to main panel
        add(titleLabel, BorderLayout.NORTH);
        add(functionPanel, BorderLayout.CENTER);
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
        GraphPanel graphPanel = new GraphPanel(currentFunction, true);
        add(graphPanel, BorderLayout.CENTER);
    }
}