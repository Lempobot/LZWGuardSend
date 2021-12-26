package GUI;
import LZW.*;
import LZW.Compressor.LZW_Compressor_Decompressor;
import LZW.Guard.GuardExecption;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LZW PASSWORD PROTECTED PROJECT
 * Submitted by:
 * Student 1: 	DANNY KOGEL 318503257
 * Student 2. 	ALEX BREGER 205580087
 */

public class LZWGuardGUI {

    public static boolean loadingDone = false;
    public static JTextField DnDField = new JTextField(" \uD83D\uDCC2 Drop your file here \uD83D\uDCC2");
    public static JLabel loading = new JLabel();
    public static String FilePath = "";
    public static int itemCounter = 0;
    public static int m_defaultBitSize = 22;
    public static int const_maxPasswordLength = 16;

    public static void main(String[] args) {

        //init the main frame for the gui
        JFrame mainJFrame= new JFrame("Guard LZW");


        //checks for the nimbus look for the gui
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        //DRAG AND DROP FIELD LOGIC + Counter label
        JLabel itemCounterLabel = new JLabel("No loaded Files");
        itemCounterLabel.setBounds(685,110, 200,40);
        DnDField.setBounds(100,50, 750,50);
        DnDField.setDropTarget(new DropTarget() {
            public void drop(DropTargetDropEvent draggedFile) {
                try {
                    draggedFile.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> userDroppedFiles = (List<File>) draggedFile.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : userDroppedFiles) {
                        FilePath = FilePath + " ➕ " + file.getPath();
                        Runner.inputSourceQueue.add(file.getPath());
                        Runner.FileQueue.add(file.getName());
                        itemCounterLabel.setText("Number of items Loaded: " + ++itemCounter);
                    }
                    DnDField.setText(FilePath);
                    draggedFile.dropComplete(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //source https://www.tutorialspoint.com/how-to-restrict-the-number-of-digits-inside-jpasswordfield-in-java
        //Checks that no more than 16 chars are entered to the password field,

        JPasswordField GuardPassword = new JPasswordField(const_maxPasswordLength);
        GuardPassword.setVisible(false);
        GuardPassword.setToolTipText("Make to sure to remember the password used, It's impossible to restore it");
        PlainDocument document = (PlainDocument) GuardPassword.getDocument();
        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String passwordCurrentString = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (passwordCurrentString.length() <= const_maxPasswordLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
                if(passwordCurrentString.length()==0){
                    passwordCurrentString = "1234567891011121";
                }
                Runner.setPassword(passwordCurrentString);
            }
        });

        //Buttons for compress and decompress, also loading label
        loading.setBounds(360,230,400,30);
        JButton compressButton=new JButton("\uD83D\uDCD4 Compress");
        compressButton.setBounds(150,230,120,30);
        compressButton.addActionListener(e -> {
            loading.setVisible(true);
            try {
                Runner.RunCompress();
            } catch (GuardExecption guardExecption) {
                guardExecption.printStackTrace();
            }
            itemCounter = 0;
            FilePath = "";
            DnDField.setText(" \uD83D\uDCC2 Drop your file here \uD83D\uDCC2");
        });

        JButton decompressButton=new JButton("\uD83D\uDD6E Decompress");
        decompressButton.setBounds(680,230,120,30);
        decompressButton.addActionListener(e -> {
            loadingDone = false;
            loading.setVisible(true);
            try {
                Runner.RunDecompress();
            } catch (GuardExecption guardExecption) {
                guardExecption.printStackTrace();
            }
            itemCounter = 0;
            FilePath = "";
            DnDField.setText(" \uD83D\uDCC2 Drop your file here \uD83D\uDCC2");
        });


        //Guard checkbox and label
        JLabel labelGuard = new JLabel("\uD83D\uDD12 Guard™");
        labelGuard.setToolTipText("This will allow you to choose a password\n and your compressed files will be encrypted using AES 128-bit Encryption.");
        JCheckBox checkbox = new JCheckBox();
        checkbox.addActionListener(event -> {
            JCheckBox cb = (JCheckBox) event.getSource();
            if (cb.isSelected()) {
                compressButton.setText("\uD83D\uDD12 G-Compress");
                GuardPassword.setVisible(true);
                Runner.setActivateGuard(true);
            } else {
                compressButton.setText("\uD83D\uDCD4 Compress");
                GuardPassword.setVisible(false);
                Runner.setActivateGuard(false);
            }
        });

        //GITHUB MENU
        JMenu main = new JMenu("GitHub");
        JMenuBar mainBar = new JMenuBar();
        JMenuItem github1 = new JMenuItem(new AbstractAction("Kogwork") {
            public void actionPerformed(ActionEvent e) {
                try {

                    Desktop.getDesktop().browse(new URI("https://github.com/Kogwork"));

                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JMenuItem github2 = new JMenuItem(new AbstractAction("Lempobot") {
            public void actionPerformed(ActionEvent e) {
                try {

                    Desktop.getDesktop().browse(new URI("https://github.com/Lempobot"));

                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });

        //"HELP" MENU
        JMenu help = new JMenu("Help");
        JMenuItem quickStart = new JMenuItem(new AbstractAction("Quick Start") {
            public void actionPerformed(ActionEvent e) {
                JFrame quick=new JFrame();
                JOptionPane.showMessageDialog(quick,
                        " Thank you for using Guard™! \nTo compress your files, just drag and drop them in the allocated field." +
                                " \nIf you wish to Encrypt them using a password, check the Guard™ checkbox " +
                                "\nand choose a password up to 16 digits long, You'll need it to decrypt the files as well " +
                                "\nso keep it in a safe place. Encrypted files are ended with .guard " +
                                "\nand compressed ones will have .lzw at the end.","Quick Start",JOptionPane.PLAIN_MESSAGE);
            }
        });
        JMenuItem forgotPassword = new JMenuItem(new AbstractAction("Forgot Password") {
            public void actionPerformed(ActionEvent e) {
                JFrame quick=new JFrame();
                JOptionPane.showMessageDialog(quick,"TOO BAD!\nIt's impossible to restore the password used, next time keep them in a safe place.","Forgot Password",JOptionPane.WARNING_MESSAGE);
            }
        });

        //BITSIZE SELECTION MENU
        JMenu bitSize = new JMenu("BitSize");
        JMenuItem bitSizeChange = new JMenuItem(new AbstractAction("Change Bit Size") {
            public void actionPerformed(ActionEvent e) {
                JFrame bitSizeFrame = new JFrame("Change BitSize");
                bitSizeFrame.setLocationRelativeTo(null);
                int infoPopUpWindow =JOptionPane.showConfirmDialog(bitSizeFrame, " Changing BitSize will make it impossible to decompress " +
                                "\nfiles compressed with different BitSize, but will allow compressing bigger files, " +
                        "\nalso may affect compressing times, are you sure?");
                if(infoPopUpWindow ==JOptionPane.YES_OPTION){
                    JLabel currentBitSize = new JLabel();
                    JSlider bitSlider;
                    JPanel bitPanel = new JPanel();
                    bitSlider = new JSlider(12, 32, LZW_Compressor_Decompressor.getM_SizeOfDictionaryElementInBits());
                    currentBitSize.setText("Bit Size: " + bitSlider.getValue() + "  ");
                    bitSlider.setPaintTrack(true);
                    bitSlider.setPaintTicks(true);
                    bitSlider.setPaintLabels(true);
                    bitSlider.setMajorTickSpacing(2);
                    bitSlider.setMinorTickSpacing(1);

                    ChangeListener slider = e13 -> currentBitSize.setText("Bit Size: " + bitSlider.getValue() + "  ");
                    JButton bitButton =new JButton("Confirm");
                    bitButton.setBounds(50,100,95,30);
                    bitButton.addActionListener(e12 -> {
                        LZW_Compressor_Decompressor.setM_SizeOfDictionaryElementInBits(bitSlider.getValue());
                        bitSizeFrame.dispatchEvent(new WindowEvent(bitSizeFrame, WindowEvent.WINDOW_CLOSING));
                    });

                    JButton bitButtonDefault =new JButton("Default");
                    bitButtonDefault.setBounds(50,200,95,30);
                    bitButtonDefault.addActionListener(e12 -> {
                        LZW_Compressor_Decompressor.setM_SizeOfDictionaryElementInBits(m_defaultBitSize);
                        bitSlider.setValue(m_defaultBitSize);
                        currentBitSize.setText("Bit Size: " + m_defaultBitSize + "  ");
                    });

                    bitSlider.addChangeListener(slider);
                    bitPanel.add(currentBitSize);
                    bitPanel.add(bitButton);
                    bitPanel.add(bitButtonDefault);
                    bitPanel.add(bitSlider);
                    bitSizeFrame.add(bitPanel);
                    bitSizeFrame.setSize(300, 150);
                    bitSizeFrame.show();

                }else if(infoPopUpWindow ==JOptionPane.NO_OPTION){
                    bitSizeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }

        });
        //FOLDER MENU
        JMenu folders = new JMenu("Folders");
        JMenuItem compressedFolder = new JMenuItem(new AbstractAction("Compressed Files") {
            public void actionPerformed(ActionEvent e) {
                File theDir = new File("..\\CompressedFiles");
                if (!theDir.exists()){
                    theDir.mkdirs();
                }
                try {
                    Runtime.getRuntime().exec("cmd /c start "+ "..\\CompressedFiles");
                } catch (IOException ex) {
                    Logger.getLogger(LZWGuardGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JMenuItem DecompressedFolder = new JMenuItem(new AbstractAction("Decompressed Files") {
            public void actionPerformed(ActionEvent e) {
                File theDir = new File("..\\DecompressedFiles");
                if (!theDir.exists()){
                    theDir.mkdirs();
                }
                try {
                    Runtime.getRuntime().exec("cmd /c start "+ "..\\DecompressedFiles");
                } catch (IOException ex) {
                    Logger.getLogger(LZWGuardGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JMenuItem exampleInputs = new JMenuItem(new AbstractAction("Example Inputs") {
            public void actionPerformed(ActionEvent e) {
                File theDir = new File("..\\ExampleInputs");
                if (!theDir.exists()){
                    theDir.mkdirs();
                }
                try {
                    Runtime.getRuntime().exec("cmd /c start "+ "..\\ExampleInputs");
                } catch (IOException ex) {
                    Logger.getLogger(LZWGuardGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //add all the components to the main frame
        bitSize.add(bitSizeChange);
        help.add(quickStart);
        help.add(forgotPassword);
        main.add(github1);
        main.add(github2);
        folders.add(compressedFolder);folders.add(DecompressedFolder); folders.add(exampleInputs);
        mainBar.add(folders); mainBar.add(help); mainBar.add(bitSize); mainBar.add(main);
        mainJFrame.setJMenuBar(mainBar);
        GuardPassword.setBounds(200,110, 200,40);
        labelGuard.setBounds(120,110, 100,40);
        checkbox.setBounds(100,120, 20,20);
        mainJFrame.add(DnDField); mainJFrame.add(GuardPassword); mainJFrame.add(mainBar); mainJFrame.add(checkbox); mainJFrame.add(labelGuard);
        mainJFrame.add(compressButton); mainJFrame.add(decompressButton); mainJFrame.add(loading); mainJFrame.add(itemCounterLabel);
        mainJFrame.setSize(1000,400);
        mainJFrame.setLayout(null);
        mainJFrame.setVisible(true);
        mainJFrame.setLocationRelativeTo(null);

        mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
