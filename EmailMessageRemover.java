import java.util.Properties;
import org.jsoup.*;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.BodyPart;
import javax.mail.internet.*;
import java.io.*;
/**
 * This program demonstrates how to remove e-mail messages on a mail server
 * using JavaMail API.
 * @author www.codejava.net
 *
 */
class EmailMessageRemover {
 
    /**
     * Deletes all e-mail messages whose subject field contain
     * a string specified by 'subjectToDelete'
     * @param host
     * @param port
     * @param userName
     * @param password
     * @param subjectToDelete delete if the message's subject contains this value.
     */
	 
	public String parseBodyPart(BodyPart bodyPart) throws MessagingException, IOException { 
		if (bodyPart.isMimeType("text/html")) {
			return "\n" + org.jsoup.Jsoup
				.parse(bodyPart.getContent().toString())
				.text();
		} 
		if (bodyPart.getContent() instanceof MimeMultipart){
			return getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
		}

		return "";
	}
	public String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException
	{
		String result = "";
		for (int i = 0; i < mimeMultipart.getCount(); i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				return result + "\n" + bodyPart.getContent(); // without return, same text appears twice in my tests
			} 
			result += this.parseBodyPart(bodyPart);
		}
		return result;
	}
    public void deleteMessages(String host, String port,
            String userName, String password, String subjectToDelete) {
        Properties properties = new Properties();
 
        // server setting
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", port);
 
        // SSL setting
        properties.setProperty("mail.imap.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.imap.socketFactory.fallback", "false");
        properties.setProperty("mail.imap.socketFactory.port",
                String.valueOf(port));
 
        Session session = Session.getDefaultInstance(properties);
 
        try {
            // connects to the message store
            Store store = session.getStore("imap");
            store.connect(userName, password);
 
            // opens the inbox folder
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
			
			 BufferedReader reader = new BufferedReader(new InputStreamReader(
            System.in));
			
			Message[] messages = folder.getMessages();
			System.out.println("messages.length---" + messages.length);
			for (int i = 0; i < (messages.length-100); ++i) {
				Message message = messages[i];
				
				System.out.println("---------------------------------");
				
				System.out.println("Email Number " + (i + 1));
				System.out.println("Subject: " + message.getSubject());
				
				
				
				Object msg = message.getContent();
		
				if(msg instanceof String){				
					System.out.println("Body: " + message.getContent());
				}
				if(msg instanceof MimeMultipart){
					System.out.println("Body: " + getTextFromMimeMultipart((MimeMultipart)message.getContent()));
				}
				
				System.out.println("Sent Date:"+message.getSentDate().toString());
				System.out.println("From: " + message.getFrom()[0]);
				System.out.println("Message Size:"+message.getSize());

				String subject = message.getSubject();
				System.out.print("Do you want to delete this message [y/n] ? ");
				String ans = "y"; //reader.readLine();
				if ("Y".equals(ans) || "y".equals(ans)) {
				   // set the DELETE flag to true
				   message.setFlag(Flags.Flag.DELETED, true);
				   System.out.println("Marked DELETE for message: " + subject);
				} else if ("n".equals(ans)) {
				   break;
				}
			 }
            // fetches new messages from server
            /*Message[] arrayMessages = folderInbox.getMessages();
 
            for (int i = 0; i < arrayMessages.length; i++) {
				
                Message message = arrayMessages[i];
                String subject = message.getSubject();
                if (subject.contains(subjectToDelete)) {
                    message.setFlag(Flags.Flag.DELETED, true);
                    System.out.println("Marked DELETE for message: " + subject);
                }
 
            }*/
 
            // expunges the folder to remove messages which are marked deleted
            boolean expunge = true;
            folder.close(expunge);
 
            // another way:
            //folderInbox.expunge();
            //folderInbox.close(false);
 
            // disconnect
            store.close
			();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        } catch (IOException io) {
			io.printStackTrace();
		}
			
    }
 
    /**
     * Runs this program to delete e-mail messages on a Gmail account
     * via IMAP protocol.
     */
    public static void main(String[] args) {
        String host = "imap.gmail.com";
        String port = "993";
        String userName = "narellaeswar160@gmail.com";
        String password = "jawwqxzzsapzzgaw";
        EmailMessageRemover remover = new EmailMessageRemover();
 
        // try to delete all messages contain this string its Subject field
        String subjectToDelete = "Newsletter";
        remover.deleteMessages(host, port, userName, password, subjectToDelete);
 
    }
}