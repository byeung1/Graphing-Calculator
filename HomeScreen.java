import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends JPanel {

    //declares variables for application windows, screens and layout manager
    private static JFrame frame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;

    /**
     * Creates the home screen with two buttons to select game model
     */
    public static void createHomeScreen() {

        //intializes and declares JFrame
        frame = new JFrame("Function Bee");

        //terminate the program when JFrame is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //sets window size
        frame.setSize(800, 600);

        //sets the location to the middle of the screen
        frame.setLocationRelativeTo(null);

        //set up CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        //Initializes game mode panels with lambdas to return to home
        JPanel mode1Screen = new GuessTheFunction(() -> cardLayout.show(mainPanel, "Home"));
        JPanel mode2Screen = new DrawTheFunction(() -> cardLayout.show(mainPanel, "Home"));

        // Add screens to the card layout
        mainPanel.add(mode1Screen, "Mode1");
        mainPanel.add(mode2Screen, "Mode2");

        // Add panel to frame
        mainPanel.add(createJPanel(), "Home");
        //shows the home panel by default
        cardLayout.show(mainPanel, "Home");
        frame.add(mainPanel);


        frame.setVisible(true);

    }

    /**
     * Creates the main JPanel with buttons for each game mode
     * @return
     */
    private static JPanel createJPanel() {
        //create a panel for UI
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        //title label with fonts 
        JLabel titleLabel = new JLabel("Choose a Game Mode");
        //aligns to the center
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        //sets the font
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        // Button for Game Mode 1 that switches the layout upon click
        JButton mode1Button = new JButton("Guess The Function");
        //centers the button
        mode1Button.setAlignmentX(Component.CENTER_ALIGNMENT);
        mode1Button.setMaximumSize(new Dimension(250, 40));
        mode1Button.setFocusPainted(false);
        mode1Button.setOpaque(true);
        mode1Button.setBorderPainted(false);
        mode1Button.setBackground(new Color(0, 0, 128)); // Dark navy blue
        mode1Button.setForeground(Color.WHITE);
        mode1Button.setFont(new Font("SansSerif", Font.BOLD, 14));
        //switch to the "Mode1" when clicked
        mode1Button.addActionListener(e -> cardLayout.show(mainPanel, "Mode1"));

        // Spacer for aethetics
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Button for Game Mode 2 that switches the layout upon click
        JButton mode2Button = new JButton("Draw the Function");
        mode2Button.setAlignmentX(Component.CENTER_ALIGNMENT);
        mode2Button.setMaximumSize(new Dimension(250, 40));
        mode2Button.setFocusPainted(false);
        mode2Button.setOpaque(true);
        mode2Button.setBorderPainted(false);
        mode2Button.setBackground(new Color(139, 0, 0)); // Dark red (dark crimson)
        mode2Button.setForeground(Color.WHITE);
        mode2Button.setFont(new Font("SansSerif", Font.BOLD, 14));
        //switch to the "Mode2" when clicked
        mode2Button.addActionListener(e -> cardLayout.show(mainPanel, "Mode2"));

        // Adding each component to panel
        panel.add(titleLabel);
        panel.add(mode1Button);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(mode2Button);

        return panel;
    }
}
