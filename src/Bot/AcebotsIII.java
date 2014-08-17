package Bot;

import javax.swing.*;
import java.io.*;
import static u.u.addHash;

public class AcebotsIII {
    public static void main(String[] args)
    {
        //JOptionPane.showMessageDialog(null, "I ran.");
        FileReader fr = null;
        String[] info = new String[4];
        try {
            fr = new FileReader("config.txt");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Welcome to Acebots III!\nWe need just a little info to start", "Acebots III First Setup", JOptionPane.INFORMATION_MESSAGE);
            String[] options = { "<-- Back", "Next -->", "Quit" };
            JPanel panel = new JPanel();
            JLabel label = new JLabel();
            panel.add(label);
            JTextField textField = new JTextField(10);
            panel.add(textField);
            String[] inputTexts = {"Username: ", "OAuth Password (not your twitch password): ", "Server (skip to use default): ", "Channels (separate multiple channels with): "};
            String[] inputs = new String[4];

            for (int i = 0; i < 4; i++)
            {
                textField.setText("");
                label.setText(inputTexts[i]);
                int result = JOptionPane.showOptionDialog(null, panel, "Acebots III First Setup v1",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, null);
                if (result == 0) {
                    if (i != 0)
                        i -= 2;
                    else
                        i -= 1;
                } else if (result == 1) {
                    inputs[i] = textField.getText();
                } else {
                    System.exit(0);
                }
            }

            if (inputs[2].equals(""))
                inputs[2] = "irc.twitch.tv";
            try {
                PrintWriter writer = new PrintWriter("config.txt");
                writer.println("Username=" + inputs[0]);
                writer.println("Password=" + inputs[1]);
                writer.println("Server=" + inputs[2]);
                writer.println("HomeChannel=" + addHash(inputs[3]).replace(",", ",#"));
                writer.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                fr = new FileReader("config.txt");
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }

        BufferedReader reader = new BufferedReader(fr);

        for (int i = 0; i < 4; i++)
        {
            try {
                info[i] = reader.readLine().split("=", 2)[1];
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        //JOptionPane.showMessageDialog(null, "I wat");
        new BotCore(info[0], info[1], info[2], info[3]);
    }
}
