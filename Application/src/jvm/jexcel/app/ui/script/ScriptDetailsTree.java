package jvm.jexcel.app.ui.script;

import jvm.jexcel.app.actions.Actions;
import jvm.jexcel.app.core.script.LocalScriptDetails;
import jvm.jexcel.app.core.script.RemoteScriptDetails;
import jvm.jexcel.app.core.script.ScriptDetails;
import res.Res;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

public class ScriptDetailsTree<T extends ScriptDetails> extends JPanel {

    private static class Node<T extends ScriptDetails>{

        private final T sd;
        private final Column col;
        private String desc;

        private Node(final T sd, final Column col){
            this.sd = sd;
            this.col = col;

            switch(col){
                case AUTHOR:
                    desc = sd.getAuthor();
                    break;
                case NAME:
                    desc = sd.getName();
                    break;
                default:
                    desc = "V" + sd.getVersion();
                    break;
            }
        }

        public String toString(){
            return desc;
        }
    }

    public static abstract class RightClickAction<T extends ScriptDetails> {

        private final String name;
        private final ImageIcon icon;

        public RightClickAction(final String name, final ImageIcon icon){
            this.name = name;
            this.icon = icon;
        }

        public abstract void perform(final T details);
    }

    public static final RightClickAction[] LOCAL_ACTIONS = {
            new RightClickAction<LocalScriptDetails>("Run", Res.RUN_16){
                public void perform(final LocalScriptDetails sd){
                    Actions.runScript(sd);
                }
            },
            /*new RightClickAction<LocalScriptDetails>("Delete", Res.DELETE_16){
                public void perform(final LocalScriptDetails sd){
                    Actions.deleteScript(sd);
                }
            }*/
    };

    public static final RightClickAction[] REMOTE_ACTIONS = {
            new RightClickAction<RemoteScriptDetails>("Download", Res.DOWNLOAD_16){
                public void perform(final RemoteScriptDetails sd){
                    Actions.downloadScript(sd);
                }
            }
    };

    public enum Column{
        NAME("Name", Res.SCRIPT_16),
        VERSION("Version", Res.INFO_16),
        AUTHOR("Author", Res.USER_16),
        DESC("Description", null);

        private final String name;
        private final ImageIcon icon;

        private Column(final String name, final ImageIcon icon){
            this.name = name;
            this.icon = icon;
        }

        public String getName(){
            return name;
        }

        public ImageIcon getIcon(){
            return icon;
        }
    }

    private final DefaultMutableTreeNode root;
    private final DefaultTreeModel model;
    private final JTree tree;

    public ScriptDetailsTree(final RightClickAction[] actions){
        super(new BorderLayout());

        root = new DefaultMutableTreeNode("Scripts");

        model = new DefaultTreeModel(root);

        tree = new JTree(model);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(
                new DefaultTreeCellRenderer(){
                    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean focused){
                        final JLabel comp = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, focused);
                        if(row == 0)
                            comp.setIcon(Res.SCRIPT_16);
                        if(value == null || row == 0 || !(value instanceof DefaultMutableTreeNode))
                            return comp;
                        final DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
                        if(!(dmtn.getUserObject() instanceof Node))
                            return comp;
                        final Node dh = (Node) dmtn.getUserObject();
                        comp.setIcon(dh.col.getIcon());
                        return comp;
                    }
                }
        );
        tree.addMouseListener(
                new MouseAdapter(){
                    public void mousePressed(final MouseEvent e){
                        final T sd = getMaxSelected();
                        if(sd == null)
                            return;
                        SwingUtilities.invokeLater(
                                new Runnable(){
                                    public void run(){
                                        final JPopupMenu popup = new JPopupMenu();
                                        for(final RightClickAction action : actions){
                                            final JMenuItem item = new JMenuItem(action.name, action.icon);
                                            item.addActionListener(
                                                    new ActionListener(){
                                                        public void actionPerformed(final ActionEvent e){
                                                            action.perform(sd);
                                                        }
                                                    }
                                            );
                                            popup.add(item);
                                        }
                                        popup.show(tree, e.getX(), e.getY());
                                    }
                                }
                        );
                    }
                }
        );

        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    public T getMaxSelected(){
        final TreePath path = tree.getSelectionPath();
        if(path == null || path.getPathCount() < 2)
            return null;
        final Node<T> node = (Node<T>)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
        return node.sd;
    }

    public void add(final T sd){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        boolean addAuthor = true;
                        DefaultMutableTreeNode authorNode = new DefaultMutableTreeNode(new Node<>(sd, Column.AUTHOR));
                        final Enumeration<DefaultMutableTreeNode> authors = root.children();
                        while(authors.hasMoreElements()){
                            final DefaultMutableTreeNode an = authors.nextElement();
                            final Node n = (Node) an.getUserObject();
                            if(sd.getAuthor().equals(n.sd.getAuthor())){
                                authorNode = an;
                                addAuthor = false;
                                break;
                            }
                        }
                        boolean addScript = true;
                        DefaultMutableTreeNode scriptNode = new DefaultMutableTreeNode(new Node<>(sd, Column.NAME));
                        final Enumeration<DefaultMutableTreeNode> scripts = authorNode.children();
                        while(scripts.hasMoreElements()){
                            final DefaultMutableTreeNode sn = scripts.nextElement();
                            final Node n = (Node) sn.getUserObject();
                            if(sd.getName().equals(n.sd.getName())){
                                scriptNode = sn;
                                addScript = false;
                                break;
                            }
                        }
                        final DefaultMutableTreeNode versionNode = new DefaultMutableTreeNode(new Node<>(sd, Column.VERSION));
                        if(addAuthor)
                            model.insertNodeInto(authorNode, root, root.getChildCount());
                        if(addScript)
                            model.insertNodeInto(scriptNode, authorNode, authorNode.getChildCount());
                        model.insertNodeInto(versionNode, scriptNode, scriptNode.getChildCount());
                        tree.expandRow(0);
                        tree.revalidate();
                        tree.repaint();
                    }
                }
        );
    }

    public void remove(final T sd){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        DefaultMutableTreeNode authorNode = null;
                        final Enumeration<DefaultMutableTreeNode> authors = root.children();
                        while(authors.hasMoreElements()){
                            final DefaultMutableTreeNode an = authors.nextElement();
                            final Node n = (Node) an.getUserObject();
                            if(sd.getAuthor().equals(n.sd.getAuthor())){
                                authorNode = an;
                                break;
                            }
                        }
                        if(authorNode == null)
                            return;
                        DefaultMutableTreeNode scriptNode = null;
                        final Enumeration<DefaultMutableTreeNode> scripts = authorNode.children();
                        while(scripts.hasMoreElements()){
                            final DefaultMutableTreeNode sn = scripts.nextElement();
                            final Node n = (Node) sn.getUserObject();
                            if(sd.getName().equals(n.sd.getName())){
                                scriptNode = sn;
                                break;
                            }
                        }
                        if(scriptNode == null)
                            return;
                        DefaultMutableTreeNode versionNode = null;
                        final Enumeration<DefaultMutableTreeNode> versions = scriptNode.children();
                        while(versions.hasMoreElements()){
                            final DefaultMutableTreeNode vn = versions.nextElement();
                            final Node n = (Node) vn.getUserObject();
                            if(sd.getVersion() == n.sd.getVersion()){
                                versionNode = vn;
                                break;
                            }
                        }
                        if(versionNode == null)
                            return;
                        model.removeNodeFromParent(versionNode);
                        if(scriptNode.getChildCount() == 0)
                            model.removeNodeFromParent(scriptNode);
                        if(authorNode.getChildCount() == 0)
                            model.removeNodeFromParent(authorNode);
                        tree.revalidate();
                        tree.repaint();
                    }
                }
        );
    }

    public void clear(){
        root.removeAllChildren();
        model.reload();
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        root.removeAllChildren();
                        model.reload();
                        tree.revalidate();
                        tree.repaint();
                    }
                }
        );
    }
}
