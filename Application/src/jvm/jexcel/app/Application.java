package jvm.jexcel.app;

import jvm.jexcel.app.core.script.LocalScriptDetails;
import jvm.jexcel.app.ui.console.Console;
import jvm.jexcel.app.ui.run.ScriptRunTable;
import jvm.jexcel.app.ui.script.ScriptDetailsTree;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;
import res.Res;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;

public class Application extends JFrame {

    private static ScriptDetailsTree<LocalScriptDetails> localScriptsTree;
    private static ScriptRunTable scriptRunTable;
    private static Console console;

    private Application(){
        super("JExcel");
        setIconImages(Res.icons());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        localScriptsTree = new ScriptDetailsTree<>(ScriptDetailsTree.LOCAL_ACTIONS);
        localScriptsTree.setBorder(new TitledBorder("Scripts"));

        scriptRunTable = new ScriptRunTable();
        scriptRunTable.setBorder(new TitledBorder("Running Scripts"));

        final JSplitPane topSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, localScriptsTree, scriptRunTable);
        topSplit.setResizeWeight(0.5);

        console = new Console();

        final JSplitPane midSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, console);
        midSplit.setResizeWeight(0.7);

        final JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(10, 10, 10, 10));
        container.add(new ToolBar(), BorderLayout.NORTH);
        container.add(midSplit, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);

        setSize(1000, 700);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    public static ScriptDetailsTree<LocalScriptDetails> getLocalScriptsTree(){
        return localScriptsTree;
    }

    public static ScriptRunTable getScriptRunTable(){
        return scriptRunTable;
    }

    public static Console getConsole(){
        return console;
    }

    public static void main(String[] args) throws Exception{
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run(){
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                        SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
                        new Application();
                    }
                }
        );
    }
}
