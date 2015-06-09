package jvm.jexcel.app.ui.browse;

import jvm.jexcel.app.core.script.RemoteScriptDetails;
import jvm.jexcel.app.ui.script.ScriptDetailsTree;
import res.Res;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class BrowseWindow extends JFrame {

    private static BrowseWindow instance;

    private static ScriptDetailsTree<RemoteScriptDetails> remoteScriptsTree;

    public BrowseWindow(){
        super("JExcel - Browse Remote Scripts");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setIconImages(Res.icons());

        remoteScriptsTree = new ScriptDetailsTree<>(ScriptDetailsTree.REMOTE_ACTIONS);

        final JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(5, 5, 5, 5));
        container.add(new ToolBar(), BorderLayout.NORTH);
        container.add(remoteScriptsTree);

        add(container, BorderLayout.CENTER);

        setSize(700, getPreferredSize().height);
        setLocationRelativeTo(null);
    }

    public static ScriptDetailsTree<RemoteScriptDetails> getRemoteScriptsTree(){
        return remoteScriptsTree;
    }

    public static void open(){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        if(instance == null)
                            instance = new BrowseWindow();
                        instance.setVisible(true);
                    }
                }
        );
    }
}
