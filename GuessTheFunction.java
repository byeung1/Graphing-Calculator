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

    public GuessTheFunction(Runnable onBack) {
        this.onBack = onBack;
        setLayout(new BorderLayout());
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

        // Top panel with back button
        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> onBack.run());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // Center panel with graph
        GraphPanel graphPanel = new GraphPanel(correctFunction, true);
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

        String message = isCorrect
                ? "Correct!"
                : "Incorrect. The correct answer was: " + correctFunction.getExpressionString();

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