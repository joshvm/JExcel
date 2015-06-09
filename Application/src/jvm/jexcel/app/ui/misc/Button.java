package jvm.jexcel.app.ui.misc;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;

public class Button extends JButton {

    public Button(final String text, final ImageIcon icon, final ActionListener listener){
        super(text, icon);
        setFocusPainted(false);
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        addActionListener(listener);
    }
}
