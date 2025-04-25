package operations;

public class Division extends Operation {
    private double operand1; // First operand
    private double operand2; // Second operand

    public Division(double operand1, double operand2) { // Constructor
        super(operand1, operand2); // Calls the constructor of the superclass
    }

    /**
     * Calculates the quotient of two operands.
     * @return double
     */
    @Override
    public double calculate() { 
        if (operand2 == 0) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        return operand1 / operand2;
    }
}
