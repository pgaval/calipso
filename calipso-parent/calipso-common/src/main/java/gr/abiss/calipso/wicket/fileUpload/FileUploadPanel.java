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


import gr.abiss.calipso.wicket.BasePanel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Application;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;


/**
 *
 */
public class FileUploadPanel extends BasePanel
{
    /** Log. */
    private static final Log log = LogFactory.getLog(FileUploadPanel.class);

    /** Reference to listview for easy access. */
    private final FileListView fileListView;
    private Folder uploadFolder = null;
    
	/**
	 * 
	 * @param id
	 * 			Component id
	 */
	public FileUploadPanel(String id){
		super(id);
	    Folder uploadFolder = getUploadFolder();
        // Add folder view
	    // add(new Label("dir", uploadFolder.getAbsolutePath()));
        fileListView = new FileListView("fileList", new LoadableDetachableModel()
        {
            @Override
            protected List<File> load()
            {
            	// list of files inside directory
                return Arrays.asList(getUploadFolder().listFiles());
            }
        });
        add(fileListView);

        // Add upload form with ajax progress bar
        final FileUploadForm ajaxSimpleUploadForm = new FileUploadForm("ajax-simpleUpload");
        ajaxSimpleUploadForm.add(new UploadProgressBar("progress", ajaxSimpleUploadForm));
        add(ajaxSimpleUploadForm);
	}
    /**
     * List view for files in upload folder.
     */
    private class FileListView extends ListView
    {
        /**
         * Construct.
         * 
         * @param name
         *            Component name
         * @param files
         *            The file list model
         */
        public FileListView(String name, final IModel files)
        {
            super(name, files);
        }

        /**
         * @see ListView#populateItem(ListItem)
         */
        @Override
        protected void populateItem(ListItem listItem)
        {
            final File file = (File) listItem.getModelObject();
            listItem.add(new Label("file", file.getName()));
            listItem.add(new Link("delete")
            {
                @Override
                public void onClick()
                {
                    Files.remove(file);
                    info("Deleted " + file);
                }
            });
        }
    }

    /**
     * Form for uploads.
     */
    private class FileUploadForm extends Form
    {
        private FileUploadField fileUploadField;

        /**
         * Construct.
         * 
         * @param name
         *            Component name
         */
        public FileUploadForm(String name)
        {
            super(name);

            // set this form to multipart mode (always needed for uploads!)
            setMultiPart(true);

            // Add one file input field
            add(fileUploadField = new FileUploadField("fileInput"));

            // Set maximum size to 100K for demo purposes
            setMaxSize(Bytes.kilobytes(100));
        }

        	
        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */
        @Override
        protected void onSubmit()
        {
        	
            final org.apache.wicket.markup.html.form.upload.FileUpload upload = fileUploadField.getFileUpload();
            if (upload != null)
            {
                // Create a new file
                File newFile = new File(getUploadFolder(), upload.getClientFileName());

                // Check new file, delete if it allready existed
                checkFileExists(newFile);
                try
                {
                    // Save to new file
                    newFile.createNewFile();
                    upload.writeTo(newFile);
                    // send message to the user that the file has been saved
                    FileUploadPanel.this.info("saved file: " + upload.getClientFileName());
                }
                catch (Exception e)
                {
                    throw new IllegalStateException("Unable to write file");
                }
            }
        }
    }
    /**
     * Check whether the file already exists, and if so, try to delete it.
     * 
     * @param newFile
     *            the file to check
     */
    private void checkFileExists(File newFile)
    {
        if (newFile.exists())
        {
        	// error("File already exists");
            // Try to delete the file
            if (!Files.remove(newFile))
            {
                throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
            }
        }
    }

    private Folder getUploadFolder()
    {
    	/*
    	 * The location of the directory used to hold temporary files is defined by the property java.io.tmpdir.
    	 * The default value can be changed with the command line used to launch the JVM
    	 * user-uploads is the new directory that we create inside java.io.tmpdir directory
    	 */
    	uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "user-uploads");
        // Ensure folder exists
        uploadFolder.mkdirs();
        return uploadFolder;
    	
    }
    
    
}