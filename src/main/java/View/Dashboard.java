package View;

import Controller.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Interface class
 * This class manages the tools that generate the graphical user interface*/
public class Dashboard extends AppFrame{

    private JTextField clientsField;
    private JTextField queuesField;
    private JTextField timeField;
    private JTextField arrivalMinField;
    private JTextField arrivalMaxField;
    private JTextField serviceMinField;
    private JTextField serviceMaxField;
    private JTextArea updateTextArea;
    private JButton startButton;
    SimulationManager simulationManager;
    String path = "D:\\Documente\\Facultate\\An 2\\Semestrul 2\\FPT\\Laboratory\\Assignment 2\\resultset.txt";
    FileWriter fileWriter;

    @Override
    public void initialize() {

        this.setTitle("Queue simulator");
        this.setSize(600, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);

        try {
            fileWriter = new FileWriter(path);
            fileWriter.write("");
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error while writing in file");
            e.printStackTrace();
        }

        JPanel panel1 = new JPanel();
        panel1.setLayout(null);

        initializeSimulator(panel1, "");
        initializeSimulatorListeners();

        this.setContentPane(panel1);
        this.setVisible(true);
    }

    /**This method initializes a panel that is going to be added to a frame with the necessary
     * text fields and buttons*/
    private void initializeSimulator(JPanel panel, String resultText){

        JLabel titleLabel = new JLabel("Queue simulator");
        titleLabel.setBounds(190, 10, 300, 70);
        Font labelFont = titleLabel.getFont();
        titleLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 20));

        JLabel clientsLabel = new JLabel("Clients:");
        clientsLabel.setBounds(20, 80, 60, 30);

        clientsField = new JTextField();
        clientsField.setBounds(90, 80, 50, 30);

        JLabel queuesLabel = new JLabel("Queues: ");
        queuesLabel.setBounds(20, 120, 60, 30);

        queuesField = new JTextField();
        queuesField.setBounds(90, 120, 50, 30);

        JLabel timeLabel = new JLabel("Sim time: ");
        timeLabel.setBounds(20, 160, 60, 30);

        timeField = new JTextField();
        timeField.setBounds(90, 160, 50, 30);

        JLabel arrivalMinLabel = new JLabel("Min arrival:");
        arrivalMinLabel.setBounds(170, 80, 80, 30);

        arrivalMinField = new JTextField();
        arrivalMinField.setBounds(250, 80, 50, 30);

        JLabel arrivalMaxLabel = new JLabel("Max arrival: ");
        arrivalMaxLabel.setBounds(170, 120, 80, 30);

        arrivalMaxField = new JTextField();
        arrivalMaxField.setBounds(250, 120, 50, 30);

        JLabel serviceMinLabel = new JLabel("Min service:");
        serviceMinLabel.setBounds(330, 80, 80, 30);

        serviceMinField = new JTextField();
        serviceMinField.setBounds(420, 80, 50, 30);

        JLabel serviceMaxLabel = new JLabel("Max service: ");
        serviceMaxLabel.setBounds(330, 120, 80, 30);

        serviceMaxField = new JTextField();
        serviceMaxField.setBounds(420, 120, 50, 30);

        startButton = new JButton("START");
        startButton.setBounds(480, 90, 90, 40);

        updateTextArea = new JTextArea(resultText);
        updateTextArea.setBounds(20, 200, 550, 250);

        panel.add(titleLabel);
        panel.add(clientsLabel);
        panel.add(queuesLabel);
        panel.add(clientsField);
        panel.add(queuesField);
        panel.add(timeLabel);
        panel.add(timeField);
        panel.add(arrivalMinLabel);
        panel.add(arrivalMinField);
        panel.add(arrivalMaxLabel);
        panel.add(arrivalMaxField);
        panel.add(serviceMinLabel);
        panel.add(serviceMinField);
        panel.add(serviceMaxLabel);
        panel.add(serviceMaxField);
        panel.add(startButton);
        panel.add(updateTextArea);
    }

    /**Method that initializes the start button*/
    private void initializeSimulatorListeners(){

        startButton.addActionListener(e -> {

            if (checkFormat(queuesField.getText()) && checkFormat(clientsField.getText()) && checkFormat(timeField.getText()) && checkFormat(serviceMinField.getText()) && checkFormat(serviceMaxField.getText()) && checkFormat(arrivalMinField.getText()) && checkFormat(arrivalMaxField.getText())) {

                /**First we check if the fields match the necessary format and conditions*/
                if(Integer.parseInt(serviceMinField.getText()) < Integer.parseInt(serviceMaxField.getText()) && Integer.parseInt(arrivalMinField.getText()) < Integer.parseInt(arrivalMaxField.getText()) && Integer.parseInt(arrivalMaxField.getText()) < Integer.parseInt(timeField.getText())) {
                    simulationManager = new SimulationManager(Integer.parseInt(queuesField.getText()), Integer.parseInt(clientsField.getText()), Integer.parseInt(timeField.getText()), Integer.parseInt(serviceMinField.getText()), Integer.parseInt(serviceMaxField.getText()), Integer.parseInt(arrivalMinField.getText()), Integer.parseInt(arrivalMaxField.getText()), this);
                    Thread t = new Thread(simulationManager);
                    t.start();
                }
                else {
                    JOptionPane.showMessageDialog(this, "Invalid arrival or service time", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else{
                JOptionPane.showMessageDialog(this, "Please insert only numbers", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**Method that writes to a log file the results and also displays them in the interface*/
    public void updateResults(String resultText, String fullResultText){

        try {
            fileWriter = new FileWriter(path);
            fileWriter.write(fullResultText);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error while writing in file");
            e.printStackTrace();
        } catch (NullPointerException e1) {
            System.out.println("");
        }

        this.setTitle("Queue simulator");
        this.setSize(600, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);

        JPanel panel2 = new JPanel();
        panel2.setLayout(null);

        updateTextArea.setText(resultText);
        panel2.add(updateTextArea);
        initializeSimulator(panel2, resultText);
        initializeSimulatorListeners();

        this.setContentPane(panel2);
        this.setVisible(true);
    }

    /**Method that uses general expressions in order to check if the fields are valid*/
    private boolean checkFormat(String string){
        Pattern pattern = Pattern.compile("[0-9]+"); /**check if string matches pattern*/
        Matcher matcher = pattern.matcher(string);
        boolean check = matcher.matches();
        return check;
    }
}