import javax.swing.*;
import java.awt.*;

/**
 * The main home screen of the application that provides navigation to different game modes
 * Displays a clean interface with two buttons for selecting between the modes
 * Uses CardLayout for smooth transitions between screens
 */
public class HomeScreen extends JPanel {
    // Declaring the main application window, the panel that holds the screens, and the layout manager for switching between screens
    private static JFrame frame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;
    
    // The "Guess The Function" game screen
    // Stored as a field to allow resetting the game state
    private static GuessTheFunction guessTheFunctionScreen;

    /**
     * Creates and displays the home screen with navigation buttons.
     * Sets up the main window, layout, and all game mode screens.
     * The home screen is shown by default when the application starts.
     */
    public static void createHomeScreen() {
        // Create and configure the main window
        frame = new JFrame("Function Bee");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Set up the card layout for managing the homescreen, and screens for the two modes
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create game mode screens with navigation callbacks
        guessTheFunctionScreen = new GuessTheFunction(() -> {
            cardLayout.show(mainPanel, "Home");
        });
        JPanel mode2Screen = new DrawTheFunction(() -> cardLayout.show(mainPanel, "Home"));

        // Add all screens to the card layout
        mainPanel.add(guessTheFunctionScreen, "Mode1");
        mainPanel.add(mode2Screen, "Mode2");
        mainPanel.add(createJPanel(), "Home");
        
        // Show the home screen by default
        cardLayout.show(mainPanel, "Home");
        frame.add(mainPanel);

        frame.setVisible(true);
    }

    /**
     * Creates the main panel containing the game mode selection buttons
     * Includes a title and two coloured buttons for each game mode
     * 
     * @return A JPanel containing the home screen UI elements
     */
    private static JPanel createJPanel() {
        // Create and configure the main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Create the title label
        JLabel titleLabel = new JLabel("Choose a Game Mode");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        // Create the "Guess The Function" button
        JButton mode1Button = new JButton("Guess The Function");
        mode1Button.setAlignmentX(Component.CENTER_ALIGNMENT);
        mode1Button.setMaximumSize(new Dimension(250, 40));
        mode1Button.setFocusPainted(false);
        mode1Button.setOpaque(true);
        mode1Button.setBorderPainted(false);
        mode1Button.setBackground(new Color(0, 0, 128)); // Dark navy blue
        mode1Button.setForeground(Color.WHITE);
        mode1Button.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Add action listener to switch to game mode 1
        mode1Button.addActionListener(e -> {
            guessTheFunctionScreen.setupRound();
            cardLayout.show(mainPanel, "Mode1");
        });

        // Add spacing between buttons for aesthetics
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create the "Draw The Function" button
        JButton mode2Button = new JButton("Draw the Function");
        mode2Button.setAlignmentX(Component.CENTER_ALIGNMENT);
        mode2Button.setMaximumSize(new Dimension(250, 40));
        mode2Button.setFocusPainted(false);
        mode2Button.setOpaque(true);
        mode2Button.setBorderPainted(false);
        mode2Button.setBackground(new Color(139, 0, 0)); // Dark red (dark crimson)
        mode2Button.setForeground(Color.WHITE);
        mode2Button.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Add action listener to switch to game mode 2
        mode2Button.addActionListener(e -> cardLayout.show(mainPanel, "Mode2"));

        // Add all components to the panel
        panel.add(titleLabel);
        panel.add(mode1Button);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(mode2Button);

        return panel;
    }
}
