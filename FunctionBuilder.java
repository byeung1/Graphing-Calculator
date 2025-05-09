import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class FunctionBuilder extends JPanel {
    public FunctionBuilder(Runnable onBack) {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("🎯 Welcome to Guess the Function", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> onBack.run());

        add(label, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }
}
