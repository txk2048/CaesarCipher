package org.txk64;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;

import javax.swing.*;

public class CaesarCipher extends JFrame {
    private static Pattern shiftPattern = Pattern.compile("ROT(\\d+)");
    private static String lowerCaseAlphabet = "abcdefghijklmnopqrstuvwxyz";
    private static String upperCaseAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    JTextArea inputTextBox;
    JScrollPane inputTextScrollPane;

    JComboBox<String> shiftSelector;
	JComboBox<String> modeSelector;
    JButton encryptButton;

    JTextArea outputTextBox;
    JScrollPane outputTextScrollPane;

    public CaesarCipher() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setSize(300, 300);
        setTitle("Caesar Cipher");

        GridBagConstraints constraints = new GridBagConstraints();

        // Input text box
        inputTextBox = new JTextArea();
        inputTextBox.setLineWrap(true);
        inputTextBox.setWrapStyleWord(true);

        inputTextScrollPane =
                new JScrollPane(
                        inputTextBox,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(10, 10, 5, 10);
        constraints.weightx = 1.0d;
        constraints.weighty = 0.5d;
        add(inputTextScrollPane, constraints);

        // Shift selector (ROT1 through ROT25)
        shiftSelector = new JComboBox<String>();
        for (int n = 1; n < 26; ++n) shiftSelector.addItem(String.format("ROT%d", n));

        shiftSelector.setFocusable(false);

        // Set default item to be ROT13
        shiftSelector.setSelectedItem("ROT13");

        constraints.gridwidth = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(5, 10, 5, 5);
        constraints.weightx = 0.1d;
        constraints.weighty = 0.d;
        add(shiftSelector, constraints);
		
		// operation mode selector
		modeSelector = new JComboBox<String>(new String[] {"Encrypt", "Decrypt"});
		modeSelector.setFocusable(false);
		
		constraints.gridx = 1;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 0.1d;
		add(modeSelector, constraints);

        // Encrypt button
        encryptButton = new JButton("Go");
        encryptButton.setFocusable(false);
        encryptButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        CaesarCipher.this.onGoButtonClicked();
                    }
                });

        constraints.gridx = 2;
        constraints.insets = new Insets(5, 5, 5, 10);
        constraints.weightx = 0.8d;
        add(encryptButton, constraints);

        // output text display box
        outputTextBox = new JTextArea();
        outputTextBox.setEditable(false);
        outputTextBox.setFocusable(true);
        outputTextBox.setLineWrap(true);
        outputTextBox.setWrapStyleWord(true);

        outputTextScrollPane =
                new JScrollPane(
                        outputTextBox,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(10, 5, 5, 10);
        constraints.weightx = 1.0d;
        constraints.weighty = 0.5d;
        add(outputTextScrollPane, constraints);
    }

    private void onGoButtonClicked() {
		final String input = inputTextBox.getText();
		final String mode = (String)modeSelector.getSelectedItem();
		int key;
		String output = null;
		
		Matcher m = shiftPattern.matcher(
						(String)shiftSelector.getSelectedItem());
						
		if (!m.matches()) {
			System.err.println("Error: Bug detected: Shift selector regex did not match");
			System.exit(1);
		}
		
		key = Integer.parseInt(m.group(1));
		if (mode.equals("Encrypt")) {
			output = performShift(input, key);
		} else if (mode.equals("Decrypt")) {
			// Reversing the key undoes the cipher
			key = 26 - key;
			output = performShift(input, key);
		} else {
			System.err.println("Error: Bug detected: Unsupported operation mode");
			System.exit(1);
		}
		
		outputTextBox.setText(output);
		outputTextBox.setCaretPosition(0);
    }

    private static String performShift(final String input, final int key) {
        StringBuilder output = new StringBuilder(input.length());

        char[] inputChars = input.toCharArray();
        for (char c : inputChars) {
            if ('a' <= c && c <= 'z') {
                int currentPosition = lowerCaseAlphabet.indexOf(c);
                int newPosition = (currentPosition + key) % 26;
                output.append(lowerCaseAlphabet.charAt(newPosition));
            } else if ('A' <= c && c <= 'Z') {
                int currentPosition = upperCaseAlphabet.indexOf(c);
                int newPosition = (currentPosition + key) % 26;
                output.append(upperCaseAlphabet.charAt(newPosition));
            } else {
                output.append(c);
            }
        }

        return output.toString();
    }

    public static void main(final String[] args)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException,
                    UnsupportedLookAndFeelException {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        CaesarCipher window = new CaesarCipher();
                        window.setVisible(true);
                    }
                });
    }
}
