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

package gr.abiss.calipso.domain;

import gr.abiss.calipso.util.ItemUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.log4j.Logger;

/**
 * Class that encapsulates an Excel Sheet / Workbook
 * and is used to process, cleanse and import contents of an 
 * uploaded excel file into JTrac
 */
public class ExcelFile implements Serializable {    
    
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ExcelFile.class);    
    
    /**
     * represents a column heading and data type
     */
    public class Column {        
        
        private String label;
        private Field field;
        
        public Column(String label) {
            this.label = label;
        }

        public Field getField() {
            return field;
        }

        public String getLabel() {
            return label;
        }       
        
    }
    
    /**
     * represents a cell value, acts as object holder
     */    
    public class Cell {
        
        private Object value;

        public Cell(Object value) {
            this.value = value;
        }       
        
        @Override
        public String toString() {
            if (value == null) {
                return "";
            }
            if (value instanceof String) {
                return ItemUtils.fixWhiteSpace((String) value);
            }
            return value.toString();
        }
        
    }
    
    private List<Column> columns;
    private List<List<Cell>> rows;

    public List<List<Cell>> getRows() {
        return rows;
    }

    public List<Column> getColumns() {
        return columns;
    }
    
    //==========================================================================
    // form binding stuff
    
    private int[] selCols;
    private int[] selRows;
    private int action;

    public int getAction() {
        return action;
    }
    
    public void setAction(int action) {
        this.action = action;
    }
     
    public int[] getSelCols() {
        return selCols;
    }

    public void setSelCols(int[] selCols) {
        this.selCols = selCols;
    }

    public int[] getSelRows() {
        return selRows;
    }

    public void setSelRows(int[] selRows) {
        this.selRows = selRows;
    }    
    
    //==========================================================================
    // edits
    
    /* note that selected rows and columns would be set by spring MVC */
    public void deleteSelectedRowsAndColumns() {
        int cursor = 0;
        if (selRows != null) {
            for(int i : selRows) {
                rows.remove(i - cursor);
                cursor++;
            }
        }
        cursor = 0;
        if (selCols != null) {
            for(int i : selCols) {
                columns.remove(i - cursor);
                for(List<Cell> cells : rows) {                
                    cells.remove(i - cursor);
                }
                cursor++;
            }
        }
    }
    
    public void convertSelectedColumnsToDate() {
        if (selCols == null) {
            return;            
        }
        // could not find a better way to convert excel number to date
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell((short) 0);
        for(int i : selCols) {
            for(List<Cell> cells : rows) {
                Cell c = cells.get(i);                
                if (c != null && c.value instanceof Double) {                    
                    cell.setCellValue((Double) c.value);                    
                    c.value = cell.getDateCellValue();
                }
            }            
        }        
    }
    
    public void concatenateSelectedColumns() {
        if (selCols == null) {
            return;            
        }
        List<Cell> list = new ArrayList<Cell>(rows.size());
        for(List<Cell> cells : rows) {
            list.add(new Cell(null));
        }
        int first = selCols[0];
        for(int i : selCols) {
            int rowIndex = 0;
            for(List<Cell> cells : rows) {
                Cell c = cells.get(i);                
                if (c != null) {
                    String s = (String) list.get(rowIndex).value;                    
                    if (s == null) {
                        s = (String) c.value;                        
                    } else {
                        s += "\n\n" + c.value;
                    }                    
                    list.set(rowIndex, new Cell(s));
                }
                rowIndex++;
            }            
        }
        // update the first column
        int rowIndex = 0;
        for(List<Cell> cells : rows) {
            cells.set(first, list.get(rowIndex));
            rowIndex++;
        }
    }
    
    public void extractSummaryFromSelectedColumn() {
        if (selCols == null) {
            return;            
        }
        int first = selCols[0];           
        for(List<Cell> cells : rows) {
            Cell c = cells.get(first);                
            if (c != null && c.value != null) {
                String s = c.value.toString();
                if (s.length() > 80) {
                    s = s.substring(0, 80);
                }
                cells.add(0, new Cell(s));                
            } else {
                cells.add(0, null);
            }         
        }         
        columns.add(0, new Column("Summary"));   
    }
    
    //==========================================================================
    
    public ExcelFile() {
        // zero arg constructor
    }    
    
    public ExcelFile(InputStream is) {
        POIFSFileSystem fs = null;
        HSSFWorkbook wb = null;
        try {
            fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow r = null;
        HSSFCell c = null;
        int row = 0;
        int col = 0;
        columns = new ArrayList<Column>();
        //========================== HEADER ====================================
        r = sheet.getRow(row);       
        while(true) {
            c = r.getCell((short) col);
            if (c == null) {          
                break;
            }
            String value = c.getStringCellValue();
            if (value == null || value.trim().length() == 0) {
                break;
            }
            Column column = new Column(value.trim());
            columns.add(column);
            col++;
        }
        //============================ DATA ====================================
        rows = new ArrayList<List<Cell>>();
        while(true) {
            row++;            
            r = sheet.getRow(row);
            if (r == null) {
                break;
            }
            List rowData = new ArrayList(columns.size());
            boolean isEmptyRow = true;
            for(col = 0; col < columns.size(); col++) {
                c = r.getCell((short) col);
                Object value = null;
                switch(c.getCellType()) {
                    case(HSSFCell.CELL_TYPE_STRING) : value = c.getStringCellValue(); break;
                    case(HSSFCell.CELL_TYPE_NUMERIC) :
                        // value = c.getDateCellValue();
                        value = c.getNumericCellValue(); 
                        break;
                    case(HSSFCell.CELL_TYPE_BLANK) : break;
                    default: // do nothing
                }
                if (value != null && value.toString().length() > 0) {
                    isEmptyRow = false;
                    rowData.add(new Cell(value));
                } else {
                    rowData.add(null);
                }
            }
            if(isEmptyRow) {
                break;
            }
            rows.add(rowData);
        }
    }
    
}
