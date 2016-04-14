package ec2016.demo11.application;

// Quelle: http://www.vogella.com/tutorials/EclipseMicrosoftIntegration/article.html

import org.eclipse.swt.SWT;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SendEmail {

  
  public Object execute(String id, String name, String price )  {
	  
    Display display = Display.getCurrent();
    Shell shell = new Shell(display);
    OleFrame frame = new OleFrame(shell, SWT.NONE);
    // This should start outlook if it is not running yet
    OleClientSite site = new OleClientSite(frame, SWT.NONE, "Outlook.Application");
    
    site.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
    // now get the outlook application
    OleClientSite site2 = new OleClientSite(frame, SWT.NONE,
        "Outlook.Application");
    OleAutomation outlook = new OleAutomation(site2);
    // 
    OleAutomation mail = invoke(outlook, "CreateItem", 0 /* Mail item */)
        .getAutomation();
    setProperty(mail, "To", "test@pizzapizza.nsf"); 
    setProperty(mail, "BodyFormat", 2 /* HTML */);
    setProperty(mail, "Subject", "Pizza Info");
    setProperty(mail, "HtmlBody",
        "<html><h1>Pizza</h1><b>id</b> " + id + "<br><b>name</b> " + name + "<br><b>price</b> " + price + "</html>");
    invoke(mail, "Display" /* or "Send" */);
    return null;
  }

  private static Variant invoke(OleAutomation auto, String command) {
    return auto.invoke(property(auto, command));
  }

  private static Variant invoke(OleAutomation auto, String command, int value) {
    return auto.invoke(property(auto, command),
        new Variant[] { new Variant(value) });
  }

  private static boolean setProperty(OleAutomation auto, String name,
      String value) {
    return auto.setProperty(property(auto, name), new Variant(value));
  }

  private static boolean setProperty(OleAutomation auto, String name,
      int value) {
    return auto.setProperty(property(auto, name), new Variant(value));
  }

  private static int property(OleAutomation auto, String name) {
    return auto.getIDsOfNames(new String[] { name })[0];
  }

} 