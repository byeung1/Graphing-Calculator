import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * A class that represents a mathematical function that can be evaluated at any x value
 * Uses the exp4j library to parse and evaluate mathematical expressions
 */
public class Function {
    // The parsed expression object that can be evaluated
    private final Expression expression;
    
    // The original string representation of the expression
    private final String expressionString;

    /**
     * Creates a new Function from a given mathematical expression String
     * The expression should use 'x' as the variable and can include standard mathematical operators and functions supported by the exp4j library
     * 
     * @param expression a mathematical expression to evaluate
     */
    public Function(String expression) {
        this.expression = new ExpressionBuilder(expression).variable("x").build();
        this.expressionString = expression;
    }
    
    /**
     * Creates a new Function with a randomly generated mathematical expression by using Math.Random()
     * The expression is built using a combination of operators, functions (sin, cos, log), and random constants
     * The expression will have 1-4 terms with random operators between them
     */
    public Function() {
        // Define the set of operators and functions that can be used in the expression
        String[] operators = {"+", "-", "*", "/", "^"};
        String[] functions = {"sin", "cos", "tan", "log", "exp"};
        StringBuilder expressionBuilder = new StringBuilder();
        
        // Generate the first term of the expression
        // Can be either the variable x, a constant, or a function of x
        double initialTermType = Math.random();
        
        if (initialTermType < 0.33) {
            // Start with the variable x
            expressionBuilder.append("x");
        }
        else if (initialTermType < 0.66) {
            // Start with a random constant between 0 and 10
            double value = Math.round(Math.random() * 10 * 10) / 10.0;
            expressionBuilder.append(value);
        } 
        else {
            // Start with a random function of x
            String function = functions[(int)(Math.random() * functions.length)];
            expressionBuilder.append(function).append("(x)");
        }
            
        // Add 1-3 additional terms to the expression
        // Each term will be connected to the previous terms with a random operator
        int terms = (int)(Math.random() * 3) + 1;
            
        for (int i = 0; i < terms; i++) {
            // Add a random operator between terms
            String operator = operators[(int)(Math.random() * operators.length)];
            expressionBuilder.append(operator);
                
            // Generate the next term in the expression
            // Can be either the variable x, a constant, or a function of x
            double termType = Math.random();
                
            if (termType < 0.33) {
                // Use the variable x
                expressionBuilder.append("x");
            } else if (termType < 0.66) {
                // Use a random constant between 0 and 10
                double value = Math.round(Math.random() * 10 * 10) / 10.0;
                expressionBuilder.append(value);
            } else {
                // Use a random function of x
                String function = functions[(int)(Math.random() * functions.length)];
                expressionBuilder.append(function).append("(x)");
            }
        }
            
        // Parse the generated expression string into a readable expression
        this.expression = new ExpressionBuilder(expressionBuilder.toString())
                            .variable("x")
                            .build();
        this.expressionString = expressionBuilder.toString();
        System.out.println(this);
    }

    /**
     * Evaluates the function at a given x value
     * Handles errors such as division by 0, and results that are NaN or infinite
     * 
     * @param x The x value at which to evaluate the function
     * @return The y value of the function at x, or Double.NaN if the evaluation fails
     */
    public double evaluate(double x) {
        try {
            // Set the x value and evaluate the expression
            double result = expression.setVariable("x", x).evaluate();
            
            // Check for invalid results
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return Double.NaN;
            }
            return result;
        } catch (ArithmeticException e) {
            // Return NaN for any arithmetic errors (like division by zero)
            return Double.NaN;
        }
    }

    /**
     * Returns the original string representation of the function.
     * This is the expression as it was provided or generated,
     * not the parsed form used for evaluation.
     * 
     * @return The original expression string
     */
    public String getExpressionString() {
        return expressionString;
    }

    /**
     * Overrides the toString() method
     * Returns a human-readable representation of the function.
     * Formats the function as "f(x) = [expression]"
     * 
     * @return A string representation of the function
     */
    @Override
    public String toString() {
        return "f(x) = " + expressionString;
    }
}
