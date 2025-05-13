import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Function {
    private final Expression expression;

    //Constructor given an expression string
    public Function(String expression) {
        this.expression = new ExpressionBuilder(expression).variable("x").build();
    }

    /**
     * Evaluate the function at a given x value
     * @param x
     * @return
     */
    public double evaluate(double x) {
        return expression.setVariable("x", x).evaluate();
    }

    /**
     * Get the expression string
     * @return
     */
    public Expression getExpression() {
        return expression;
    }
}
