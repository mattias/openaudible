package org.openaudible.desktop.swt.manager.menu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.*;
import org.openaudible.Audible;
import org.openaudible.audible.ConnectionNotifier;
import org.openaudible.desktop.swt.dialog.AboutDialog;
import org.openaudible.desktop.swt.gui.GUI;
import org.openaudible.desktop.swt.gui.MessageBoxFactory;
import org.openaudible.desktop.swt.manager.Application;
import org.openaudible.desktop.swt.manager.AudibleGUI;
import org.openaudible.desktop.swt.manager.Version;
import org.openaudible.desktop.swt.manager.VersionCheck;
import org.openaudible.desktop.swt.manager.views.Preferences;
import org.openaudible.desktop.swt.util.shop.WidgetShop;

/**
 * The CommandCenter is responsible to react on user-action. User action may for example occur when any item from the main menu is selected. The execute command is the main switch for running commands
 */

public class CommandCenter {
    public final static Log logger = LogFactory.getLog(CommandCenter.class);
    public static CommandCenter instance;
    boolean confirmQuit = true;
    boolean confirmSave = true;
    private Clipboard cb;
    private Application app;
    private Shell shell;

    public CommandCenter(Display display, Shell shell, Application a) {
        this.shell = shell;
        this.app = a;
        instance = this;
        cb = new Clipboard(display);
    }

    public void search() {

    }

    public void userError(String s) {
        MessageBoxFactory.showMessage(shell, SWT.ICON_INFORMATION, GUI.i18n.getTranslation("Unexpected event"), s);
    }

    public void actionConfigurationWizard() {

    }

    public void handleMenuAction(MenuItem item) {
        Object obj = item.getData();
        if (obj != null) {
            logger.info(obj);
        }
    }

    public void handleMenuAction(Command cmd, MenuItem item) // throws Exception
    {
        try {
            execute(cmd);
            app.updateMenus();
        } catch (Throwable t) {
            String task = "ProcessMenu: " + cmd;
            logger.error(task, t);
        }
    }


    /**
     * Copy text into clipboard
     *
     * @param text Any control capable of holding text
     */
    void actionCopyText(Control text) {
        /* Control is a StyledText widget */
        if (text instanceof StyledText) {
            if (((StyledText) text).getText() != null) {
                /* User has selected text */
                if (((StyledText) text).getSelectionCount() > 0)
                    cb.setContents(new Object[]{((StyledText) text).getSelectionText()}, new Transfer[]{TextTransfer.getInstance()});
                /* User has not selected text */
                else
                    cb.setContents(new Object[]{((StyledText) text).getText()}, new Transfer[]{TextTransfer.getInstance()});
            }
        }

    }

    public void setClipboard(String text) {
        cb.setContents(new Object[]{text}, new Transfer[]{TextTransfer.getInstance()});
    }

    /**
     * Exit application
     */
    void actionExit() {
        logger.info("actionExit");
        /* Force an Exit */
        if (reallyQuit())
            app.onClose(new Event(), true);
    }

    /*
     * Minimize application window
     */
    void actionMinimizeWindow() {
        shell.setMinimized(true);
    }

    public void showAbout() {
        AboutDialog.doAbout(GUI.shell);
    }

    /*
     * Open the URL in the external browser
     *
     * @param url The URL to open
     */
    public void actionOpenURL(String url) {
    }

    /**
     * This method is called when a MenuItem from the Edit Menu is selected. It will retrieve the current selected control and perform an action based on the given action value and the type of Control that is selected.
     *
     * @param action One of the supported actions of the edit menu
     */
    boolean handleEditAction(Command action) {
        /* Retrieve the Focus Control */
        Control control = GUI.display.getFocusControl();
        /* No focus control available, return */
        if (!WidgetShop.isset(control))
            return false;

        /* Perform Edit Action */
        switch (action) {
            /* Edit action: Cut */
            case Cut:
                if (control instanceof Text)
                    ((Text) control).cut();
                else if (control instanceof StyledText)
                    ((StyledText) control).cut();
                else if (control instanceof Combo)
                    ((Combo) control).cut();
                else
                    return false;
                break;
            /* Edit action: Copy */
            case Copy:
                if (control instanceof Text)
                    ((Text) control).copy();
                else if (control instanceof StyledText)
                    ((StyledText) control).copy();
                else if (control instanceof Combo)
                    ((Combo) control).copy();
                else
                    return false;
                break;
            /* Edit action: Paste */
            case Paste:
                if (control instanceof Text)
                    ((Text) control).paste();
                else if (control instanceof StyledText)
                    ((StyledText) control).paste();
                else if (control instanceof Combo)
                    ((Combo) control).paste();
                else
                    return false;
                break;

            default:
                return false;
        }
        return true;
    }

    public void execute(Command c) {
        CommandCenter e = this;
        logger.info("Execute: " + c);

        switch (c) {
            case About:
                e.showAbout();
                break;
            case Preferences:
                Preferences.show(null);
                break;

            case Quit:
                e.actionExit();
                break;

            case Cut:
            case Copy:
            case Paste:
                break;
            case Convert:
                AudibleGUI.instance.convertSelected();
                break;

            case Rescan_Library:
                AudibleGUI.instance.refreshLibrary(false);
                break;
            case Quick_Refresh:
                AudibleGUI.instance.refreshLibrary(true);
                break;

            case Download:
                AudibleGUI.instance.downloadSelected();
                break;
            case Fetch_Decryption_Key:
                AudibleGUI.instance.fetchDecryptionKey();
                break;

            case ViewInAudible:
                AudibleGUI.instance.viewInAudible();
                break;
            case Show_MP3:
                AudibleGUI.instance.explore();
                break;

            case Show_AAX:
                break;
            case Play:
                AudibleGUI.instance.play();
                break;
            case Export_Book_List:
                Application.instance.exportBookList();
                break;
            case Check_For_Update:
                VersionCheck.instance.checkForUpdate(true);
                break;
            case Export_Web_Page:
                AudibleGUI.instance.exportWebPage();
                break;
            case Refresh_Book_Info:
                AudibleGUI.instance.refreshBookInfo();
                break;
            case AppWebPage:
                AudibleGUI.instance.browse(Version.appLink);
                break;

            case ParseAAX:
                AudibleGUI.instance.parseAAX();
                break;
            case Browser:
                AudibleGUI.instance.browse();
                break;
            case Cookies:
                AudibleGUI.instance.updateCookies(false);
                break;
            case Connect:
                AudibleGUI.instance.connect();
                break;

            case Download_All:
                AudibleGUI.instance.downloadAll();
                break;
            case Convert_All:
                AudibleGUI.instance.convertAll();
                break;
            default:
                logger.info("Unknown cmd: " + c);
        }
    }

    public boolean reallyQuit() {
        int testsInProgress = 0;

        if (testsInProgress > 0) {
            String q = GUI.isMac() ? "Quit?" : "Exit?";
            String msg = "You have " + testsInProgress;
            msg += " test(s)";
            msg += " in progress.\n\nDo you really want to " + q.toLowerCase();

            String buttons[] = {q, "Cancel"};
            int result = MessageBoxFactory.showGeneral(shell, "Are you sure you want to " + q + "?", msg, buttons, 0, null);
            return result == 0;

        }
        return true;
    }

    public int saveChanges(String msg) {
        MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
        messageBox.setMessage(msg);
        messageBox.setText("Save Changes");
        return messageBox.open();
    }

    public boolean getEnabled(Command c) {
        switch (c) {
            case Convert:
                return AudibleGUI.instance.canConvert();

            case Download:
                return AudibleGUI.instance.canDownload();

            case Show_AAX:
                break;
            case Play:
                return AudibleGUI.instance.canPlay();

            case Export_Web_Page:
                return Audible.instance.mp3Count() > 0;

            case Export_Book_List:
                return AudibleGUI.instance.bookCount() > 0;


            case ViewInAudible:
                return AudibleGUI.instance.canViewInAudible();

            case Show_MP3:
                return AudibleGUI.instance.canViewInSystem();

            case Download_All:
                return AudibleGUI.instance.canDownloadAll();
            case Convert_All:
                return AudibleGUI.instance.canConvertAll();

            case Preferences:
            case Quit:
            case About:
            case Browser:
            case Cookies:
                case AppWebPage:
                return true;

            case Copy:
            case Cut:
            case Paste:
                return false;

            case Refresh_Book_Info:
                return AudibleGUI.instance.getSelected().size() > 0;

            case Rescan_Library:
            case Quick_Refresh:
                return AudibleGUI.instance.hasLogin();

            case Connect:
                return !ConnectionNotifier.getInstance().isConnected();

            case ParseAAX:
                return AudibleGUI.instance.selectedAAXCount() > 0;

            case Fetch_Decryption_Key:
                return AudibleGUI.instance.canGetDecryptionKey();
            case Check_For_Update:
                return true;

            default:
                assert (false);
                break;

        }
        return false;
    }

}
