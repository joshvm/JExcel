package jvm.jexcel.app.util;

import jvm.jexcel.app.Application;
import jvm.jexcel.app.ui.console.Message;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Desktop;
import java.io.File;

public final class Utils {

    private Utils(){}

    public static void msg(final Message msg, final boolean show){
        Application.getConsole().add(msg);
        if(show)
            msg(msg.getText(), msg.getType().toString(), msg.getType().getDialogId());
    }

    public static void err(final Exception ex, final boolean show){
        msg(Message.error(ex), show);
    }

    public static void info(final String fmt, final Object... args){
        msg(Message.info(fmt, args), false);
    }

    public static void app(final String fmt, final Object... args){
        msg(Message.app(fmt, args), false);
    }

    public static void script(final String fmt, final Object... args){
        msg(Message.script(fmt, args), false);
    }

    public static void infoMsg(final String msg){
        msg(msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void errMsg(final String fmt, final Object... args){
        msg(String.format(fmt, args), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void msg(final String msg, final String title, final int type){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        JOptionPane.showMessageDialog(null, msg, title, type);
                    }
                }
        );
    }

    public static void open(final File file){
        try{
            Desktop.getDesktop().open(file);
        }catch(Exception ex){
            err(ex, false);
        }
    }
}
