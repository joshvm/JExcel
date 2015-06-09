package jvm.jexcel.app.ui.misc;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class LabelledComponent extends JPanel {

    private static final int SPACING = 10;

    public LabelledComponent(final String labelText, final int labelWidth, final JComponent component, final JComponent right, final int spacing){
        super(new BorderLayout(spacing, 0));

        final JLabel label = new JLabel(labelText, JLabel.RIGHT);
        label.setPreferredSize(new Dimension(labelWidth, label.getPreferredSize().height));
        label.setMinimumSize(label.getPreferredSize());
        label.setSize(label.getPreferredSize());

        add(label, BorderLayout.WEST);
        add(component, BorderLayout.CENTER);
        if(right != null)
            add(right, BorderLayout.EAST);
    }

    public LabelledComponent(final String labelText, final int labelWidth, final JComponent component, final int spacing){
        this(labelText, labelWidth, component, null, spacing);
    }

    public LabelledComponent(final String labelText, final int labelWidth, final JComponent component, final JComponent right){
        this(labelText, labelWidth, component, right, SPACING);
    }

    public LabelledComponent(final String labelText, final int labelWidth, final JComponent component){
        this(labelText, labelWidth, component, null);
    }
}
