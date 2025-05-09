import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawTheFunction extends JPanel {
    public DrawTheFunction(Runnable onBack) {
        // Setting the layout for the panel
        setLayout(new BorderLayout());

        // Creating a label with a welcome message
        JLabel label = new JLabel("🧩 Welcome to Transformation Challenge", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> onBack.run());

        add(label, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }
}
