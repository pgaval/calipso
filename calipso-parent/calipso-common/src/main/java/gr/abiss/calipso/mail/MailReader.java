/*
 * Copyright (c) 2007 - 2010 Abiss.gr <info@abiss.gr>  
 *
 *  This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 *  Calipso is free software: you can redistribute it and/or modify 
 *  it under the terms of the GNU Affero General Public License as published by 
 *  the Free Software Foundation, either version 3 of the License, or 
 *  (at your option) any later version.
 * 
 *  Calipso is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU Affero General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License 
 *  along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */


package gr.abiss.calipso.mail;

import gr.abiss.calipso.domain.MailedItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.sun.mail.pop3.POP3Folder;

public class MailReader {
	private static final Logger logger = Logger.getLogger(MailReader.class);
	private String mailHost;
	private String mailUser;
	private String mailPass;
	private String mailedTicketSignificant;
	private List<MailedItem> mailedItemsList;
	private boolean hasMailedTicketSignificant;
	

	///////////////////////////////////////////////////////////////////////////////////////////////

	public MailReader(String mailHost, String mailUser, String mailPass, String mailedTicketSignificant){
		if(mailHost != null && mailUser != null && mailPass != null){
			this.mailHost = mailHost;
			this.mailUser = mailUser;
			this.mailPass = mailPass;
			this.mailedTicketSignificant = mailedTicketSignificant;
			this.mailedItemsList = new ArrayList<MailedItem>();
			hasMailedTicketSignificant = (this.mailedTicketSignificant!=null && this.mailedTicketSignificant!=null);
	
			try{
				this.run();
		    }//try
		    catch (Exception e){
	//	    	logger.error("MailReader:: " + e.getMessage());
		    	//e.printStackTrace();
		    	logger.error("Error reading mail: " + e.getMessage(), e);
		    }//catch
		}
		else{
			logger.warn("MailReader was called to scan for emailed Items but it has not yet been configured. Required config properties are mailedItem.mailServer, mailedItem.mailUserNameAccount and mailedItem.mailUserNamePassword");
		}
	}

	//---------------------------------------------------------------------------------------------

	private void run() throws Exception{
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore("pop3");
		store.connect(this.mailHost, this.mailUser, this.mailPass);
		POP3Folder inbox = (POP3Folder)store.getFolder("inbox");

		if (inbox.exists() && !inbox.isOpen()){
			inbox.open(Folder.READ_WRITE);
			Message messages[] = inbox.getMessages();

			logger.debug("Reading inbox....");

			for (int messageIndex = 0; messageIndex<messages.length; messageIndex++){
				Message message = messages[messageIndex];
				if (this.mailedTicketSignificant==null || 
					(this.mailedTicketSignificant!=null && this.mailedTicketSignificant.trim()=="") ||
					(this.mailedTicketSignificant!=null && this.mailedTicketSignificant.trim()!="" && message.getSubject().startsWith(this.mailedTicketSignificant))){
					try{
						MailedItem mailedItem = parseMailedItem(message);
						if (mailedItem!=null){//Valid mailed item
							mailedItemsList.add(mailedItem);
						}//if
					}//try
					catch(MessagingException messagingException){
						//TODO Handle MessagingException
						logger.error("Error parsing mail: MessagingException: " + messagingException.getMessage());
					}//catch
					catch(IOException exception){
						//TODO Handle IOException
						logger.error("Error parsing mail: IOException: " + exception.getMessage());
					}//catch
				}//if
			}//for
			
			logger.debug("DONE");
		}//if
		
		inbox.close(false);
		store.close();
		
	}

	//---------------------------------------------------------------------------------------------
	
	private MailedItem parseMailedItem(Message message) throws MessagingException, IOException{
		MailedItem mailedItem = new MailedItem();

		//Get mail subject
		String subject = message.getSubject();

		//Set Space Code / Space Description
		int mailedTicketSignificantLength = 0;
		if (hasMailedTicketSignificant){
			mailedTicketSignificantLength = this.mailedTicketSignificant.length();
		}
		int spaceBeginPosition = mailedTicketSignificantLength+1;
		int spaceEndPosition = subject.indexOf("]", spaceBeginPosition);
		//Invalid Subject Format
		if (spaceEndPosition==-1){
			return null;
		}
		String spacePart = subject.substring(spaceBeginPosition, spaceEndPosition);
		mailedItem.setSpace(spacePart);
		
		//Set Ticket Summary
		String summary = subject.substring(spaceEndPosition+1).trim();
		mailedItem.setSummary(summary);

		//Set Ticket Detail
		mailedItem.setDetail(parseEMailBody(message.getContent()));

		Address[] addresses = message.getFrom();
		if (addresses!=null && addresses.length>0){
			mailedItem.setLoggedByAccount(parseEMailAddress(addresses[0].toString()));
		}//if

		//Set Ticket TimeStamp
		mailedItem.setTimeStamp(Calendar.getInstance().getTime());

		return mailedItem;
	}//parseMailedItem

	//---------------------------------------------------------------------------------------------

	private String parseEMailAddress(String address){
		String emailAdress = "";
		int emailAddressBeginIndex = address.indexOf("<");
		int emailAddressEndIndex = address.indexOf(">");
		
		if (emailAddressBeginIndex>-1){
			emailAdress = address.substring(emailAddressBeginIndex+1, emailAddressEndIndex);
		}//if
		else{
			emailAdress = address;
		}//else
		
		return emailAdress;
	}//parseEMailAddress

	//---------------------------------------------------------------------------------------------

	private String parseEMailBody(Object content) throws MessagingException, IOException{
		String mailBody = "";
		
		if (content instanceof Multipart){
			MimeMultipart multipart = (MimeMultipart)content;
			if (multipart.getCount()>0){
				mailBody = multipart.getBodyPart(0).getContent().toString();
			}//if
		}//if
		else{
			mailBody = content.toString();
		}//else

		return mailBody;
	}//parseEMailBody

	//---------------------------------------------------------------------------------------------

	public List<MailedItem> getMailedItemsList() {
		return mailedItemsList;
	}//getMailedItemsList
}
