import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class WordGuessingGame extends JFrame {
    private ArrayList<String[]> questionData = new ArrayList<>();
    private int currentQuestionIndex = 0, score = 5, attempts = 0, clueIndex = 0;
    private JLabel questionLabel, scoreLabel, attemptsLabel;
    private JTextField answerField;
    private JTextArea cluesArea;

    public WordGuessingGame() {
        if (readCSV()) {
            initializeUI();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error reading the CSV file. Please check the " +
                            "file and try again.");
            System.exit(1);
        }
    }

    private boolean readCSV() {
        File file = new File("src/questions.csv");

        if (!file.exists() || file.isDirectory()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    questionData.add(data);
                } else {
                    System.err.println("Skipping invalid line: " + line);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void initializeUI() {
        setTitle("Word Guessing Game");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        questionLabel = new JLabel("Question: " + questionData.get(currentQuestionIndex)[0], SwingConstants.CENTER);
        topPanel.add(questionLabel);

        answerField = new JTextField(20);
        topPanel.add(answerField);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(actionEvent -> checkAnswer());
        buttonPanel.add(submitButton);

        JButton clueButton = new JButton("Clue");
        clueButton.addActionListener(actionEvent -> provideClue());
        buttonPanel.add(clueButton);

        topPanel.add(buttonPanel);
        add(topPanel, BorderLayout.NORTH);

        cluesArea = new JTextArea(5, 30);
        cluesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cluesArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new GridLayout(1, 2));
        scoreLabel = new JLabel("Score: " + score);
        statusPanel.add(scoreLabel);

        attemptsLabel = new JLabel("Attempts: " + attempts);
        statusPanel.add(attemptsLabel);

        add(statusPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void provideClue() {
        if (clueIndex < 3 && score > 1) {
            String[] clues = Arrays.copyOfRange(questionData.get(currentQuestionIndex), 2, 5);
            String clueToShow = clues[clueIndex];
            cluesArea.append("Clue " + (clueIndex + 1) + ": " + clueToShow + "\n");
            clueIndex++;
            score--;
            scoreLabel.setText("Score: " + score);
        } else if (clueIndex >= 3) {
            JOptionPane.showMessageDialog(this, "No more clues available.");
        } else {
            JOptionPane.showMessageDialog(this, "Not enough points for a clue.");
        }
    }

    private void checkAnswer() {
        if (questionData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No question available.");
            return;
        }

        String answer = answerField.getText().trim();
        if (answer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a guess.");
            return;
        }

        String correctAnswer = questionData.get(currentQuestionIndex)[1];
        if (answer.equalsIgnoreCase(correctAnswer)) {
            JOptionPane.showMessageDialog(this, "Correct! Your score: " + score);
            resetGame();
        } else {
            attempts++;
            attemptsLabel.setText("Attempts: " + attempts);
            if (attempts >= 10) {
                JOptionPane.showMessageDialog(this, "Out of attempts! The correct word was: " + correctAnswer);
                resetGame();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong answer. Try again!");
            }
        }
    }

    private void resetGame() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionData.size();
        questionLabel.setText("Question: " + questionData.get(currentQuestionIndex)[0]);
        score = 5;
        scoreLabel.setText("Score: " + score);
        attempts = 0;
        attemptsLabel.setText("Attempts: " + attempts);
        answerField.setText("");
        clueIndex = 0;
        cluesArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WordGuessingGame::new);
    }
}
