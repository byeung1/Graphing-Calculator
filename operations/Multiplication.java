package operations;

public class Multiplication extends Operation {
    private double operand1; // First operand
    private double operand2; // Second operand

    public Multiplication(double operand1, double operand2) { // Constructor
        super(operand1, operand2); // Calls the constructor of the superclass
    }

    /**
     * Calculates the product of two operands.
     * @return double
     */
    @Override
    public double calculate() { 
        return operand1 * operand2;
    }
}
