package example;
import com.pff.*;

import java.text.DecimalFormat;
import java.util.*;

public class Test {
	public static void main(String[] args)
	{
		new Test(args[0]);
	}

	public Test(String filename) {
		try {
			PSTFile pstFile = new PSTFile(filename);
			System.out.println(pstFile.getMessageStore().getDisplayName());
			processFolder(pstFile.getRootFolder());
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	int depth = -1;
	DecimalFormat twoDForm = new DecimalFormat("#.##"); 
	public void processFolder(PSTFolder folder)
			throws PSTException, java.io.IOException
	{
		depth++;
		// the root folder doesn't have a display name
		if (depth > 0) {
			printDepth();
			System.out.println(folder.getDisplayName());
		}

		// go through the folders...
		if (folder.hasSubfolders()) {
			Vector<PSTFolder> childFolders = folder.getSubFolders();
			for (PSTFolder childFolder : childFolders) {
				processFolder(childFolder);
			}
		}

		// and now the emails for this folder
		if (folder.getContentCount() > 0) {
			depth++;
			PSTObject emailObj = folder.getNextChild();
			PSTMessage email = (PSTMessage)emailObj;
			while (email != null) {
				printDepth();
				int num = email.getNumberOfAttachments();
				long sizeAtt = 0;
				for(int i =0; i<num; i++) {
				    PSTAttachment att = email.getAttachment(i);
				    sizeAtt = sizeAtt + att.getFilesize();
				}
				System.out.println("Email: "+ email.getInternetMessageId() + " - " + email.getSubject() + " - " + email.getClientSubmitTime().toString());
				printDepth();
				System.out.println("        FROM: " + email.getDisplayName() + ", TO: " + email.getDisplayTo() + ", CC: " + email.getDisplayCC()+ ", RE: " + email.getInReplyToId() + 
				    "SIZE FILES: " + twoDForm.format(sizeAtt / 1024.0) + " KiB, " + " TOTAL SIZE: " + twoDForm.format(email.getMessageSize() / 1024.0) + " KiB");
				email = (PSTMessage)folder.getNextChild();
			}
			depth--;
		}
		depth--;
	}

	public void printDepth() {
		for (int x = 0; x < depth-1; x++) {
			System.out.print(" | ");
		}
		System.out.print(" |- ");
	}
}
