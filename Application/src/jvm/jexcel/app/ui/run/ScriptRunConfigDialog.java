package jvm.jexcel.app.ui.run;

import jvm.jexcel.app.core.run.ScriptRunConfig;
import jvm.jexcel.app.core.script.LocalScriptDetails;
import jvm.jexcel.app.core.script.ScriptDetailsManager;
import jvm.jexcel.app.ui.misc.LabelledComponent;
import jvm.jexcel.app.util.Settings;
import res.Res;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Set;

public class ScriptRunConfigDialog extends JDialog implements ActionListener, ItemListener{

    private static final int LABEL_WIDTH = 125;
    private static final int FIELD_SPACING = 5;

    private final DefaultComboBoxModel<String> scriptAuthorsBoxModel;
    private final JComboBox<String> scriptAuthorsBox;
    private final DefaultComboBoxModel<String> scriptNamesBoxModel;
    private final JComboBox<String> scriptNamesBox;
    private final DefaultComboBoxModel<Double> scriptVersionsBoxModel;
    private final JComboBox<Double> scriptVersionsBox;

    private final JTextField inFileBox;
    private final JButton inBrowseButton;

    private final JToggleButton saveChangesYesButton;
    private final JToggleButton saveChangesNoButton;

    private final JTextField outFileBox;
    private final JButton outBrowseButton;

    private final JLabel infoLabel;
    private final JButton runButton;
    private final JButton cancelButton;

    private JFileChooser fileChooser;

    private ScriptRunConfig config;

    private ScriptRunConfigDialog(final String author, final String script, final double version){
        super((Frame) null, "JExcel - Run Config", true);
        setIconImages(Res.icons());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        scriptAuthorsBoxModel = new DefaultComboBoxModel<>();
        scriptAuthorsBox = new JComboBox<>(scriptAuthorsBoxModel);
        scriptAuthorsBox.setRenderer(
                new DefaultListCellRenderer(){
                    public Component getListCellRendererComponent(final JList list, final Object o, final int i, final boolean s, final boolean f){
                        final JLabel label = (JLabel) super.getListCellRendererComponent(list, o, i, s, f);
                        label.setIcon(Res.USER_16);
                        return label;
                    }
                }
        );

        scriptNamesBoxModel = new DefaultComboBoxModel<>();
        scriptNamesBox = new JComboBox<>(scriptNamesBoxModel);
        scriptNamesBox.setRenderer(
                new DefaultListCellRenderer(){
                    public Component getListCellRendererComponent(final JList list, final Object o, final int i, final boolean s, final boolean f){
                        final JLabel label = (JLabel) super.getListCellRendererComponent(list, o, i, s, f);
                        label.setIcon(Res.SCRIPT_16);
                        return label;
                    }
                }
        );

        scriptVersionsBoxModel = new DefaultComboBoxModel<>();
        scriptVersionsBox = new JComboBox<>(scriptVersionsBoxModel);
        scriptVersionsBox.setRenderer(
                new DefaultListCellRenderer(){
                    public Component getListCellRendererComponent(final JList list, final Object o, final int i, final boolean s, final boolean f){
                        final JLabel label = (JLabel) super.getListCellRendererComponent(list, o, i, s, f);
                        label.setIcon(Res.INFO_16);
                        return label;
                    }
                }
        );

        update(author, script, version);

        scriptAuthorsBox.addItemListener(this);
        scriptNamesBox.addItemListener(this);

        final JPanel scriptContainer = new JPanel();
        scriptContainer.setBorder(new TitledBorder("Script Details"));
        scriptContainer.setLayout(new BoxLayout(scriptContainer, BoxLayout.Y_AXIS));
        scriptContainer.add(new LabelledComponent("Author", LABEL_WIDTH, scriptAuthorsBox));
        scriptContainer.add(Box.createVerticalStrut(FIELD_SPACING));
        scriptContainer.add(new LabelledComponent("Name", LABEL_WIDTH, scriptNamesBox));
        scriptContainer.add(Box.createVerticalStrut(FIELD_SPACING));
        scriptContainer.add(new LabelledComponent("Version", LABEL_WIDTH, scriptVersionsBox));

        inFileBox = new JTextField();

        inBrowseButton = new JButton(Res.FILE_BROWSE_16);
        inBrowseButton.addActionListener(this);

        saveChangesYesButton = new JToggleButton("Yes", Res.YES_16, Settings.getSaveChanges());

        saveChangesNoButton = new JToggleButton("No", Res.NO_16, !Settings.getSaveChanges());

        final ButtonGroup group = new ButtonGroup();
        group.add(saveChangesYesButton);
        group.add(saveChangesNoButton);

        final JPanel saveChangesButtonContainer = new JPanel();
        saveChangesButtonContainer.setLayout(new BoxLayout(saveChangesButtonContainer, BoxLayout.X_AXIS));
        saveChangesButtonContainer.add(saveChangesYesButton);
        saveChangesButtonContainer.add(Box.createHorizontalStrut(FIELD_SPACING));
        saveChangesButtonContainer.add(saveChangesNoButton);

        outFileBox = new JTextField();

        outBrowseButton = new JButton(Res.FILE_BROWSE_16);
        outBrowseButton.addActionListener(this);

        final JPanel fieldsContainer = new JPanel();
        fieldsContainer.setBorder(new TitledBorder("Workbook Options"));
        fieldsContainer.setLayout(new BoxLayout(fieldsContainer, BoxLayout.Y_AXIS));
        fieldsContainer.add(new LabelledComponent("Workbook (In)", LABEL_WIDTH, inFileBox, inBrowseButton));
        fieldsContainer.add(Box.createVerticalStrut(FIELD_SPACING));
        fieldsContainer.add(new LabelledComponent("Save Changes", LABEL_WIDTH, saveChangesButtonContainer));
        fieldsContainer.add(Box.createVerticalStrut(FIELD_SPACING));
        fieldsContainer.add(new LabelledComponent("Workbook (Out)", LABEL_WIDTH, outFileBox, outBrowseButton));

        infoLabel = new JLabel("", JLabel.CENTER);

        runButton = new JButton("Run", Res.RUN_16);
        runButton.addActionListener(this);

        cancelButton = new JButton("Cancel", Res.STOP_16);
        cancelButton.addActionListener(this);

        final JPanel buttonsContainer = new JPanel();
        buttonsContainer.setLayout(new BoxLayout(buttonsContainer, BoxLayout.X_AXIS));
        buttonsContainer.add(runButton);
        buttonsContainer.add(Box.createHorizontalStrut(FIELD_SPACING));
        buttonsContainer.add(cancelButton);

        final JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(infoLabel, BorderLayout.CENTER);
        bottomContainer.add(buttonsContainer, BorderLayout.EAST);

        final JLabel titleLabel = new JLabel("Run Configuration", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(40f));

        final JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(titleLabel, BorderLayout.NORTH);
        northPanel.add(scriptContainer, BorderLayout.CENTER);

        final JPanel container = new JPanel(new BorderLayout(0, FIELD_SPACING));
        container.setBorder(new EmptyBorder(5, 5, 5, 5));
        container.add(northPanel, BorderLayout.NORTH);
        container.add(fieldsContainer, BorderLayout.CENTER);
        container.add(bottomContainer, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);

        setSize(700, getPreferredSize().height);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void update(final String selectedAuthor, final String selectedScript, final double selectedVersion){
        scriptAuthorsBoxModel.removeAllElements();
        scriptNamesBoxModel.removeAllElements();
        scriptVersionsBoxModel.removeAllElements();
        final Set<String> authors = ScriptDetailsManager.getAuthorNames();
        String useAuthor = selectedAuthor;
        for(final String author : authors){
            if(useAuthor == null)
                useAuthor = author;
            scriptAuthorsBoxModel.addElement(author);
        }
        if(useAuthor != null)
            scriptAuthorsBox.setSelectedItem(useAuthor);
        scriptAuthorsBox.repaint();
        if(authors.isEmpty())
            return;
        final Set<String> scripts = ScriptDetailsManager.getScriptNames(useAuthor);
        String useScript = selectedScript;
        for(final String script : scripts){
            if(useScript == null)
                useScript = script;
            scriptNamesBoxModel.addElement(script);
        }
        if(useScript != null)
            scriptNamesBox.setSelectedItem(useScript);
        scriptNamesBox.repaint();
        if(scripts.isEmpty())
            return;
        final Set<Double> versions = ScriptDetailsManager.getVersions(useAuthor, useScript);
        for(final double version : versions)
            scriptVersionsBoxModel.addElement(version);
        if(selectedVersion != -1)
            scriptVersionsBox.setSelectedItem(selectedVersion);
        scriptVersionsBox.repaint();
    }

    public void itemStateChanged(final ItemEvent e){
        if(e.getStateChange() != ItemEvent.SELECTED)
            return;
        final Object source = e.getSource();
        if(source.equals(scriptAuthorsBox)){
            final String author = (String) scriptAuthorsBox.getSelectedItem();
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            update(author, null, -1);
                        }
                    }
            );
        }else if(source.equals(scriptNamesBox)){
            final String author = (String) scriptAuthorsBox.getSelectedItem();
            final String script = (String) scriptNamesBox.getSelectedItem();
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            update(author, script, -1);
                        }
                    }
            );
        }
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(inBrowseButton) || source.equals(outBrowseButton)){
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            if(fileChooser == null)
                                initFileChooser();
                            if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
                                return;
                            final File selected = fileChooser.getSelectedFile();
                            if(source.equals(inBrowseButton))
                                inFileBox.setText(selected.getPath());
                            else
                                outFileBox.setText(selected.getPath());
                        }
                    }
            );
        }else if(source.equals(runButton)){
            info("");
            final String author = (String)scriptAuthorsBox.getSelectedItem();
            final String script = (String)scriptNamesBox.getSelectedItem();
            final double version = (Double) scriptVersionsBox.getSelectedItem();
            final LocalScriptDetails details = ScriptDetailsManager.getScript(author, script, version);
            if(details == null){
                info("This script is not valid");
                return;
            }
            final String inPath = inFileBox.getText();
            if(inPath.isEmpty()){
                info("You must select an input workbook");
                return;
            }
            if(!isWorkbook(inPath)){
                info("Select a valid .xls or .xlsx input Excel workbook");
                return;
            }
            final File in = new File(inPath);
            if(!in.exists()){
                info("Input Excel workbook doesn't exist");
                return;
            }
            final boolean saveChanges = saveChangesYesButton.isSelected();
            final String outPath = outFileBox.getText();
            File out = null;
            if(!outPath.isEmpty()){
                if(!isWorkbook(outPath)){
                    info("Select a valid .xls or .xlsx output Excel workbook");
                    return;
                }
                out = new File(outPath);
            }
            config = new ScriptRunConfig(details, in, saveChanges, out);
            dispose();
        }else if(source.equals(cancelButton)){
            dispose();
        }
    }

    private boolean isWorkbook(final String path){
        return path.endsWith(".xlsx") || path.endsWith(".xls");
    }

    private void initFileChooser(){
        fileChooser = new JFileChooser();
        final String workbookDirPath = Settings.getWorkbookDir();
        if(!workbookDirPath.isEmpty()){
            final File file = new File(workbookDirPath);
            if(file.exists() && file.isDirectory())
                fileChooser.setCurrentDirectory(file);
        }
        fileChooser.setDialogTitle("Select An Excel Workbook");
        fileChooser.setApproveButtonText("Select");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook", ".xlsx", "xlsx", ".xls", "xls"));
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

    public static ScriptRunConfig open(final String author, final String script, final double version){
        final ScriptRunConfigDialog dialog = new ScriptRunConfigDialog(author, script, version);
        while(dialog.isVisible());
        return dialog.config;
    }
}
