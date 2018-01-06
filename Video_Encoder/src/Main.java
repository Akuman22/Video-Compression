import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends JFrame implements ActionListener, ChangeListener {

    private String folderName = "test"; // folder where the images are
    private String fileName = "temp.txt"; // name of the file to be decoded
    private JTextField field = new JTextField(40);
    private JTextField field1 = new JTextField(40);
    private JTextField field2 = new JTextField(40);
    private String saveName = "temp.txt"; // name and place to save the converted file in
    private boolean imageRead = false; // flag - images have been read into image_objects
    private boolean fileSelect = false; // flag - file to play has been selected
    private boolean folderSelect = false; // flag - folder to read images from has been selected.
    private boolean fileRead = false; // flag - file to be decoded has been read
    public static int Quality = 10; // Decides the amount of compression
    private JSlider sBar1 = new JSlider(JSlider.HORIZONTAL, 0, 100, Quality); // Slider bar for selecting
    public static ArrayList<Image_Object> allFrames = new ArrayList<>(); // Array of Image_Objects for all files
    private JFrame f = new JFrame("EC504 Group - 1"); // Main Frame
    private JFrame f1 = new JFrame("EC504 Group - 1"); // Secondary Frame for pop-ups
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // For customized screen resolutions
    private double width = screenSize.getWidth(); // Resolution Width, my screen - 1920
    private double height = screenSize.getHeight(); // Resolution Height, my screen - 1080
    File workingDirectory = new File(System.getProperty("user.dir")); // Current Directory
    public static int GOP = 4;
    public static Decode ob1;

    /*
*   Main Constructor Method
*   Creates the Main JFrame for the app
*   @param - None
    */
    public Main(){

        // Setting all buttons
        JButton button1 = new JButton();
        JButton button2 = new JButton();
        JButton button3 = new JButton();
        JButton button4 = new JButton();
        JButton button5 = new JButton();
        JButton button6 = new JButton();
        JButton button7 = new JButton();
        JButton button8 = new JButton();
        JButton button9 = new JButton();

        // Labels for text display
        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();

        // Setting locations and sizes of all objects in the JFrame
        // Formula - (Local_Resolution*Pixels_Required/Design_System_Resolution
        sBar1.setBounds((int)(width*400/1920), (int)(height*600/1080),(int)(width*250/1920), (int)(height*50/1080));
        field.setBounds((int)(width*400/1920), (int)(height*400/1080),(int)(width*250/1920), (int)(height*25/1080) );
        field1.setBounds((int)(width*1200/1920), (int)(height*400/1080),(int)(width*250/1920), (int)(height*25/1080) );
        field2.setBounds((int)(width*400/1920), (int)(height*750/1080),(int)(width*250/1920), (int)(height*25/1080) );

        button1.setBounds((int)(width*400/1920), (int)(height*450/1080),(int)(width*250/1920), (int)(height*50/1080) );
        button2.setBounds((int)(width*400/1920), (int)(height*350/1080),(int)(width*300/1920), (int)(height*50/1080) );
        button3.setBounds((int)(width*1850/1920), 0,(int)(width*60/1920), (int)(height*50/1080) );
        button4.setBounds((int)(width*400/1920), (int)(height*800/1080),(int)(width*250/1920), (int)(height*50/1080) );
        button5.setBounds((int)(width*1200/1920), (int)(height*800/1080),(int)(width*250/1920), (int)(height*50/1080) );
        button6.setBounds((int)(width*1200/1920), (int)(height*350/1080),(int)(width*200/1920), (int)(height*50/1080) );
        button7.setBounds((int)(width*1800/1920), 0,(int)(width*100/1920), (int)(height*40/1080) );
        button8.setBounds((int)(width*1200/1920), (int)(height*450/1080),(int)(width*250/1920), (int)(height*50/1080) );
        button9.setBounds((int)(width*400/1920), (int)(height*700/1080),(int)(width*250/1920), (int)(height*50/1080) );

        label1.setBounds((int)(width*400/1920), (int)(height*200/1080),(int)(width*400/1920), (int)(height*50/1080));
        label2.setBounds((int)(width*400/1920), (int)(height*550/1080),(int)(width*400/1920), (int)(height*50/1080));
        label3.setBounds((int)(width*1200/1920), (int)(height*200/1080),(int)(width*400/1920), (int)(height*50/1080));

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close app when JFrame is closed

        // Custom Image for Background
        f.setLayout(new BorderLayout());
        f.setContentPane(new JLabel(new ImageIcon("back.jpg")));
        f.setLayout(new FlowLayout());

        // Set Font for all text on Objects
        sBar1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(15*(height)/(1080))));
        label1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(40*(height)/(1080))));
        label2.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(height)/(1080))));
        label3.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(40*(height)/(1080))));

        button1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(height)/(1080))));
        button2.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(height)/(1080))));
        button3.setFont(new Font("Arial", Font.BOLD, (int)(30*(height)/(1080))));
        button4.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(30*(height)/(1080))));
        button5.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(30*(height)/(1080))));
        button6.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(height)/(1080))));
        button7.setFont(new Font("Arial", Font.BOLD, (int)(50*(height)/(1080))));
        button8.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(height)/(1080))));
        button9.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(height)/(1080))));

        // Set Text for all objects
        label1.setText("Encode");
        label2.setText("Quality");
        label3.setText("Decode");

        button1.setText("Read Images");
        button2.setText("Browse Image Folder");
        button3.setText("X");
        button4.setText("Convert");
        button5.setText("Play");
        button6.setText("Browse File");
        button7.setText("-");
        button8.setText("Read File");
        button9.setText("Save as");

        field.setText(folderName);
        field1.setText(fileName);
        field2.setText(saveName);

        // Slider Bar Properties
        sBar1.setMinorTickSpacing(2); // Small Ticks spacing
        sBar1.setMajorTickSpacing(10); // Big Ticks Spacing
        sBar1.setPaintTicks(true); // Add Ticks
        sBar1.setPaintLabels(true); // Add number Labels
        sBar1.setOpaque(false); // Transparent Background
        sBar1.addChangeListener(this); // Read change in value

        // Full Screen Mode
        f.setLayout(null);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setUndecorated(true);

        // Color of text in all objects of the Frame
        label1.setForeground(Color.green);
        label2.setForeground(Color.green);
        label3.setForeground(Color.green);
        sBar1.setForeground(Color.GREEN);

        button1.setForeground(Color.green);
        button2.setForeground(Color.green);
        button3.setForeground(Color.red);
        button4.setForeground(Color.GREEN);
        button5.setForeground(Color.GREEN);
        button6.setForeground(Color.GREEN);
        button7.setForeground(Color.blue);
        button8.setForeground(Color.green);
        button9.setForeground(Color.green);

        // Button properties - Provide Border + Transparent Block
        button1.setBorderPainted(true);
        button1.setContentAreaFilled(false);
        button1.setFocusPainted(false);
        button1.setOpaque(false);
        button2.setBorderPainted(true);
        button2.setContentAreaFilled(false);
        button2.setFocusPainted(false);
        button2.setOpaque(false);
        button3.setBorderPainted(false);
        button3.setContentAreaFilled(false);
        button3.setFocusPainted(false);
        button3.setOpaque(false);
        button4.setBorderPainted(true);
        button4.setContentAreaFilled(false);
        button4.setFocusPainted(false);
        button4.setOpaque(false);
        button5.setBorderPainted(true);
        button5.setContentAreaFilled(false);
        button5.setFocusPainted(false);
        button5.setOpaque(false);
        button6.setBorderPainted(true);
        button6.setContentAreaFilled(false);
        button6.setFocusPainted(false);
        button6.setOpaque(false);
        button7.setBorderPainted(false);
        button7.setContentAreaFilled(false);
        button7.setFocusPainted(false);
        button7.setOpaque(false);
        button8.setBorderPainted(true);
        button8.setContentAreaFilled(false);
        button8.setFocusPainted(false);
        button8.setOpaque(false);
        button9.setBorderPainted(true);
        button9.setContentAreaFilled(false);
        button9.setFocusPainted(false);
        button9.setOpaque(false);

        // Listen to when button is pressed
        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
        button4.addActionListener(this);
        button5.addActionListener(this);
        button6.addActionListener(this);
        button7.addActionListener(this);
        button8.addActionListener(this);
        button9.addActionListener(this);

        // Change Color when button goes above Button
        button1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button1.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button1.setForeground(Color.green);
            }
        });

        button3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button3.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button3.setForeground(Color.red);
            }
        });

        button2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button2.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button2.setForeground(Color.green);
            }
        });

        button4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button4.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button4.setForeground(Color.green);
            }
        });

        button5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button5.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button5.setForeground(Color.green);
            }
        });

        button6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button6.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button6.setForeground(Color.green);
            }
        });

        button7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button7.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button7.setForeground(Color.blue);
            }
        });

        button8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button8.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button8.setForeground(Color.green);
            }
        });

        button9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button9.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button9.setForeground(Color.green);
            }
        });

        // Add everything to the JFrame
        f.add(field);
        f.add(field1);
        f.add(field2);
        f.add(sBar1);

        f.add(button1);
        f.add(button2);
        f.add(button3);
        f.add(button4);
        f.add(button5);
        f.add(button6);
        f.add(button7);
        f.add(button8);
        f.add(button9);

        f.add(label1);
        f.add(label2);
        f.add(label3);

        // Show JFrame
        f.setVisible(true);

    }

    public static void main(String args[]) {
        new Main();
    }

    private void readImages(){
        JProgressBar progressBar = new JProgressBar();
        File dir = new File(folderName); // folder with images
        float count = 0;
        int lossAvg = 0;
        File[] filesList = dir.listFiles();
        JButton buttonOK = new JButton();

        buttonOK.setText("OK");
        buttonOK.setForeground(Color.gray);
        buttonOK.setBounds(200, 70,100, 50 );
        progressBar.setBounds(0, 0,500, 50 );

        f1 = new JFrame();
        f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f1.setLocationRelativeTo(null);
        f1.setSize(500, 200);
        f1.setVisible(true);
        f1.setLayout(null);

        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        Border border = BorderFactory.createTitledBorder("Reading...");
        progressBar.setBorder(border);
        f1.add(progressBar, BorderLayout.NORTH);
        progressBar.setValue((0));
        f1.add(buttonOK);

        for (File file : filesList) {
            System.out.println(file);
            if (file.isFile()) {
                count++;
                progressBar.setValue((int) (100.0 * (count / (float) filesList.length)));
                try{
                    allFrames.add(new Image_Object("" + file));
                    new CrCb("compress");

                }
                catch (Exception e){
                    f1.setVisible(false);
                    imageRead = false;
                    System.out.println("Error Reading File");
                    f1 = new JFrame();
                    JLabel label1 = new JLabel();
                    JButton button = new JButton();

                    f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f1.setLocationRelativeTo(null);
                    f1.setSize(500, 200);
                    f1.setVisible(true);
                    f1.setLayout(null);

                    label1.setBounds((int)(width*50/1920), (int)(height*0/1080),(int)(width*400/1920), (int)(height*50/1080));
                    label1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(1920*1080)/(width*height))));
                    label1.setText("Error: Cant Read Files ");

                    button.setBounds((int)(width*50/1920), (int)(height*50/1080),(int)(width*400/1920), (int)(height*50/1080));
                    button.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(1920*1080)/(width*height))));
                    button.setText("OK");
                    button.addActionListener(this);

                    f1.add(label1);
                    f1.add(button);
                    f1.setVisible(true);
                    break;
                }
                lossAvg += allFrames.get((int) count - 1).loss;
            }
        }


        progressBar.setString("Done");
        buttonOK.addActionListener(this);
        border = BorderFactory.createTitledBorder("Average Loss = " + (float) lossAvg / filesList.length); //0.03 for 75
        progressBar.setBorder(border);
    }

    @Override
    public void stateChanged(ChangeEvent e){
        Quality = sBar1.getValue();
        System.out.println(Quality);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println(command);

        if (command.equals("X")) {
            System.exit(0);
        }

        if (command.equals("-")) {
            f.setState(Frame.ICONIFIED);
        }

        if (command.equals("OK")) {
            f1.setVisible(false);
        }

        if (command.equals("Read Images")) {
            if (folderSelect) {
                new Thread(new Runnable() { // main updating loop
                    public void run() {
                        readImages();
                    }
                }).start();
                imageRead = true;

            } else {

                f1 = new JFrame();
                JLabel label1 = new JLabel();
                JButton button = new JButton();

                f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f1.setLocationRelativeTo(null);
                f1.setSize(500, 200);
                f1.setVisible(true);
                f1.setLayout(null);

                label1.setBounds((int) (width * 50 / 1920), (int) (height * 0 / 1080), (int) (width * 400 / 1920), (int) (height * 50 / 1080));
                label1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int) (20 * (1920 * 1080) / (width * height))));
                label1.setText("Please Select Folder First");

                button.setBounds((int) (width * 50 / 1920), (int) (height * 50 / 1080), (int) (width * 400 / 1920), (int) (height * 50 / 1080));
                button.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int) (20 * (1920 * 1080) / (width * height))));
                button.setText("OK");
                button.addActionListener(this);

                f1.add(label1);
                f1.add(button);
                f1.setVisible(true);
            }
        }

        if (command.equals("Play")) {
            if (fileRead) {
                try{
                    new Player();
                }
                catch (IOException e2){
                    e2.printStackTrace();
                }

            } else {

                f1 = new JFrame();
                JLabel label1 = new JLabel();
                JButton button = new JButton();

                f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f1.setLocationRelativeTo(null);
                f1.setSize(500, 200);
                f1.setVisible(true);
                f1.setLayout(null);

                label1.setBounds((int) (width * 50 / 1920), (int) (height * 0 / 1080), (int) (width * 400 / 1920), (int) (height * 50 / 1080));
                label1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int) (20 * (1920 * 1080) / (width * height))));
                label1.setText("Please Read File First");

                button.setBounds((int) (width * 50 / 1920), (int) (height * 50 / 1080), (int) (width * 400 / 1920), (int) (height * 50 / 1080));
                button.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int) (20 * (1920 * 1080) / (width * height))));
                button.setText("OK");
                button.addActionListener(this);

                f1.add(label1);
                f1.add(button);
                f1.setVisible(true);
            }

        }

        if (command.equals("Save as")) {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(Main.this);

            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Opening: " + file.getPath() + '\n');
                saveName = file.getPath() + ".txt";
                field2.setText(saveName);

            } else {

                System.out.println("Open command cancelled by user." + '\n');

            }
        }

        if (command.equals("Read File")) {
            if (fileSelect) {
                try{
                    fileRead = true;
                    ob1 = new Decode(new File(fileName));
                } catch (Exception e2) {
                    System.out.println("Error Decompressing");
                }

                f1 = new JFrame();
                JLabel label1 = new JLabel();
                JButton button = new JButton();

                f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f1.setLocationRelativeTo(null);
                f1.setSize(500, 200);
                f1.setVisible(true);
                f1.setLayout(null);

                label1.setBounds((int)(width*50/1920), (int)(height*0/1080),(int)(width*400/1920), (int)(height*50/1080));
                label1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(1920*1080)/(width*height))));
                label1.setText("File Read, let's play");

                button.setBounds((int)(width*50/1920), (int)(height*50/1080),(int)(width*400/1920), (int)(height*50/1080));
                button.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(1920*1080)/(width*height))));
                button.setText("OK");
                button.addActionListener(this);

                f1.add(label1);
                f1.add(button);
                f1.setVisible(true);

        } else {

            f1 = new JFrame();
            JLabel label1 = new JLabel();
            JButton button = new JButton();

            f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f1.setLocationRelativeTo(null);
            f1.setSize(500, 200);
            f1.setVisible(true);
            f1.setLayout(null);

            label1.setBounds((int) (width * 50 / 1920), (int) (height * 0 / 1080), (int) (width * 400 / 1920), (int) (height * 50 / 1080));
            label1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int) (20 * (1920 * 1080) / (width * height))));
            label1.setText("Please Select File First");

            button.setBounds((int) (width * 50 / 1920), (int) (height * 50 / 1080), (int) (width * 400 / 1920), (int) (height * 50 / 1080));
            button.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int) (20 * (1920 * 1080) / (width * height))));
            button.setText("OK");
            button.addActionListener(this);

            f1.add(label1);
            f1.add(button);
            f1.setVisible(true);
            }
        }

        if (command.equals("Browse File")) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(Main.this);

            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Opening: " + file.getPath() + '\n');
                fileName = file.getPath();
                field1.setText(fileName);
                fileSelect = true;

            } else {

                System.out.println("Open command cancelled by user." + '\n');

            }
        }

        if (command.equals("Browse Image Folder")) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(Main.this);

            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getCurrentDirectory();
                //This is where a real application would open the file.
                System.out.println("Opening: " + file.getPath() + "." + '\n');
                folderName = file.getPath();
                field.setText(folderName);
                folderSelect = true;

            } else {

                System.out.println("Open command cancelled by user." + '\n');

            }
        }

        if (command.equals("Convert")) {
            if(imageRead) {
                f1 = new JFrame();
                JLabel label1 = new JLabel();

                f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f1.setLocationRelativeTo(null);
                f1.setSize(500, 200);
                f1.setVisible(true);
                f1.setLayout(null);

                label1.setBounds((int)(width*50/1920), (int)(height*0/1080),(int)(width*400/1920), (int)(height*50/1080));
                label1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(1920*1080)/(width*height))));
                label1.setText("Converting...");

                f1.add(label1);
                f1.setVisible(true);
                dct testDCT = new dct(Quality);
                testDCT.idctWrapper();
                new InterFrame();
                int blockNum = allFrames.get(0).macroBlocks.size();
                int Height = allFrames.get(0).imgHeight;
                int Width = allFrames.get(0).imgWidth;
                int fps = 10;
                int frameNum = allFrames.size();
                new bitStream( blockNum, GOP, Height, Width, fps, frameNum, saveName);
                allFrames.clear();
                f1.setVisible(false);
                f1 = new JFrame();
                label1 = new JLabel();
                JButton button = new JButton();

                f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f1.setLocationRelativeTo(null);
                f1.setSize(500, 200);
                f1.setVisible(true);
                f1.setLayout(null);

                label1.setBounds((int)(width*50/1920), (int)(height*0/1080),(int)(width*400/1920), (int)(height*50/1080));
                label1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(1920*1080)/(width*height))));
                label1.setText("Images Read and Saved to file");

                button.setBounds((int)(width*50/1920), (int)(height*50/1080),(int)(width*400/1920), (int)(height*50/1080));
                button.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(1920*1080)/(width*height))));
                button.setText("OK");
                button.addActionListener(Main.this);

                f1.add(label1);
                f1.add(button);
                f1.setVisible(true);

            } else {

                f1 = new JFrame();
                JLabel label1 = new JLabel();
                JButton button = new JButton();

                f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f1.setLocationRelativeTo(null);
                f1.setSize(500, 200);
                f1.setVisible(true);
                f1.setLayout(null);

                label1.setBounds((int)(width*50/1920), (int)(height*0/1080),(int)(width*400/1920), (int)(height*50/1080));
                label1.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(1920*1080)/(width*height))));
                label1.setText("Please Read the images first");

                button.setBounds((int)(width*50/1920), (int)(height*50/1080),(int)(width*400/1920), (int)(height*50/1080));
                button.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20*(1920*1080)/(width*height))));
                button.setText("OK");
                button.addActionListener(this);

                f1.add(label1);
                f1.add(button);
                f1.setVisible(true);
            }
        }
    }
}