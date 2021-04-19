package View;

import javax.swing.*;

public abstract class AppFrame extends JFrame {

    public abstract void initialize();

    public void displayErrorMessage(Exception exception) {
        if (exception != null) {
            String message = exception.getMessage();
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}