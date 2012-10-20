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

package gr.abiss.calipso.util;

import gr.abiss.calipso.domain.AbstractItem;
import gr.abiss.calipso.domain.ColumnHeading;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Effort;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.ColumnHeading.Name;
import gr.abiss.calipso.wicket.asset.ItemAssetTypesPanel;

import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.wicket.Component;

import static gr.abiss.calipso.domain.ColumnHeading.Name.ASSET_TYPE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.ASSIGNED_TO;
import static gr.abiss.calipso.domain.ColumnHeading.Name.DETAIL;
import static gr.abiss.calipso.domain.ColumnHeading.Name.DUE_TO;
import static gr.abiss.calipso.domain.ColumnHeading.Name.ID;
import static gr.abiss.calipso.domain.ColumnHeading.Name.LOGGED_BY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.PLANNED_EFFORT;
import static gr.abiss.calipso.domain.ColumnHeading.Name.REPORTED_BY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.SPACE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.STATUS;
import static gr.abiss.calipso.domain.ColumnHeading.Name.SUMMARY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TIME_FROM_CREATION_TO_CLOSE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TIME_FROM_CREATION_TO_FIRST_REPLY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TIME_STAMP;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TOTAL_RESPONSE_TIME;

/**
 * Excel Sheet generation helper
 */
public class ExcelUtils {
    
    private HSSFSheet sheet;
    private List<AbstractItem> items;
    private ItemSearch itemSearch;
    private HSSFCellStyle csBold;
    private HSSFCellStyle csDate;
    private HSSFWorkbook wb;
    private Component callerComponent;
    
    public ExcelUtils(List items, ItemSearch itemSearch, Component callerComponent) {
        this.wb = new HSSFWorkbook();
        this.sheet = wb.createSheet("calipso");
        this.sheet.setDefaultColumnWidth((short) 12);
        
        HSSFFont fBold = wb.createFont();
        fBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        this.csBold = wb.createCellStyle();
        this.csBold.setFont(fBold);
        
        this.csDate = wb.createCellStyle();
        this.csDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
        
        this.items = items;
        this.itemSearch = itemSearch;
        
        this.callerComponent = callerComponent;
        
    }
    
    private HSSFCell getCell(int row, int col) {
        HSSFRow sheetRow = sheet.getRow(row);
        if (sheetRow == null) {
            sheetRow = sheet.createRow(row);
        }
        HSSFCell cell = sheetRow.getCell((short) col);
        if (cell == null) {
            cell = sheetRow.createCell((short) col);
        }
        return cell;
    }
    
    private void setText(int row, int col, String text) {
        HSSFCell cell = getCell(row, col);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(text);
    }
    
    private void setDate(int row, int col, Date date) {
        if (date == null) {
            return;
        }
        HSSFCell cell = getCell(row, col);
        cell.setCellValue(date);
        cell.setCellStyle(csDate);
    }
    
    private void setDouble(int row, int col, Double value) {
        if (value == null) {
            return;
        }
        HSSFCell cell = getCell(row, col);
        cell.setCellValue(value);
    }
    
    private void setHistoryIndex(int row, int col, int value) {
        if(value == 0) {
            return;
        }
        HSSFCell cell = getCell(row, col);
        cell.setCellValue(value);
    }
    
    private void setHeader(int row, int col, String text) {
        HSSFCell cell = getCell(row, col);
        cell.setCellStyle(csBold);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(text);
    }
        
    public HSSFWorkbook exportToExcel() {        
        
        boolean showDetail = itemSearch.isShowDetail();
        boolean showHistory = itemSearch.isShowHistory();
        List<ColumnHeading> columnHeadings = itemSearch.getColumnHeadingsToRender();
        
        int row = 0;
        int col = 0;
        
        // begin header row
        for(ColumnHeading ch : columnHeadings) {
        	if(ch.isField()) {
        		setHeader(row, col++, this.callerComponent.getLocalizer().getString(ch.getLabel(), null));
        	}
        	else{
                setHeader(row, col++, ch.getLabel());
        	}
        }
        
        // iterate over list
        for(AbstractItem item : items) {
            row++; col = 0;
            for(ColumnHeading ch : columnHeadings) {
                if(ch.isField()) {
                    Field field = ch.getField();
                    switch(field.getName().getType()) {
	                    case 3: // Integer, Options
	                    case 33: // Integer, Options
	                        setText(row, col++, this.callerComponent.getLocalizer().getString("CustomAttributeLookupValue."+item.getValue(field.getName())+".name", null));
                        break;
                        case 4: // double
                            setDouble(row, col++, (Double) item.getValue(field.getName()));
                            break;
                        case 6: // date
                            setDate(row, col++, (Date) item.getValue(field.getName()));
                            break;
                        case 20: // user
                        	setText(row, col++, ((User) item.getValue(field.getName())).getFullName());
                            break;
                        case 10: // organization
                        	setText(row, col++, ((Organization) item.getValue(field.getName())).getName());
                            break;
                        case 25: // country
                        	setText(row, col++, ((Country) item.getValue(field.getName())).getName());
                            break;
                        default:
                            setText(row, col++, item.getCustomValue(field).toString());
                    }
                } else {
                    // TODO optimize if-then for performance
                    Name name = ch.getName();
                    if(name == ID) {
                        if (showHistory) {                                                                                                            
                            int index = ((History) item).getIndex();
                            if (index > 0) {
                                setText(row, col++, item.getUniqueRefId() + " (" + index + ")");
                            } else {
                                setText(row, col++, item.getUniqueRefId());
                            }
                        } else {
                            setText(row, col++, item.getUniqueRefId());
                        }
                    } else if(name == SUMMARY) {
                        setText(row, col++, item.getSummary());
                    } else if(name == DETAIL) {
                        if (showHistory) {
                            History h = (History) item;
                            if(h.getIndex() > 0) {
                                setText(row, col++, h.getComment());
                            } else {
                                setText(row, col++, h.getDetail());
                            }
                        } else {
                            setText(row, col++, item.getDetail());
                        }
                    } else if(name == LOGGED_BY) {
                        setText(row, col++, item.getLoggedBy() != null ? item.getLoggedBy().getFullName() : "");
                    } else if(name == REPORTED_BY) {
                    	User reporter = item.getReportedBy();
                    	String loggedBy = item.getLoggedBy() != null ? item.getLoggedBy().getFullName() : "";
                        setText(row, col++, reporter != null ? reporter.getFullName() : loggedBy);
                    } else if(name == ASSET_TYPE){
                    	setText(row, col++, new ItemAssetTypesPanel("", item).toString());
//                    	setText(row, col++, "<Asset Type(s)>");
                    } else if(name == STATUS) {
                        setText(row, col++, item.getStatusValue());
                    } else if(name == ASSIGNED_TO) {
                        setText(row, col++, (item.getAssignedTo() == null ? "" : item.getAssignedTo().getFullName()));
                    } else if(name == TIME_STAMP) {
//                        setDate(row, col++, item.getTimeStamp());
                    	setText(row, col, DateUtils.formatTimeStamp(item.getTimeStamp()));
                    } else if(name == SPACE) {
                        setText(row, col++, item.getSpace().getName());
                    } else if(name == TIME_FROM_CREATION_TO_FIRST_REPLY) {
                    	setText(row, col++, ItemUtils.formatEffort(item.getTimeFromCreationToFirstReply(), this.callerComponent.getLocalizer().getString("item_list.days", null), this.callerComponent.getLocalizer().getString("item_list.hours", null), this.callerComponent.getLocalizer().getString("item_list.minutes", null)));
                    } else if(name == TIME_FROM_CREATION_TO_CLOSE){
                    	setText(row, col++, ItemUtils.formatEffort(item.getTimeFromCreationToClose(), this.callerComponent.getLocalizer().getString("item_list.days", null), this.callerComponent.getLocalizer().getString("item_list.hours", null), this.callerComponent.getLocalizer().getString("item_list.minutes", null)));
                    }else if(name == TOTAL_RESPONSE_TIME){
                    	setText(row, col++, ItemUtils.formatEffort(item.getTotalResponseTime(), this.callerComponent.getLocalizer().getString("item_list.days", null), this.callerComponent.getLocalizer().getString("item_list.hours", null), this.callerComponent.getLocalizer().getString("item_list.minutes", null)));                    	
                    }else if(name == DUE_TO){
                    	// show what's closer
                    	Date dueTo = item.getDueTo();
                    	if(item instanceof Item){
                        	Date stateDueTo = ((Item) item).getStateDueTo();
                        	if(dueTo != null && stateDueTo != null){
	                        	dueTo = dueTo.after(stateDueTo)?stateDueTo:dueTo;
                        	}
                        	else if(stateDueTo != null){
                        		dueTo = stateDueTo;
                        	}
                    	}
                   		setText(row, col++, DateUtils.format(dueTo));
                    }else if(name == PLANNED_EFFORT){
                    	setText(row, col++, new Effort(item.getPlannedEffort()).formatEffort(this.callerComponent.getLocalizer().getString("item_list.days", null), this.callerComponent.getLocalizer().getString("item_list.hours", null), this.callerComponent.getLocalizer().getString("item_list.minutes", null)));                    	
                    }
                    else {
                        throw new RuntimeException("Unexpected name: '" + name + "'");
                    }
                }
            }
        }
        return wb;
    }
    
}
