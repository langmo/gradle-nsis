package com.github.langmo.gradlensis.examples.extended;
import javax.swing.JOptionPane;

public class Example {
    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Gradle-NSIS extended example running on "+System.getProperty("os.arch")+".", "Gradle-NSIS extended example", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
    }
}