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

package gr.abiss.calipso.wicket;

import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.util.AttachmentUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

import java.net.URLEncoder;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.BufferedDynamicImageResource;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.io.Streams;

import sun.net.www.MimeTable;

/**
 * link for downloading an attachment
 */
public class AttachmentLinkPanel extends BasePanel {
	
	public AttachmentLinkPanel(String id, final Attachment attachment) {
		this(id, attachment, true);
	}
	
	public AttachmentLinkPanel(String id, final Attachment attachment, boolean addHeadScript) {
		super(id);
		if(addHeadScript){
			renderSighslideDirScript();
		}
		WebMarkupContainer attachmentContainer = new WebMarkupContainer(
				"attachmentContainer");
		add(attachmentContainer);
		if (attachment == null) {
			attachmentContainer.setVisible(false);
			return;
		}
		final String fileName = getResponse().encodeURL(
				attachment.getFileName()).toString();
		String downloadLabel = null;
		WebMarkupContainer imageAttachmentContainer = new WebMarkupContainer(
				"imageAttachment");
		attachmentContainer.add(imageAttachmentContainer);
		// if attachment is image, preview it
		if (fileName.endsWith(".png") || fileName.endsWith(".gif")
				|| fileName.endsWith(".bmp") || fileName.endsWith(".jpeg")
				|| fileName.endsWith(".jpg")) {
			BufferedImage icon = null;
			// read image
			try {
				File imageFileThumb = new File(getCalipso().getCalipsoHome()+File.separator+attachment.getBasePath()+File.separator+"thumb_"+attachment.getFileName());
				if(imageFileThumb.exists()){
					icon = ImageIO.read(imageFileThumb);
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to read thumb image", e);
			}
			// render html
			if (icon != null) {
				BufferedDynamicImageResource iconResource = new BufferedDynamicImageResource();
				iconResource.setImage(icon);
				Image image = new Image("imageThumb", iconResource);
				Link imageAttachmentLink = new Link("attachment") {
					// adapted from wicket.markup.html.link.DownloadLink
					// with the difference that the File is instantiated only
					// after onClick
					public void onClick() {
						getRequestCycle().scheduleRequestHandlerAfterCurrent(
								new IRequestHandler() {

									public void respond(
											IRequestCycle requestCycle) {
										WebResponse r = (WebResponse) requestCycle
												.getResponse();
										r.setAttachmentHeader(fileName);
										try {
											File previewfile = new File(getCalipso().getCalipsoHome()+File.separator+attachment.getBasePath()+File.separator+"small_"+attachment.getFileName());
											logger.info("Looking for previewfile path: "+previewfile.getAbsolutePath());
											InputStream is = new FileInputStream(
													previewfile);
											try {
												Streams.copy(is, r
														.getOutputStream());
											} catch (IOException e) {
												throw new RuntimeException(e);
											} finally {
												try {
													is.close();
												} catch (IOException e) {
													throw new RuntimeException(
															e);
												}
											}
										} catch (FileNotFoundException e) {
											throw new RuntimeException(e);
										}
									}

									public void detach(
											IRequestCycle requestCycle) {
										// TODO Auto-generated method stub
										
									}});
					}
				};
				imageAttachmentLink.add(image);
				imageAttachmentContainer.add(imageAttachmentLink);
				downloadLabel = attachment.isSimple()?attachment.getOriginalFileName():localize("item_view_form.download");
			} else {
				imageAttachmentContainer.setVisible(false);
			}
		} else {
			imageAttachmentContainer.setVisible(false);
		}
		// attachment link
		Link link = new Link("attachment") {
			// adapted from wicket.markup.html.link.DownloadLink
			// with the difference that the File is instantiated only after
			// onClick
			public void onClick() {
				getRequestCycle().scheduleRequestHandlerAfterCurrent(
						new IRequestHandler() {

							public void respond(IRequestCycle requestCycle) {
								WebResponse r = (WebResponse) requestCycle
										.getResponse();
								try {
									String ua = ((WebRequest) requestCycle.getRequest()).getHeader(
													"User-Agent");
									boolean isMSIE = (ua != null && ua.indexOf("MSIE") != -1);
									logger.debug("Client browser is IE - " + isMSIE);
									if (isMSIE) {
										r.setAttachmentHeader(URLEncoder.encode(
												fileName, "UTF-8").replaceAll("\\+",
												"%20"));
									} else {
										// This works in FireFox - NEW W3C STANDART
										// See
										// http://greenbytes.de/tech/webdav/draft-reschke-rfc2231-in-http-latest.html#RFC2231
										r.setHeader("Content-Disposition",
												"attachment; filename*=UTF-8''"
														+ URLEncoder.encode(attachment.isSimple()?attachment.getOriginalFileName():fileName,
																"UTF-8").replaceAll(
																"\\+", "%20"));
									}
									r.setContentType(MimeTable.getDefaultTable().getContentTypeFor(fileName));
								} catch (UnsupportedEncodingException e1) {
									logger.error("Error encoding", e1);
									r.setAttachmentHeader(fileName);
								}
								try {
									File file = AttachmentUtils.getSavedAttachmentFile(attachment, getCalipso().getCalipsoHome());
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
								
							}});
			}
		};
		if (downloadLabel == null){
			downloadLabel = fileName;
		}
		link.add(new Label("fileName", downloadLabel));
		attachmentContainer.add(link);
	}
}
