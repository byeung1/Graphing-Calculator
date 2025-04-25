package operations;

/**
 * Abstract class representing a mathematical operation.
 * This class serves as a base for specific operations like Addition, Subtraction, etc.
 */

public abstract class Operation {
    protected final double operand1; // Stores the first operand
    protected final double operand2; // Stores the second operand

    Operation(double operand1, double operand2) { // Constructor
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public abstract double calculate(); // Abstract method for calculation to be implemented by subclasses
}
