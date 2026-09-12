package dunos.apps;

import dunos.ui.*;
import dunos.ui.Window;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * DunDunDunOS Calculator - A standard calculator application.
 * Supports basic arithmetic operations with a clean UI.
 */
public class CalculatorApp {

    private StringBuilder currentInput;
    private double firstNumber;
    private String operator;
    private boolean newInput;
    private JLabel displayLabel;

    public CalculatorApp() {
        currentInput = new StringBuilder("0");
        firstNumber = 0;
        operator = "";
        newInput = false;
    }

    public Window createWindow() {
        Window window = new Window("Calculator", "calculator", null);
        window.setSize(320, 480);
        window.setResizable(false);
        window.setMinimizable(true);
        window.setMaximizable(false);

        JPanel content = window.getContentArea();
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Display
        displayLabel = new JLabel("0", SwingConstants.RIGHT);
        displayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 42));
        displayLabel.setForeground(new Color(235, 235, 235));
        displayLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50), 1),
            new EmptyBorder(15, 10, 15, 10)
        ));
        content.add(displayLabel, BorderLayout.NORTH);

        // Button grid
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 4, 4));
        buttonPanel.setOpaque(false);

        String[][] buttons = {
            {"C", "±", "%", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "−"},
            {"1", "2", "3", "+"},
            {"0", ".", "⌫", "="}
        };

        for (String[] row : buttons) {
            for (String text : row) {
                JButton btn = createButton(text);
                buttonPanel.add(btn);
            }
        }

        content.add(buttonPanel, BorderLayout.CENTER);

        return window;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boolean isNumber = text.matches("[0-9.]");
        boolean isOperator = text.matches("[÷×−+=]");
        boolean isSpecial = text.matches("[C±%⌫]");

        if (isOperator || text.equals("=")) {
            btn.setBackground(new Color(0, 100, 180));
            btn.setForeground(Color.WHITE);
        } else if (isSpecial) {
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(new Color(235, 235, 235));
        } else {
            btn.setBackground(new Color(40, 40, 40));
            btn.setForeground(new Color(235, 235, 235));
        }

        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50)),
            new EmptyBorder(8, 8, 8, 8)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(btn.getBackground().brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boolean isOp = text.matches("[÷×−+=]") || text.equals("=");
                btn.setBackground(isOp ? new Color(0, 100, 180) : 
                    text.matches("[C±%⌫]") ? new Color(60, 60, 60) : new Color(40, 40, 40));
            }
        });

        btn.addActionListener(e -> handleButton(text));
        return btn;
    }

    private void handleButton(String text) {
        switch (text) {
            case "C" -> clear();
            case "⌫" -> backspace();
            case "±" -> negate();
            case "=" -> calculate();
            case "÷", "×", "−", "+" -> handleOperator(text);
            case "%" -> percent();
            case "." -> addDecimal();
            default -> addDigit(text);
        }
    }

    private void clear() {
        currentInput = new StringBuilder("0");
        firstNumber = 0;
        operator = "";
        newInput = false;
        updateDisplay();
    }

    private void backspace() {
        if (currentInput.length() > 1) {
            currentInput.deleteCharAt(currentInput.length() - 1);
        } else {
            currentInput = new StringBuilder("0");
        }
        updateDisplay();
    }

    private void negate() {
        if (currentInput.charAt(0) == '-') {
            currentInput.deleteCharAt(0);
        } else {
            currentInput.insert(0, '-');
        }
        updateDisplay();
    }

    private void addDigit(String digit) {
        if (newInput || currentInput.toString().equals("0")) {
            currentInput = new StringBuilder(digit);
            newInput = false;
        } else {
            currentInput.append(digit);
        }
        updateDisplay();
    }

    private void addDecimal() {
        if (newInput) {
            currentInput = new StringBuilder("0.");
            newInput = false;
        } else if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
        }
        updateDisplay();
    }

    private void handleOperator(String op) {
        if (!operator.isEmpty()) {
            calculate();
        }
        firstNumber = Double.parseDouble(currentInput.toString());
        operator = switch (op) {
            case "÷" -> "/";
            case "×" -> "*";
            case "−" -> "-";
            default -> "+";
        };
        newInput = true;
    }

    private void calculate() {
        if (operator.isEmpty()) return;
        double secondNumber = Double.parseDouble(currentInput.toString());
        double result = switch (operator) {
            case "/" -> secondNumber != 0 ? firstNumber / secondNumber : 0;
            case "*" -> firstNumber * secondNumber;
            case "-" -> firstNumber - secondNumber;
            default -> firstNumber + secondNumber;
        };

        currentInput = new StringBuilder(formatNumber(result));
        operator = "";
        newInput = true;
        updateDisplay();
    }

    private void percent() {
        double value = Double.parseDouble(currentInput.toString()) / 100;
        currentInput = new StringBuilder(formatNumber(value));
        updateDisplay();
    }

    private String formatNumber(double num) {
        if (num == (long) num) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);
    }

    private void updateDisplay() {
        displayLabel.setText(currentInput.toString());
    }
}

