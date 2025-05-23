import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * GuessTheFunction panel displays a graph and four options to guess the function expression.
 */
public class GuessTheFunction extends JPanel {

    private Function correctFunction;
    private final Runnable onBack;
    private final Random random = new Random();
    private int totalQuestions = 0;
    private int correctAnswers = 0;
    private JLabel scoreLabel;

    public GuessTheFunction(Runnable onBack) {
        this.onBack = onBack;
        setLayout(new BorderLayout());
        
        // Create score label
        scoreLabel = new JLabel("Score: 0/0", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        setupRound();
    }

    private void setupRound() {
        removeAll(); // Clear UI

        // Generate the correct function
        correctFunction = new Function();
        String correctExpr = correctFunction.getExpressionString();

        // Use a Set to guarantee unique expressions
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

        // Shuffle choices
        Collections.shuffle(choices);

        // Top panel with back button and score
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("← Back to Home");
        // Style the back button to be dark red
        backButton.setBackground(new Color(139, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> onBack.run());
        
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(scoreLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center panel with graph
        FunctionGraphPanel graphPanel = new FunctionGraphPanel(correctFunction, true);
        // Hide the undo button in the control panel
        graphPanel.hideUndoButton();
        add(graphPanel, BorderLayout.CENTER);

        // Bottom panel with multiple choice buttons
        JPanel choicesPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (String option : choices) {
            JButton optionButton = new JButton(option);
            optionButton.addActionListener(e -> handleAnswer(option));
            choicesPanel.add(optionButton);
        }

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        bottomWrapper.add(choicesPanel, BorderLayout.CENTER);
        add(bottomWrapper, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void handleAnswer(String selected) {
        boolean isCorrect = selected.equals(correctFunction.getExpressionString());
        totalQuestions++;
        if (isCorrect) {
            correctAnswers++;
        }
        scoreLabel.setText(String.format("Score: %d/%d", correctAnswers, totalQuestions));

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

        if (result == JOptionPane.YES_OPTION) {
            setupRound();
        } else {
            onBack.run();
        }
    }
}