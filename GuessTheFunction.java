import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * A game mode where players try to identify a mathematical function from its graph.
 * The game displays a function's graph and provides four possible expressions to choose from.
 * Players can track their score as they play multiple rounds, and can return to the home
 * screen at any time.
 */
public class GuessTheFunction extends JPanel {
    // The function that the player is trying to guess
    private Function correctFunction;
    
    // Callback function to return to the home screen
    private final Runnable onBack;
    
    // Random number generator for creating new functions
    private final Random random = new Random();
    
    // Total number of questions attempted
    private int totalQuestions = 0;
    
    // Number of correct answers given
    private int correctAnswers = 0;
    
    // Label displaying the current score
    private JLabel scoreLabel;

    /**
     * Creates a new GuessTheFunction game panel
     * Sets up the UI with a score display and prepares for the first round
     * 
     * @param onBack Callback function to return to the home screen
     */
    public GuessTheFunction(Runnable onBack) {
        this.onBack = onBack;
        setLayout(new BorderLayout());
        
        // Create the score label
        scoreLabel = new JLabel("Score: 0/0", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        setupRound();
    }

    /**
     * Sets up a new round of the game
     * Generates a new function and creates four possible answers
     * Updates the UI to show the function's graph and answer choices
     */
    public void setupRound() {
        // Clear UI
        removeAll(); 

        // Generate the correct function
        correctFunction = new Function();
        String correctExpr = correctFunction.getExpressionString();

        // Use a Set to ensure unique expressions
        Set<String> expressionSet = new HashSet<>();
        expressionSet.add(correctExpr);

        ArrayList<String> choices = new ArrayList<>();
        choices.add(correctExpr);

        // Generate 3 additional unique incorrect functions
        while (choices.size() < 4) {
            Function candidate = new Function();
            String expr = candidate.getExpressionString();
            if (expressionSet.add(expr)) {
                choices.add(expr);
            }
        }

        // Shuffle the answer choices
        Collections.shuffle(choices);

        // Create and configure the top panel with back button and score
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("← Back to Home");
        backButton.setBackground(new Color(139, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> onBack.run());
        
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(scoreLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Create and configure the graph panel
        FunctionGraphPanel graphPanel = new FunctionGraphPanel(correctFunction, true);
        graphPanel.hideUndoButton();
        add(graphPanel, BorderLayout.CENTER);

        // Create and configure the answer choice buttons
        JPanel choicesPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (String option : choices) {
            JButton optionButton = new JButton(option);
            optionButton.addActionListener(e -> handleAnswer(option));
            choicesPanel.add(optionButton);
        }

        // Add padding around the choices panel
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        bottomWrapper.add(choicesPanel, BorderLayout.CENTER);
        add(bottomWrapper, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Handles the player's answer selection
     * Updates the score and shows a dialog with the result
     * Gi
     * 
     * @param selected The expression that the player selected
     */
    private void handleAnswer(String selected) {
        // Check if the answer is correct and update score
        boolean isCorrect = selected.equals(correctFunction.getExpressionString());
        totalQuestions++;
        if (isCorrect) {
            correctAnswers++;
        }
        scoreLabel.setText(String.format("Score: %d/%d", correctAnswers, totalQuestions));

        // Show result dialog
        String message = isCorrect ? "Correct!" : "Incorrect. The correct answer was: " + correctFunction.getExpressionString();

        int result = JOptionPane.showOptionDialog(
                this,
                message + "\n\nPlay another round?",
                "Result",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Next", "Back to Home"},
                "Next"
        );

        // Handle player's choice
        if (result == JOptionPane.YES_OPTION) {
            setupRound();
        } else {
            onBack.run();
        }
    }
}