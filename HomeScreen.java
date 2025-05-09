import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends JPanel {

    private static JFrame frame;

    public static void createHomeScreen() {

        //intializes and declares JFrame
        frame = new JFrame("Home");

        //terminate the program when JFrame is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //sets window size
        frame.setSize(800, 600);

        //sets the location to the middle of the screen
        frame.setLocationRelativeTo(null);

        // Add panel to frame
        frame.add(createJPanel());


        frame.setVisible(true);

    }

    private static JPanel createJPanel() {
        // Create a panel for content
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Title label
        JLabel titleLabel = new JLabel("Choose a Game Mode");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        // Button for Game Mode 1
        JButton mode1Button = new JButton("Function Builder");
        mode1Button.setAlignmentX(Component.CENTER_ALIGNMENT);
        mode1Button.setMaximumSize(new Dimension(250, 40));
        mode1Button.setFocusPainted(false);
        mode1Button.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Mode 1 Selected"));

        // Spacer
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Button for Game Mode 2
        JButton mode2Button = new JButton("Draw the Function");
        mode2Button.setAlignmentX(Component.CENTER_ALIGNMENT);
        mode2Button.setMaximumSize(new Dimension(250, 40));
        mode2Button.setFocusPainted(false);
        mode2Button.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Mode 2 Selected"));

        // Add components to panel
        panel.add(titleLabel);
        panel.add(mode1Button);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(mode2Button);

        return panel;
    }
}
