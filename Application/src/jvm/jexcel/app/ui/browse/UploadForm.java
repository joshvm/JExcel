package jvm.jexcel.app.ui.browse;

import jvm.jexcel.app.core.script.LocalScriptDetails;
import jvm.jexcel.app.core.script.ScriptDetailsManager;
import jvm.jexcel.app.ui.misc.LabelledComponent;
import res.Res;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UploadForm extends JFrame implements ActionListener{

    private static final int SPACING = 10;
    private static final int LABEL_WIDTH = 100;

    private JFileChooser fileChooser;
    private LocalScriptDetails script;

    private final JTextField jarFileBox;
    private final JButton browseButton;

    private final JTextField nameBox;
    private final JTextField versionBox;
    private final JTextField authorBox;
    private final JTextArea descArea;

    private final JLabel infoLabel;
    private final JButton uploadButton;
    private final JButton cancelButton;

    public UploadForm(){
        super("JExcel - Upload Script");
        setIconImages(Res.icons());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        jarFileBox = new JTextField();
        jarFileBox.setEditable(false);

        browseButton = new JButton(Res.FILE_BROWSE_16);
        browseButton.addActionListener(this);

        nameBox = new JTextField();
        nameBox.setEditable(false);

        versionBox = new JTextField();
        versionBox.setEditable(false);

        authorBox = new JTextField();
        authorBox.setEditable(false);

        descArea = new JTextArea();
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setRows(3);

        final JPanel infoContainer = new JPanel();
        infoContainer.setBorder(new TitledBorder("Script Info"));
        infoContainer.setLayout(new BoxLayout(infoContainer, BoxLayout.Y_AXIS));
        infoContainer.add(new LabelledComponent("Name", LABEL_WIDTH, nameBox, SPACING));
        infoContainer.add(Box.createVerticalStrut(SPACING));
        infoContainer.add(new LabelledComponent("Version", LABEL_WIDTH, versionBox, SPACING));
        infoContainer.add(Box.createVerticalStrut(SPACING));
        infoContainer.add(new LabelledComponent("Author", LABEL_WIDTH, authorBox, SPACING));
        infoContainer.add(Box.createVerticalStrut(SPACING));
        infoContainer.add(new LabelledComponent("Description", LABEL_WIDTH, new JScrollPane(descArea), SPACING));

        infoLabel = new JLabel("", JLabel.CENTER);

        uploadButton = new JButton("Upload", Res.UPLOAD_16);
        uploadButton.addActionListener(this);

        cancelButton = new JButton("Cancel", Res.STOP_16);
        cancelButton.addActionListener(this);

        final JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.add(uploadButton);
        buttonContainer.add(Box.createHorizontalStrut(SPACING));
        buttonContainer.add(cancelButton);

        final JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(infoLabel, BorderLayout.CENTER);
        bottomContainer.add(buttonContainer, BorderLayout.EAST);

        final JPanel container = new JPanel(new BorderLayout(0, SPACING));
        container.setBorder(new EmptyBorder(5, 5, 5, 5));
        container.add(new LabelledComponent("Script JAR", LABEL_WIDTH, jarFileBox, browseButton, SPACING), BorderLayout.NORTH);
        container.add(infoContainer, BorderLayout.CENTER);
        container.add(bottomContainer, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);

        setSize(500, getPreferredSize().height);
        setLocationRelativeTo(null);
    }

    private void info(final String fmt, final Object... args){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        infoLabel.setText(String.format(fmt, args));
                        infoLabel.repaint();
                    }
                }
        );
    }

    private void populate(final LocalScriptDetails sd){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        nameBox.setText(sd == null ? "" : sd.getName());
                        nameBox.repaint();
                        versionBox.setText(sd == null ? "" : Double.toString(sd.getVersion()));
                        versionBox.repaint();
                        authorBox.setText(sd == null ? "" : sd.getAuthor());
                        authorBox.repaint();
                        descArea.setText(sd == null ? "" : sd.getDesc());
                        descArea.repaint();
                    }
                }
        );
    }

    private void initFileChooser(){
        fileChooser = new JFileChooser(new File(".").getParentFile());
        fileChooser.setFileFilter(new FileNameExtensionFilter("JExcel Script", ".jar", "jar"));
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle("Select a Script (JAR)");
        fileChooser.setApproveButtonText("Select");
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(browseButton)){
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            if(fileChooser == null)
                                initFileChooser();
                            if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
                                return;
                            final File selected = fileChooser.getSelectedFile();
                            jarFileBox.setText(selected.getPath());
                            jarFileBox.repaint();
                            script = ScriptDetailsManager.parse(selected);
                            if(script == null){
                                info("Invalid jar file - not a script");
                                return;
                            }
                            populate(script);
                        }
                    }
            );
        }else if(source.equals(uploadButton)){
            info("");
            if(script == null){
                info("Select a script first");
                return;
            }
            info(Integer.toString(script.upload()));
        }else if(source.equals(cancelButton)){
            dispose();
        }
    }

    public static void open(){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        new UploadForm().setVisible(true);
                    }
                }
        );
    }
}
