import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GuessTheFunction extends JPanel {
    public GuessTheFunction(Runnable onBack) {
        setLayout(new BorderLayout());


        // Example: y = sin(x)
        //Function func = new Function("sin(x)");
        //ArrayList<Function> func = new ArrayList<>();
        //func.add(new Function());
        //func.add(new Function());
        GraphPanel panel = new GraphPanel(new Function());
            

        JButton backButton = new JButton("← Back to Home");
        backButton.addActionListener(e -> onBack.run());

        add(panel, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }
}
