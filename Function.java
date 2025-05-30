import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Function {
    private final Expression expression;
    private final String expressionString;

    //Constructor given an expression string
    public Function(String expression) {
        this.expression = new ExpressionBuilder(expression).variable("x").build();
        this.expressionString = expression;
    }
    
    //Constructor that generates a random expression
    public Function()
    {
        // Operators and functions to be used in the expression
        String[] operators = {"+", "-", "*", "/", "^"};
        String[] functions = {"sin", "cos", "tan", "log", "exp"};
        StringBuilder expressionBuilder = new StringBuilder();
        

        // Randomly decide initial term type: variable, number, or function
        double initialTermType = Math.random();
        
        if (initialTermType < 0.33) {
            // Start with the variable x
            expressionBuilder.append("x");
        }
        else if (initialTermType < 0.66) 
        {
            // Start with a constant
            double value = Math.round(Math.random() * 10 * 10) / 10.0;
            expressionBuilder.append(value);
        } 
        else {
            // Start with a function
            String function = functions[(int)(Math.random() * functions.length)];
            expressionBuilder.append(function).append("(x)");
        }
            
        // Add 1-3 additional terms with operators
        int terms = (int)(Math.random() * 3) + 1;
            
        for (int i = 0; i < terms; i++) {
            // Add a random operator
            String operator = operators[(int)(Math.random() * operators.length)];
            expressionBuilder.append(operator);
                
            // Generate the next term
            double termType = Math.random();
                
            if (termType < 0.33) {
                // Use the variable
                expressionBuilder.append("x");
            } else if (termType < 0.66) {
                // Use a number
                double value = Math.round(Math.random() * 10 * 10) / 10.0;
                expressionBuilder.append(value);
            } else {
                // Use a function
                String function = functions[(int)(Math.random() * functions.length)];
                expressionBuilder.append(function).append("(x)");
                }
            }
            
            // Build the expression
            this.expression = new ExpressionBuilder(expressionBuilder.toString())
                                .variable("x")
                                .build();
            this.expressionString = expressionBuilder.toString();
            System.out.println(this);
    }

    /**
     * Evaluate the function at a given x value
     * @param x the x value to evaluate at
     * @return the y value, or Double.NaN if the evaluation fails
     */
    public double evaluate(double x) {
        try {
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
     * Get the expression string
     * @return the expression string
     */
    public String getExpressionString() {
        return expressionString;
    }

    @Override
    public String toString() {
        return "f(x) = " + expressionString;
    }
}
