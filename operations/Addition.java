package operations;

public class Addition extends Operation {
    private double operand1; // First operand
    private double operand2; // Second operand

    public Addition(double operand1, double operand2) { // Constructor
        super(operand1, operand2); // Calls the constructor of the superclass
    }

    /**
     * Calculates the sum of two operands.
     * @return double
     */
    @Override
    public double calculate() { 
        return operand1 + operand2;
    }
}
