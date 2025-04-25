import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class DynamicGrapher extends JPanel {
    private final List<Expression> expressions = new ArrayList<>();
    private final int width = 800, height = 600;
    private final double xMin = -10, xMax = 10, yMin = -10, yMax = 10;

    public void updateExpressions(List<String> functions) {
        expressions.clear();
        for (String func : functions) {
            try {
                expressions.add(new ExpressionBuilder(func).variable("x").build());
            } catch (Exception ignored) {
                // Skip invalid functions
            }
        }
        repaint();
    }

    public DynamicGrapher() {
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Axes
        int xAxis = (int) (height * yMax / (yMax - yMin));
        int yAxis = (int) (-xMin * width / (xMax - xMin));
        g2.setColor(Color.GRAY);
        g2.drawLine(0, xAxis, width, xAxis);
        g2.drawLine(yAxis, 0, yAxis, height);

        // Colors for graphs
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.PINK};

        // Plot each function
        for (int idx = 0; idx < expressions.size(); idx++) {
            Expression expr = expressions.get(idx);
            g2.setColor(colors[idx % colors.length]);
            for (int i = 0; i < width; i++) {
                double x = xMin + i * (xMax - xMin) / width;
                try {
                    double y = expr.setVariable("x", x).evaluate();
                    int j = (int) ((yMax - y) * height / (yMax - yMin));
                    g2.fillRect(i, j, 1, 1);
                } catch (Exception ignored) {}
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dynamic Graphing Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Graph panel
        DynamicGrapher graphPanel = new DynamicGrapher();

        // Function inputs panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // Scroll pane in case user adds a lot of inputs
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setPreferredSize(new Dimension(300, 600));

        // Function list (JTextFields)
        List<JTextField> functionFields = new ArrayList<>();

        // Update graph from current fields
        Runnable updateGraph = () -> {
            List<String> functions = new ArrayList<>();
            for (JTextField field : functionFields) {
                if (!field.getText().trim().isEmpty()) {
                    functions.add(field.getText().trim());
                }
            }
            graphPanel.updateExpressions(functions);
        };

        // Function to add a new input field
        Runnable addFunctionField = () -> {
            JPanel line = new JPanel(new BorderLayout());
            JTextField field = new JTextField("x");
            JButton delete = new JButton("×");
            functionFields.add(field);
            line.add(field, BorderLayout.CENTER);
            line.add(delete, BorderLayout.EAST);

            field.addActionListener(e -> updateGraph.run());
            delete.addActionListener((ActionEvent e) -> {
                functionFields.remove(field);
                inputPanel.remove(line);
                inputPanel.revalidate();
                inputPanel.repaint();
                updateGraph.run();
            });

            inputPanel.add(line);
            inputPanel.revalidate();
            updateGraph.run();
        };

        // Add Function Button
        JButton addBtn = new JButton("Add Function");
        addBtn.addActionListener(e -> addFunctionField.run());

        // Top panel with add button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(addBtn, BorderLayout.WEST);

        // Add default function field
        addFunctionField.run();

        // Layout
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.WEST);
        frame.add(graphPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
