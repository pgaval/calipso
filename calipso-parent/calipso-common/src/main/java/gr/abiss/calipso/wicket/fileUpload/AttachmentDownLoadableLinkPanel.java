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

package gr.abiss.calipso.wicket.fileUpload;

import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.util.AttachmentUtils;
import gr.abiss.calipso.wicket.BasePanel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.BufferedDynamicImageResource;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.io.Streams;

import sun.net.www.MimeTable;

/**
 * 
 *         A link for downloading attachments
 * 
 */
public class AttachmentDownLoadableLinkPanel extends BasePanel {
	public AttachmentDownLoadableLinkPanel(String id,
			final Attachment attachment) {
		super(id);
		renderSighslideDirScript();

		final String filaName = attachment.getFileName();
		String downloadLabel = null;

		// attachment link
		Link link = new Link("attachmentLink") {

			public void onClick() {

				getRequestCycle().scheduleRequestHandlerAfterCurrent(new IRequestHandler() {

					public void respond(IRequestCycle requestCycle) {
						WebResponse r = (WebResponse) requestCycle
								.getResponse();
						try {
							String ua = ((WebRequest) requestCycle.getRequest())
									.getHeader(
											"User-Agent");
							boolean isMSIE = (ua != null && ua.indexOf("MSIE") != -1);
							logger.debug("Client browser is IE - " + isMSIE);
							if (isMSIE) {
								r.setAttachmentHeader(URLEncoder.encode(
										attachment.getFileName(), "UTF-8")
										.replaceAll("\\+", "%20"));
							} else {

								// This works in FireFox - NEW W3C STANDART
								// See
								// http://greenbytes.de/tech/webdav/draft-reschke-rfc2231-in-http-latest.html#RFC2231
								r.setHeader(
										"Content-Disposition",
										"attachment; filename*=UTF-8''"
												+ URLEncoder.encode(
														attachment
																.getFileName(),
														"UTF-8").replaceAll(
														"\\+", "%20"));
							}
							r.setContentType(MimeTable
									.getDefaultTable()
									.getContentTypeFor(attachment.getFileName()));
						} catch (UnsupportedEncodingException e1) {
							logger.error("Some mess in Encoding");
							r.setAttachmentHeader(attachment.getFileName());
						}
						try {
							File file = AttachmentUtils.getSavedAttachmentFile(
									attachment, getCalipso().getCalipsoHome());
							InputStream is = new FileInputStream(file);
							try {
								Streams.copy(is, r.getOutputStream());
							} catch (IOException e) {
								throw new RuntimeException(e);
							} finally {
								try {
									is.close();
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							}
						} catch (FileNotFoundException e) {
							throw new RuntimeException(e);
						}
					}

					public void detach(IRequestCycle requestCycle) {
						// TODO Auto-generated method stub
						
					}
				});

				// }
				// }

			}
		};
		downloadLabel = attachment.getFileName();
		add(link);
		link.add(new Label("fileName", downloadLabel));
	}
}
