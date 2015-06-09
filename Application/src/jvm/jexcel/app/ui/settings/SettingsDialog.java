package jvm.jexcel.app.ui.settings;

import jvm.jexcel.app.ui.misc.LabelledComponent;
import jvm.jexcel.app.util.Settings;
import jvm.jexcel.app.util.Utils;
import res.Res;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SettingsDialog extends JDialog implements ActionListener{

    private static final int FIELDS_SPACING = 10;
    private static final int LABEL_WIDTH = 125;

    private static SettingsDialog instance;

    private final JTextField ipBox;
    private final JSpinner portBox;
    private final JToggleButton saveChangesYesButton;
    private final JToggleButton saveChangesNoButton;
    private final JTextField defaultWorkbookBox;
    private final JButton defaultWorkbookBrowseButton;

    private final JButton refreshButton;
    private final JButton saveButton;

    private JFileChooser fileChooser;

    private SettingsDialog(){
        super((Frame)null, "JExcel - Settings", true);

        ipBox = new JTextField(Settings.getIp());

        portBox = new JSpinner(new SpinnerNumberModel(Settings.getPort(), 1, Integer.MAX_VALUE, 1));
        final JFormattedTextField txt = ((JSpinner.NumberEditor) portBox.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        saveChangesYesButton = new JToggleButton("Yes", Res.YES_16, Settings.getSaveChanges());

        saveChangesNoButton = new JToggleButton("No", Res.NO_16, !Settings.getSaveChanges());

        final ButtonGroup group = new ButtonGroup();
        group.add(saveChangesYesButton);
        group.add(saveChangesNoButton);

        final JPanel saveChangesButtonsContainer = new JPanel();
        saveChangesButtonsContainer.setLayout(new BoxLayout(saveChangesButtonsContainer, BoxLayout.X_AXIS));
        saveChangesButtonsContainer.add(saveChangesYesButton);
        saveChangesButtonsContainer.add(Box.createHorizontalStrut(FIELDS_SPACING));
        saveChangesButtonsContainer.add(saveChangesNoButton);

        defaultWorkbookBox = new JTextField(Settings.getWorkbookDir());

        defaultWorkbookBrowseButton = new JButton(Res.FILE_BROWSE_16);
        defaultWorkbookBrowseButton.addActionListener(this);

        final JPanel fieldsContainer = new JPanel();
        fieldsContainer.setLayout(new BoxLayout(fieldsContainer, BoxLayout.Y_AXIS));
        fieldsContainer.add(new LabelledComponent("Browse IP", LABEL_WIDTH, ipBox, FIELDS_SPACING));
        fieldsContainer.add(Box.createVerticalStrut(FIELDS_SPACING));
        fieldsContainer.add(new LabelledComponent("Browse Port", LABEL_WIDTH, portBox, FIELDS_SPACING));
        fieldsContainer.add(Box.createVerticalStrut(FIELDS_SPACING));
        fieldsContainer.add(new LabelledComponent("Save Changes", LABEL_WIDTH, saveChangesButtonsContainer, FIELDS_SPACING));
        fieldsContainer.add(Box.createVerticalStrut(FIELDS_SPACING));
        fieldsContainer.add(new LabelledComponent("Workbook Path", LABEL_WIDTH, defaultWorkbookBox, defaultWorkbookBrowseButton, FIELDS_SPACING));

        refreshButton = new JButton("Refresh", Res.REFRESH_16);
        refreshButton.addActionListener(this);

        saveButton = new JButton("Save", Res.SAVE_16);
        saveButton.addActionListener(this);

        final JPanel buttonsContainer = new JPanel();
        buttonsContainer.setLayout(new BoxLayout(buttonsContainer, BoxLayout.X_AXIS));
        buttonsContainer.add(Box.createHorizontalGlue());
        buttonsContainer.add(refreshButton);
        buttonsContainer.add(Box.createHorizontalStrut(FIELDS_SPACING));
        buttonsContainer.add(saveButton);

        final JPanel container = new JPanel(new BorderLayout(0, FIELDS_SPACING));
        container.setBorder(new EmptyBorder(5, 5, 5, 5));
        container.add(fieldsContainer, BorderLayout.CENTER);
        container.add(buttonsContainer, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);

        setSize(500, getPreferredSize().height);
        setLocationRelativeTo(null);
    }

    private void refresh(){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        ipBox.setText(Settings.getIp());
                        ipBox.repaint();
                        portBox.setValue(Settings.getPort());
                        portBox.repaint();
                        saveChangesYesButton.setSelected(Settings.getSaveChanges());
                        saveChangesYesButton.repaint();
                        saveChangesNoButton.setSelected(!Settings.getSaveChanges());
                        saveChangesNoButton.repaint();
                        defaultWorkbookBox.setText(Settings.getWorkbookDir());
                        defaultWorkbookBox.repaint();
                    }
                }
        );
    }

    private void initFileChooser(){
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Workbook Path");
        fileChooser.setApproveButtonText("Set");
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(defaultWorkbookBrowseButton)){
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            if(fileChooser == null)
                                initFileChooser();
                            if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
                                return;
                            final File dir = fileChooser.getSelectedFile();
                            defaultWorkbookBox.setText(dir.getPath());
                        }
                    }
            );
        }else if(source.equals(refreshButton)){
            refresh();
        }else if(source.equals(saveButton)){
            final String ip = ipBox.getText();
            final int port = (Integer) portBox.getValue();
            final boolean saveChanges = saveChangesYesButton.isSelected();
            final String defaultWorkbook = defaultWorkbookBox.getText();
            if(!ip.equals(Settings.getIp()) && !Settings.set(Settings.IP_KEY, ip)){
                Utils.errMsg("Error changing ip to %s", ip);
                return;
            }
            if(port != Settings.getPort() && !Settings.set(Settings.PORT_KEY, port)){
                Utils.errMsg("Error changing port to %d", port);
                return;
            }
            if(saveChanges != Settings.getSaveChanges() && !Settings.set(Settings.SAVE_CHANGES_KEY, saveChanges)){
                Utils.errMsg("Error changing save changes to %s", saveChanges);
                return;
            }
            if(!defaultWorkbook.equals(Settings.getWorkbookDir()) && !Settings.set(Settings.WORKBOOK_DIR_KEY, defaultWorkbook)){
                Utils.errMsg("Error changing workbook path to %s", defaultWorkbook);
                return;
            }
        }
    }

    public static void open(){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        if(instance == null)
                            instance = new SettingsDialog();
                        instance.setVisible(true);
                        instance.refresh();
                    }
                }
        );
    }
}
