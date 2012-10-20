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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.mail;

import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemUser;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.ItemUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataSource;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.context.MessageSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

/**
 * Class to handle sending of E-mail and pre-formatted messages
 */
public class MailSender {
    
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(MailSender.class);

	public static final Integer ENCRYPTION_PGP = 10;
	public static final Integer ENCRYPTION_SMIME = 20;    
    
    private JavaMailSenderImpl sender;
    private String prefix;
    private String from;
    private String url;
    private MessageSource messageSource;
    private Locale defaultLocale;
    
    public MailSender(Map<String, String> config, MessageSource messageSource, String defaultLocale) {
        // initialize email sender
        this.messageSource = messageSource;
        this.defaultLocale = StringUtils.parseLocaleString(defaultLocale);
        String mailSessionJndiName = config.get("mail.session.jndiname");
        if(StringUtils.hasText(mailSessionJndiName)) {
            initMailSenderFromJndi(mailSessionJndiName);
        }
        if(sender == null) {            
            initMailSenderFromConfig(config);
        }
        // if sender is still null the send* methods will not
        // do anything when called and will just return immediately
        
    }

    /**
     * we bend the rules a little and fire off a new thread for sending
     * an email message.  This has the advantage of not slowing down the item
     * create and update screens, i.e. the system returns the next screen
     * after "submit" without blocking.  This has been used in production
     * for quite a while now, on Tomcat without any problems.  This helps a lot
     * especially when the SMTP server is slow to respond, etc.
     */
    private void sendInNewThread(final MimeMessage message) {
//    	try {
//    		logger.info("Sending message: " + message.getSubject() + "\n" + message.getContent());
//		} catch (MessagingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
    	if(logger.isDebugEnabled()){
    		try {
        		logger.debug("Message contenttype: "+message.getContentType());
        		logger.debug("Message content: "+message.getContent());
		    	Enumeration headers = message.getAllHeaders();

        		logger.debug("Message Headers...");
		    	 while(headers.hasMoreElements()) {
		             Header h = (Header) headers.nextElement();
		             logger.error(h.getName() + ": " + h.getValue());
		         }
		    	 logger.debug("Message flags: "+message.getFlags());
    		} catch (MessagingException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        new Thread(){
            @Override
            public void run() {
                logger.debug("send mail thread start");
                try {
                    try {
                        sender.send(message);
                        logger.debug("send mail thread successfull");
                    } catch (Exception e) {
                        logger.error("send mail thread failed, dumping headers: ");
                        logger.error("mail headers dump start");                    
                        Enumeration headers = message.getAllHeaders();
                        while(headers.hasMoreElements()) {
                            Header h = (Header) headers.nextElement();
                            logger.error(h.getName() + ": " + h.getValue());
                        }
                        logger.error("mail headers dump end, exception follows", e);
                    }
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
    
    private String fmt(String key, Locale locale) {
        try {
            return messageSource.getMessage("mail_sender." + key, null, locale);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return "???mail_sender." + key + "???";
        }
    }    

    private String addHeaderAndFooter(StringBuffer html) {
        return addHeaderAndFooter(html.toString());
    }
    
    private String addHeaderAndFooter(String html) {
        StringBuffer sb = new StringBuffer();
        // additional cosmetic tweaking of e-mail layout
        // style just after the body tag does not work for a minority of clients like gmail, thunderbird etc.
        // ItemUtils adds the main inline CSS when generating the email content, so we gracefully degrade
        sb.append("<style type='text/css'>table.jtrac th, table.jtrac td { padding-left: 0.2em; padding-right: 0.2em; } .footer{text-align:center;color:#555555;font-family:Georgia,'Times New Roman',Times,serif;font-size:smaller;} a,a:link,a:visited{color:#3B5998;text-decoration:none}</style>");
        sb.append(html);
        sb.append("<p class='footer'>Powered by <a href='http://calipso.abiss.gr' target='_blank'>Calipso</a></p>");
        
        return sb.toString();
    }
    
    private String getItemViewAnchor(Item item, Locale locale) {
        String itemUrl = url + "app/item/" + item.getUniqueRefId();
        return "<p style='font-family: Arial; font-size: 75%'><a href='" + itemUrl + "'>" + itemUrl + "</a></p>";
    }

    private String getSubject(Item item, Locale locale) {       
        StringBuffer summary = new StringBuffer(prefix)
        	.append(" ")
        	.append("#").append(item.getUniqueRefId())
        	.append(": ");
        if (item.getSummary().length() > 80) {
            summary.append(item.getSummary().substring(0, 80));
        } else {
        	summary.append(item.getSummary());
        }
        return summary.toString();
    }
    
    private String getSubjectForDueIn24HoursNotification(Item item, Locale locale) {       
        StringBuffer summary = new StringBuffer(prefix)
	    	.append(" ")
	    	.append(this.fmt("dueIn24HoursSubject", locale))
	    	.append(" (#")
	    	.append(item.getUniqueRefId())
	    	.append(")");
	    return summary.toString();
    }

    /**
     * Due in 24 hours notifications
     * @param item
     */
    public void sendDueIn24HoursNotifications(Item item) {
    	send(item, true);
    }
    
    /**
     * Common workflow notifications
     * @param item
     */
    public void send(Item item) {
    	send(item, false);
    }
    
    
    public void send(Item item, boolean due) {
        if (sender == null) {
            logger.debug("mail sender is null, not sending notifications");
            return;
        }
        
        Set<User> recipients = getRecipients(item);
        Map<String,String> messagesByLocale = new HashMap<String,String>();
        
        // iterate recipients to send them a personalized (i.e. locale sensitive) email
        if(CollectionUtils.isNotEmpty(recipients)){
        	for(User recipient : recipients){
        		Locale userLocale = getUserLocale(recipient);
        		String subject = due ? getSubjectForDueIn24HoursNotification(item, userLocale) : getSubject(item, userLocale);
        		// Create message body
        		String messageBody = messagesByLocale.get(recipient.getLocale());
        		if(messageBody == null){
            		StringBuffer sb = new StringBuffer();
            		 // prepare message content
                    String anchor = getItemViewAnchor(item, userLocale);
                    sb.append(anchor);
                    sb.append(ItemUtils.getAsHtml(item, messageSource, userLocale));
                    sb.append(anchor);
                    // init message body
                    messageBody = sb.toString();
                    // cache for the given locale
                    messagesByLocale.put(recipient.getLocale(), messageBody);
        		}
        		
        		 send(recipient, subject, messageBody);    
        		
        		
        		
        	}
        }
        
        logger.debug("attempting to send mail for item update");
       
                 
    }

	public void send(User recipient, String subject, String messageBody) {
		send(recipient.getEmail(), subject, messageBody);
	}

	public void send(String email, String subject, String messageBody) {
		send(email, subject, messageBody, null);
	}

	/**
	 * 
	 * @param email
	 * @param subject
	 * @param messageBody
	 * @param attachments
	 * @param encryption
	 * @param keyStorePath
	 */
	public void send(String email, String subject, String messageBody, Map<String, DataSource> attachments, boolean html) {
		// prepare message
		try {
		    MimeMessage message = sender.createMimeMessage();
		    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		    if(html){ 
		    	helper.setText(addHeaderAndFooter(messageBody), true);
		    }
		    else{
		    	helper.setText(messageBody, false);
		    }
		    helper.setSubject(subject);
		    helper.setSentDate(new Date());
		    helper.setFrom(from);
		    // set TO
		    helper.setTo(email);
		    if(MapUtils.isNotEmpty(attachments)){
		    	for(String attachmentFilename : attachments.keySet()){
		    		DataSource in = attachments.get(attachmentFilename);
		    		if(in != null){
		    			helper.addAttachment(attachmentFilename, in);
		    		}
		    	}
		    }

		    //logger.info("Sending email: "+subject+"\n"+messageBody);
		    sendInNewThread(message);
		} catch (Exception e) {
		    logger.error("failed to prepare e-mail", e);
		}
	}

	public void send(String email, String subject, String messageBody, Map<String, DataSource> attachments) {
		send(email, subject, messageBody,attachments, true); 
	}
	
	/**
	 * @param item
	 */
	private Set<User> getRecipients(Item item) {
		Set<User> recipients = new HashSet<User>();
        if (item.getAssignedTo() != null 
        		&& !item.getReportedBy().getEmail().equals(item.getAssignedTo().getEmail())) {
        	recipients.add(item.getAssignedTo());
        }
        if(CollectionUtils.isNotEmpty(item.getItemUsers())){
            for (ItemUser itemUser : item.getItemUsers()) {
            	recipients.add(itemUser.getUser());
            }
        }
        return recipients;
	}
    
    public void sendUserPassword(User user, String clearText) {
        if (sender == null) {
            logger.warn("mail sender is null, not sending new user / password change notification");
            return;
        }
        if(logger.isDebugEnabled()){
            logger.debug("attempting to send mail for user password");
        }
        Locale locale= getUserLocale(user);
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(prefix + " " + fmt("loginMailSubject", locale));
            StringBuffer sb = new StringBuffer();
            String greeting = fmt("loginMailGreeting", locale);
            if(org.apache.commons.lang.StringUtils.isNotBlank(greeting)){
                sb.append("<p>" + fmt("loginMailGreeting", locale) + " " + user.getName()+ ",</p>"); 
            }     
            sb.append("<p>" + fmt("loginMailLine1", locale) + "</p>");           
            sb.append("<table class='calipsoService'>");
            sb.append("<tr><th style='background: #CCCCCC'>" + fmt("loginName", locale) 
                + ":&nbsp;</th><td style='border: 1px solid black'>" + user.getLoginName() + "&nbsp;</td></tr>");
            sb.append("<tr><th style='background: #CCCCCC'>" + fmt("password", locale) 
                + ":&nbsp;</th><td style='border: 1px solid black'>" + clearText + "&nbsp;</td></tr>");
            sb.append("</table>");
            sb.append("<p>" + fmt("loginMailLine2", locale) + "</p>");       
            sb.append("<p><a href='" + url + "'>" + url + "</a></p>");
            sb.append("<p>" + fmt("loginMailLine3", locale) + "</p>");  
            helper.setText(addHeaderAndFooter(sb), true);
            helper.setSentDate(new Date());
            // helper.setCc(from);
            helper.setFrom(from);
            sendInNewThread(message);
        } catch (Exception e) {
            logger.error("failed to prepare e-mail", e);
        }
    }

	/**
	 * @param user
	 * @return
	 */
	private Locale getUserLocale(User user) {
		Locale locale;
		String localeString = user.getLocale();
        if(localeString == null) {
            locale = defaultLocale;
        } else {
            locale = StringUtils.parseLocaleString(localeString);
        }
		return locale;
	}
    
    private void initMailSenderFromJndi(String mailSessionJndiName) {
        //logger.info("attempting to initialize mail sender from jndi name = '" + mailSessionJndiName + "'");
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiName(mailSessionJndiName);    
        // "java:comp/env/" will be prefixed if the JNDI name doesn't already have it
        factoryBean.setResourceRef(true);        
        try {
            // this step actually does the JNDI lookup
            factoryBean.afterPropertiesSet();
        } catch(Exception e) {
            logger.warn("failed to locate mail session : " + e);
            return;
        }
        Session session = (Session) factoryBean.getObject();
        sender = new JavaMailSenderImpl();
        sender.setSession(session);
        logger.info("email sender initialized from jndi name = '" + mailSessionJndiName + "'");        
    }
    
    private void initMailSenderFromConfig(Map<String, String> config) {
        String host = config.get("mail.server.host");
        if (host == null) {
            logger.warn("'mail.server.host' config is null, mail sender not initialized");
            return;
        }        
        String port = config.get("mail.server.port");       
        String tempUrl = config.get("calipso.url.base");
        from = config.get("mail.from");
        prefix = config.get("mail.subject.prefix");
        String userName = config.get("mail.server.username");
        String password = config.get("mail.server.password");
        String startTls = config.get("mail.server.starttls.enable");
        logger.info("initializing email adapter: host = '" + host + "', port = '"
                + port + "', url = '" + url + "', from = '" + from + "', prefix = '" + prefix + "'");        
        this.prefix = prefix == null ? "[calipso]" : prefix;
        this.from = from == null ? "calipsoService" : from;
        this.url = tempUrl == null ?  "http://localhost/calipso/" : tempUrl;
        if (!this.url.endsWith("/")) {
            this.url = url + "/";
        }          
        int p = 25;
        if (port != null) {
           try {
               p = Integer.parseInt(port);
           } catch (NumberFormatException e) {
               logger.warn("mail.server.port not an integer : '" + port + "', defaulting to 25");
           }
        }
        sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(p);
        if (userName != null) {
            // authentication requested
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            if (startTls != null && startTls.toLowerCase().equals("true")) {
                props.put("mail.smtp.starttls.enable", "true");
            }
            sender.setJavaMailProperties(props);
            sender.setUsername(userName);
            sender.setPassword(password);
        }
        logger.info("email sender initialized from config: host = '" + host + "', port = '" + p + "'");        
    }
    
}
