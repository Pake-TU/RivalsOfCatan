package view;

import java.util.Scanner;

/**
 * Console-based implementation of IPlayerView.
 * Handles input/output through standard console (System.in/System.out).
 * Follows Single Responsibility Principle by focusing only on console I/O.
 */
public class ConsolePlayerView implements IPlayerView {
    private final Scanner scanner;
    
    /**
     * Constructor with default System.in scanner.
     */
    public ConsolePlayerView() {
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Constructor with custom scanner (useful for testing).
     * @param scanner The scanner to use for input
     */
    public ConsolePlayerView(Scanner scanner) {
        this.scanner = scanner;
    }
    
    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }
    
    @Override
    public String receiveMessage() {
        System.out.print("> ");
        return scanner.nextLine();
    }
}
