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

import java.io.File;
import java.io.Serializable;
import java.io.ObjectInputStream.GetField;
import java.util.Date;
import java.util.LinkedHashSet;

import static gr.abiss.calipso.domain.Field.Name.*;
import gr.abiss.calipso.domain.Field.Name;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.util.HumanTime;

import java.util.Set;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import org.springmodules.lucene.index.core.DocumentCreator;

/**
 * Abstract class that serves as base for both Item and History
 * this contains the fields that are common to both and persisted
 */
public abstract class AbstractItem implements Serializable, DocumentCreator {    
	private static final Logger logger = Logger.getLogger(AbstractItem.class);
    private long id = 0;
    private int version;
    private Item parent; // slightly different meaning for Item and History
    private String summary = "";
    private String detail = "";
    private String htmlDetail = "";
    private User loggedBy;
    private User assignedTo;
    private Date timeStamp;
    private Integer plannedEffort;
    private Date dueTo;
    private User reportedBy;

    //Attributes for response time, that be computed "on the flight"
    protected Double totalResponseTime;
    protected Double totalOpenTime;
    protected Double totalIdleTime;
    protected Long timeFromCreationToFirstReply;
    protected Long timeFromCreationToClose;

    //===========================
    private Integer status = 0;
    public abstract Set<Attachment> getAttachments();
    
    public Integer getCusInt13() {
		return cusInt13;
	}
	public void setCusInt13(Integer cusInt13) {
		this.cusInt13 = cusInt13;
	}
	public Integer getCusInt14() {
		return cusInt14;
	}
	public void setCusInt14(Integer cusInt14) {
		this.cusInt14 = cusInt14;
	}
	public Integer getCusInt15() {
		return cusInt15;
	}
	public void setCusInt15(Integer cusInt15) {
		this.cusInt15 = cusInt15;
	}
	public Integer getCusInt16() {
		return cusInt16;
	}
	public void setCusInt16(Integer cusInt16) {
		this.cusInt16 = cusInt16;
	}
	public Integer getCusInt17() {
		return cusInt17;
	}
	public void setCusInt17(Integer cusInt17) {
		this.cusInt17 = cusInt17;
	}
	public Integer getCusInt18() {
		return cusInt18;
	}
	public void setCusInt18(Integer cusInt18) {
		this.cusInt18 = cusInt18;
	}
	public Integer getCusInt19() {
		return cusInt19;
	}
	public void setCusInt19(Integer cusInt19) {
		this.cusInt19 = cusInt19;
	}
	public Integer getCusInt20() {
		return cusInt20;
	}
	public void setCusInt20(Integer cusInt20) {
		this.cusInt20 = cusInt20;
	}
	public Integer getCusInt21() {
		return cusInt21;
	}
	public void setCusInt21(Integer cusInt21) {
		this.cusInt21 = cusInt21;
	}
	public Integer getCusInt22() {
		return cusInt22;
	}
	public void setCusInt22(Integer cusInt22) {
		this.cusInt22 = cusInt22;
	}
	public Integer getCusInt23() {
		return cusInt23;
	}
	public void setCusInt23(Integer cusInt23) {
		this.cusInt23 = cusInt23;
	}
	public Integer getCusInt24() {
		return cusInt24;
	}
	public void setCusInt24(Integer cusInt24) {
		this.cusInt24 = cusInt24;
	}
	public Integer getCusInt25() {
		return cusInt25;
	}
	public void setCusInt25(Integer cusInt25) {
		this.cusInt25 = cusInt25;
	}
	public Integer getCusInt26() {
		return cusInt26;
	}
	public void setCusInt26(Integer cusInt26) {
		this.cusInt26 = cusInt26;
	}
	public Integer getCusInt27() {
		return cusInt27;
	}
	public void setCusInt27(Integer cusInt27) {
		this.cusInt27 = cusInt27;
	}
	public Integer getCusInt28() {
		return cusInt28;
	}
	public void setCusInt28(Integer cusInt28) {
		this.cusInt28 = cusInt28;
	}
	public Integer getCusInt29() {
		return cusInt29;
	}
	public void setCusInt29(Integer cusInt29) {
		this.cusInt29 = cusInt29;
	}
	public Integer getCusInt30() {
		return cusInt30;
	}
	public void setCusInt30(Integer cusInt30) {
		this.cusInt30 = cusInt30;
	}
	public Integer getCusInt31() {
		return cusInt31;
	}
	public void setCusInt31(Integer cusInt31) {
		this.cusInt31 = cusInt31;
	}
	public Integer getCusInt32() {
		return cusInt32;
	}
	public void setCusInt32(Integer cusInt32) {
		this.cusInt32 = cusInt32;
	}
	public Integer getCusInt33() {
		return cusInt33;
	}
	public void setCusInt33(Integer cusInt33) {
		this.cusInt33 = cusInt33;
	}
	public Integer getCusInt34() {
		return cusInt34;
	}
	public void setCusInt34(Integer cusInt34) {
		this.cusInt34 = cusInt34;
	}
	public Integer getCusInt35() {
		return cusInt35;
	}
	public void setCusInt35(Integer cusInt35) {
		this.cusInt35 = cusInt35;
	}
	public Integer getCusInt36() {
		return cusInt36;
	}
	public void setCusInt36(Integer cusInt36) {
		this.cusInt36 = cusInt36;
	}
	public Integer getCusInt37() {
		return cusInt37;
	}
	public void setCusInt37(Integer cusInt37) {
		this.cusInt37 = cusInt37;
	}
	public Integer getCusInt38() {
		return cusInt38;
	}
	public void setCusInt38(Integer cusInt38) {
		this.cusInt38 = cusInt38;
	}
	public Integer getCusInt39() {
		return cusInt39;
	}
	public void setCusInt39(Integer cusInt39) {
		this.cusInt39 = cusInt39;
	}
	public Integer getCusInt40() {
		return cusInt40;
	}
	public void setCusInt40(Integer cusInt40) {
		this.cusInt40 = cusInt40;
	}
	public String getCusStr21() {
		return cusStr21;
	}
	public void setCusStr21(String cusStr21) {
		this.cusStr21 = cusStr21;
	}
	public String getCusStr22() {
		return cusStr22;
	}
	public void setCusStr22(String cusStr22) {
		this.cusStr22 = cusStr22;
	}
	public String getCusStr23() {
		return cusStr23;
	}
	public void setCusStr23(String cusStr23) {
		this.cusStr23 = cusStr23;
	}
	public String getCusStr24() {
		return cusStr24;
	}
	public void setCusStr24(String cusStr24) {
		this.cusStr24 = cusStr24;
	}
	public String getCusStr25() {
		return cusStr25;
	}
	public void setCusStr25(String cusStr25) {
		this.cusStr25 = cusStr25;
	}
	public String getCusStr26() {
		return cusStr26;
	}
	public void setCusStr26(String cusStr26) {
		this.cusStr26 = cusStr26;
	}
	public String getCusStr27() {
		return cusStr27;
	}
	public void setCusStr27(String cusStr27) {
		this.cusStr27 = cusStr27;
	}
	public String getCusStr28() {
		return cusStr28;
	}
	public void setCusStr28(String cusStr28) {
		this.cusStr28 = cusStr28;
	}
	public String getCusStr29() {
		return cusStr29;
	}
	public void setCusStr29(String cusStr29) {
		this.cusStr29 = cusStr29;
	}
	public String getCusStr30() {
		return cusStr30;
	}
	public void setCusStr30(String cusStr30) {
		this.cusStr30 = cusStr30;
	}
	public String getCusStr31() {
		return cusStr31;
	}
	public void setCusStr31(String cusStr31) {
		this.cusStr31 = cusStr31;
	}
	public String getCusStr32() {
		return cusStr32;
	}
	public void setCusStr32(String cusStr32) {
		this.cusStr32 = cusStr32;
	}
	public String getCusStr33() {
		return cusStr33;
	}
	public void setCusStr33(String cusStr33) {
		this.cusStr33 = cusStr33;
	}
	public String getCusStr34() {
		return cusStr34;
	}
	public void setCusStr34(String cusStr34) {
		this.cusStr34 = cusStr34;
	}
	public String getCusStr35() {
		return cusStr35;
	}
	public void setCusStr35(String cusStr35) {
		this.cusStr35 = cusStr35;
	}
	public String getCusStr36() {
		return cusStr36;
	}
	public void setCusStr36(String cusStr36) {
		this.cusStr36 = cusStr36;
	}
	public String getCusStr37() {
		return cusStr37;
	}
	public void setCusStr37(String cusStr37) {
		this.cusStr37 = cusStr37;
	}
	public String getCusStr38() {
		return cusStr38;
	}
	public void setCusStr38(String cusStr38) {
		this.cusStr38 = cusStr38;
	}
	public String getCusStr39() {
		return cusStr39;
	}
	public void setCusStr39(String cusStr39) {
		this.cusStr39 = cusStr39;
	}
	public String getCusStr40() {
		return cusStr40;
	}
	public void setCusStr40(String cusStr40) {
		this.cusStr40 = cusStr40;
	}
	public String getCusStr41() {
		return cusStr41;
	}
	public void setCusStr41(String cusStr41) {
		this.cusStr41 = cusStr41;
	}
	public String getCusStr42() {
		return cusStr42;
	}
	public void setCusStr42(String cusStr42) {
		this.cusStr42 = cusStr42;
	}
	public String getCusStr43() {
		return cusStr43;
	}
	public void setCusStr43(String cusStr43) {
		this.cusStr43 = cusStr43;
	}
	public String getCusStr44() {
		return cusStr44;
	}
	public void setCusStr44(String cusStr44) {
		this.cusStr44 = cusStr44;
	}
	public String getCusStr45() {
		return cusStr45;
	}
	public void setCusStr45(String cusStr45) {
		this.cusStr45 = cusStr45;
	}
	public String getCusStr46() {
		return cusStr46;
	}
	public void setCusStr46(String cusStr46) {
		this.cusStr46 = cusStr46;
	}
	public String getCusStr47() {
		return cusStr47;
	}
	public void setCusStr47(String cusStr47) {
		this.cusStr47 = cusStr47;
	}
	public String getCusStr48() {
		return cusStr48;
	}
	public void setCusStr48(String cusStr48) {
		this.cusStr48 = cusStr48;
	}
	public String getCusStr49() {
		return cusStr49;
	}
	public void setCusStr49(String cusStr49) {
		this.cusStr49 = cusStr49;
	}
	public String getCusStr50() {
		return cusStr50;
	}
	public void setCusStr50(String cusStr50) {
		this.cusStr50 = cusStr50;
	}
	public String getCusStr51() {
		return cusStr51;
	}
	public void setCusStr51(String cusStr51) {
		this.cusStr51 = cusStr51;
	}
	public String getCusStr52() {
		return cusStr52;
	}
	public void setCusStr52(String cusStr52) {
		this.cusStr52 = cusStr52;
	}
	public String getCusStr53() {
		return cusStr53;
	}
	public void setCusStr53(String cusStr53) {
		this.cusStr53 = cusStr53;
	}
	public String getCusStr54() {
		return cusStr54;
	}
	public void setCusStr54(String cusStr54) {
		this.cusStr54 = cusStr54;
	}
	public String getCusStr55() {
		return cusStr55;
	}
	public void setCusStr55(String cusStr55) {
		this.cusStr55 = cusStr55;
	}
	public String getCusStr56() {
		return cusStr56;
	}
	public void setCusStr56(String cusStr56) {
		this.cusStr56 = cusStr56;
	}
	public String getCusStr57() {
		return cusStr57;
	}
	public void setCusStr57(String cusStr57) {
		this.cusStr57 = cusStr57;
	}
	public String getCusStr58() {
		return cusStr58;
	}
	public void setCusStr58(String cusStr58) {
		this.cusStr58 = cusStr58;
	}
	public String getCusStr59() {
		return cusStr59;
	}
	public void setCusStr59(String cusStr59) {
		this.cusStr59 = cusStr59;
	}
	public String getCusStr60() {
		return cusStr60;
	}
	public void setCusStr60(String cusStr60) {
		this.cusStr60 = cusStr60;
	}
	public String getCusStr61() {
		return cusStr61;
	}
	public void setCusStr61(String cusStr61) {
		this.cusStr61 = cusStr61;
	}
	public String getCusStr62() {
		return cusStr62;
	}
	public void setCusStr62(String cusStr62) {
		this.cusStr62 = cusStr62;
	}
	public String getCusStr63() {
		return cusStr63;
	}
	public void setCusStr63(String cusStr63) {
		this.cusStr63 = cusStr63;
	}
	public String getCusStr64() {
		return cusStr64;
	}
	public void setCusStr64(String cusStr64) {
		this.cusStr64 = cusStr64;
	}
	public String getCusStr65() {
		return cusStr65;
	}
	public void setCusStr65(String cusStr65) {
		this.cusStr65 = cusStr65;
	}
	public String getCusStr66() {
		return cusStr66;
	}
	public void setCusStr66(String cusStr66) {
		this.cusStr66 = cusStr66;
	}
	public String getCusStr67() {
		return cusStr67;
	}
	public void setCusStr67(String cusStr67) {
		this.cusStr67 = cusStr67;
	}
	public String getCusStr68() {
		return cusStr68;
	}
	public void setCusStr68(String cusStr68) {
		this.cusStr68 = cusStr68;
	}
	public String getCusStr69() {
		return cusStr69;
	}
	public void setCusStr69(String cusStr69) {
		this.cusStr69 = cusStr69;
	}
	public String getCusStr70() {
		return cusStr70;
	}
	public void setCusStr70(String cusStr70) {
		this.cusStr70 = cusStr70;
	}
	public String getCusStr71() {
		return cusStr71;
	}
	public void setCusStr71(String cusStr71) {
		this.cusStr71 = cusStr71;
	}
	public String getCusStr72() {
		return cusStr72;
	}
	public void setCusStr72(String cusStr72) {
		this.cusStr72 = cusStr72;
	}
	public String getCusStr73() {
		return cusStr73;
	}
	public void setCusStr73(String cusStr73) {
		this.cusStr73 = cusStr73;
	}
	public String getCusStr74() {
		return cusStr74;
	}
	public void setCusStr74(String cusStr74) {
		this.cusStr74 = cusStr74;
	}
	public String getCusStr75() {
		return cusStr75;
	}
	public void setCusStr75(String cusStr75) {
		this.cusStr75 = cusStr75;
	}
	public String getCusStr76() {
		return cusStr76;
	}
	public void setCusStr76(String cusStr76) {
		this.cusStr76 = cusStr76;
	}
	public String getCusStr77() {
		return cusStr77;
	}
	public void setCusStr77(String cusStr77) {
		this.cusStr77 = cusStr77;
	}
	public String getCusStr78() {
		return cusStr78;
	}
	public void setCusStr78(String cusStr78) {
		this.cusStr78 = cusStr78;
	}
	public String getCusStr79() {
		return cusStr79;
	}
	public void setCusStr79(String cusStr79) {
		this.cusStr79 = cusStr79;
	}
	public String getCusStr80() {
		return cusStr80;
	}
	public void setCusStr80(String cusStr80) {
		this.cusStr80 = cusStr80;
	}

	public Date getCusTim11() {
		return cusTim11;
	}
	public void setCusTim11(Date cusTim11) {
		this.cusTim11 = cusTim11;
	}
	public Date getCusTim12() {
		return cusTim12;
	}
	public void setCusTim12(Date cusTim12) {
		this.cusTim12 = cusTim12;
	}
	public Date getCusTim13() {
		return cusTim13;
	}
	public void setCusTim13(Date cusTim13) {
		this.cusTim13 = cusTim13;
	}
	public Date getCusTim14() {
		return cusTim14;
	}
	public void setCusTim14(Date cusTim14) {
		this.cusTim14 = cusTim14;
	}
	public Date getCusTim15() {
		return cusTim15;
	}
	public void setCusTim15(Date cusTim15) {
		this.cusTim15 = cusTim15;
	}
	public Date getCusTim16() {
		return cusTim16;
	}
	public void setCusTim16(Date cusTim16) {
		this.cusTim16 = cusTim16;
	}
	public Date getCusTim17() {
		return cusTim17;
	}
	public void setCusTim17(Date cusTim17) {
		this.cusTim17 = cusTim17;
	}
	public Date getCusTim18() {
		return cusTim18;
	}
	public void setCusTim18(Date cusTim18) {
		this.cusTim18 = cusTim18;
	}
	public Date getCusTim19() {
		return cusTim19;
	}
	public void setCusTim19(Date cusTim19) {
		this.cusTim19 = cusTim19;
	}
	public Date getCusTim20() {
		return cusTim20;
	}
	public void setCusTim20(Date cusTim20) {
		this.cusTim20 = cusTim20;
	}

	private Integer cusInt01;
    private Integer cusInt02;
    private Integer cusInt03;
    private Integer cusInt04;
    private Integer cusInt05;
    private Integer cusInt06;
    private Integer cusInt07;
    private Integer cusInt08;
    private Integer cusInt09;
    private Integer cusInt10;
    private Integer cusInt11;
    private Integer cusInt12;
    private Integer cusInt13;
    private Integer cusInt14;
    private Integer cusInt15;
    private Integer cusInt16;
    private Integer cusInt17;
    private Integer cusInt18;
    private Integer cusInt19;
    private Integer cusInt20;
    private Integer cusInt21;
    private Integer cusInt22;
    private Integer cusInt23;
    private Integer cusInt24;
    private Integer cusInt25;
    private Integer cusInt26;
    private Integer cusInt27;
    private Integer cusInt28;
    private Integer cusInt29;
    private Integer cusInt30;
    private Integer cusInt31;
    private Integer cusInt32;
    private Integer cusInt33;
    private Integer cusInt34;
    private Integer cusInt35;
    private Integer cusInt36;
    private Integer cusInt37;
    private Integer cusInt38;
    private Integer cusInt39;
    private Integer cusInt40;
    
    private Double cusDbl01;
    private Double cusDbl02;
    private Double cusDbl03;
    private Double cusDbl04;
    private Double cusDbl05;
    private Double cusDbl06;
    private Double cusDbl07;
    private Double cusDbl08;
    private Double cusDbl09;
    private Double cusDbl10;
    private Double cusDbl11;
    private Double cusDbl12;
    private Double cusDbl13;
    private Double cusDbl14;
    private Double cusDbl15;
    private Double cusDbl16;
    private Double cusDbl17;
    private Double cusDbl18;
    private Double cusDbl19;
    private Double cusDbl20;
    private Double cusDbl21;
    private Double cusDbl22;
    private Double cusDbl23;
    private Double cusDbl24;
    private Double cusDbl25;
    private Double cusDbl26;
    private Double cusDbl27;
    private Double cusDbl28;
    private Double cusDbl29;
    private Double cusDbl30;
    private Double cusDbl31;
    private Double cusDbl32;
    private Double cusDbl33;
    private Double cusDbl34;
    private Double cusDbl35;
    private Double cusDbl36;
    private Double cusDbl37;
    private Double cusDbl38;
    private Double cusDbl39;
    private Double cusDbl40;
    private Double cusDbl41;
    private Double cusDbl42;
    private Double cusDbl43;
    private Double cusDbl44;
    private Double cusDbl45;
    private Double cusDbl46;
    private Double cusDbl47;
    private Double cusDbl48;
    private Double cusDbl49;
    private Double cusDbl50;
    
    private String cusStr01;
    private String cusStr02;
    private String cusStr03;
    private String cusStr04;
    private String cusStr05;
    private String cusStr06;
    private String cusStr07;
    private String cusStr08;
    private String cusStr09;
    private String cusStr10;
    private String cusStr11;
    private String cusStr12;
    private String cusStr13;
    private String cusStr14;
    private String cusStr15;
    private String cusStr16;
    private String cusStr17;
    private String cusStr18;
    private String cusStr19;
    private String cusStr20;
    private String cusStr21;
    private String cusStr22;
    private String cusStr23;
    private String cusStr24;
    private String cusStr25;
    private String cusStr26;
    private String cusStr27;
    private String cusStr28;
    private String cusStr29;
    private String cusStr30;
    private String cusStr31;
    private String cusStr32;
    private String cusStr33;
    private String cusStr34;
    private String cusStr35;
    private String cusStr36;
    private String cusStr37;
    private String cusStr38;
    private String cusStr39;
    private String cusStr40;
    private String cusStr41;
    private String cusStr42;
    private String cusStr43;
    private String cusStr44;
    private String cusStr45;
    private String cusStr46;
    private String cusStr47;
    private String cusStr48;
    private String cusStr49;
    private String cusStr50;
    private String cusStr51;
    private String cusStr52;
    private String cusStr53;
    private String cusStr54;
    private String cusStr55;
    private String cusStr56;
    private String cusStr57;
    private String cusStr58;
    private String cusStr59;
    private String cusStr60;
    private String cusStr61;
    private String cusStr62;
    private String cusStr63;
    private String cusStr64;
    private String cusStr65;
    private String cusStr66;
    private String cusStr67;
    private String cusStr68;
    private String cusStr69;
    private String cusStr70;
    private String cusStr71;
    private String cusStr72;
    private String cusStr73;
    private String cusStr74;
    private String cusStr75;
    private String cusStr76;
    private String cusStr77;
    private String cusStr78;
    private String cusStr79;
    private String cusStr80;
    
    private Country cusCountry1;
    private Country cusCountry2;
    private Date cusTim01;
    private Date cusTim02;
    private Date cusTim03;
    private Date cusTim04;
    private Date cusTim05;
    private Date cusTim06;
    private Date cusTim07;
    private Date cusTim08;
    private Date cusTim09;
    private Date cusTim10;
    private Date cusTim11;
    private Date cusTim12;
    private Date cusTim13;
    private Date cusTim14;
    private Date cusTim15;
    private Date cusTim16;
    private Date cusTim17;
    private Date cusTim18;
    private Date cusTim19;
    private Date cusTim20;
    private File file1;
    private File file2;
    private File file3;
    private File file4;
    private File file5;
    private User user;
    private Organization organization;
    
    //Space in which a items has been moved.  
    //It corresponds in a selected value from field "ASSIGNABLE_SPACES"
    private Space assignableSpaces;
    
    // probably belong to Item not AbstractItem, but convenient for item_view_form binding
    private Set<ItemUser> itemUsers = new LinkedHashSet<ItemUser>();;
    private Set<ItemItem> relatedItems;
    private Set<ItemItem> relatingItems;
    private Set<ItemTag> itemTags;
    
    private Set<Asset> assets;
    

    /**
     * Used only for wicket model
     */
    private String attachment;
    
    // mvc form binding convenience not really domain, TODO refactor
    private boolean sendNotifications = false;
    
    public Object getValue(Field.Name fieldName) {
    	Object valueObject = null;
    	if(fieldName.getType() != 200){
	    	try {
				valueObject = PropertyUtils.getProperty(this, fieldName.getText());
	//			switch(fieldName.getType()) {
	//		        case 3: return (Integer) valueObject;
	//		        case 4: return (Double) valueObject;
	//		        case 5: return (String) valueObject;
	//		        case 6: return (Date) valueObject;
	//		        case 10: return (Organization) valueObject;
	//		        case 11: return (File) valueObject;
	//		        case 20: return (User) valueObject;
	//		        case 25: return (Country) valueObject;
	//		        case 100: return (Space) valueObject;
	//			}
			} catch (Exception e) {
				throw new RuntimeException("Could not obtain value for custom attribute "+fieldName, e);
			}
    	}
    	return valueObject;
    }

    public void setValue(Field.Name fieldName, Object value) {
    	if(fieldName.getType() != 200){
	    	try {
				PropertyUtils.setProperty(this, fieldName.getText(), value);
			} catch (Exception e) {
				throw new RuntimeException("Could not set value for custom attribute "+fieldName, e);
			}
    	}
    }    
    
    
    // must override, History behaves differently from Item
    public abstract Space getSpace();
    public abstract String getRefId();
    
    // Due to the fact of the possibility of space change of an item, the Item reference id should be change.
    // The "Unique Reference Id" now is constituted of three parts, separeted by "-".
    // The first part corresponds to the space short key 
    // The second part corresponds to the database wide unique item id 
    // Finaly the third part corresponds to the space sequence number
    // This form of item reference id is necessary, because if an item has been moved to another space takes o different sequence number
    // P.e. if an has as reference id : "SP1-101" after moving takes as reference id: "SP2-232".
    // With the new reference id form, the "SP1-200345-101" will be become "SP2-200345-232" where the initial information doesn't goes lost.
    public abstract String getUniqueRefId();
    
    public Serializable getCustomValue(Field field) {

    	Field.Name fieldName = field.getName();
    	return getCustomValue(fieldName);
    }
    

    public Serializable getCustomValue(Name fieldName) {
    // using accessor for space, getSpace() is overridden in subclass History
	Serializable customValue = null;
    if (fieldName.isDropDownType()) {   
    	customValue =  (Serializable) getValue(fieldName);//getSpace().getMetadata().getCustomValue(fieldName, (Integer) getValue(fieldName));
    }
    else if(fieldName.isOrganization()){ // is organization
    	// return organization'name
    	customValue =  (Organization) getValue(fieldName);
    	
    	
    }
    else if(fieldName.isFile()){ // is file
    	// return organization'name
    	String fieldLabel = getSpace().getMetadata().getField(fieldName).getLabel();
    	customValue =  fieldLabel;
    }
    else if(fieldName.isUser()){ // is user
    	// return organization'name
    	customValue =   (User) getValue(fieldName);
    	
    	
    }
    else if(fieldName.isCountry()){ // is country
    	Country country = (Country) getValue(fieldName);
    	if(country != null){
    		customValue =  country.getId();
    	}
    	else{
    		customValue =  "";
    	}
    }
    //Return the space name 
    else if (fieldName.equals(Field.Name.ASSIGNABLE_SPACES)){
    	Space space = (Space)getValue(fieldName); 
    	if (space!=null){
    		customValue =  space.getName();
    	}
    	customValue =  "";
    }
    else {
        Object o = getValue(fieldName);
        if (o == null) {
        	customValue =  "";
        }
        if (o instanceof Date) {
        	customValue =  DateUtils.format((Date) o); 
        }
        customValue =  o!= null ? o.toString(): "";
    }
    //logger.info("getCustomValue("+fieldName+": "+customValue);
    return customValue;
}

    public String getStatusValue() {
        // using accessor for space, getSpace() is overridden in subclass History
        return getSpace().getMetadata().getStatusValue(status);
    }    
    
    //===================================================
    
    public Integer getStatus() {
        return status;
    }

    public Integer getCusInt01() {
        return cusInt01;
    }

    public Integer getCusInt02() {
        return cusInt02;
    }

    public Integer getCusInt03() {
        return cusInt03;
    }

    public Integer getCusInt04() {
        return cusInt04;
    }

    public Integer getCusInt05() {
        return cusInt05;
    }

    public Integer getCusInt06() {
        return cusInt06;
    }

    public Integer getCusInt07() {
        return cusInt07;
    }

    public Integer getCusInt08() {
        return cusInt08;
    }

    public Integer getCusInt09() {
        return cusInt09;
    }

    public Integer getCusInt10() {
        return cusInt10;
    }

    public Integer getCusInt11() {
        return cusInt11;
    }

    public Integer getCusInt12() {
        return cusInt12;
    }

    public Double getCusDbl01() {
        return cusDbl01;
    }

    public Double getCusDbl02() {
        return cusDbl02;
    }

    public Double getCusDbl03() {
        return cusDbl03;
    }

    /**
	 * @return the cusDbl04
	 */
	public Double getCusDbl04() {
		return cusDbl04;
	}
	/**
	 * @param cusDbl04 the cusDbl04 to set
	 */
	public void setCusDbl04(Double cusDbl04) {
		this.cusDbl04 = cusDbl04;
	}
	/**
	 * @return the cusDbl05
	 */
	public Double getCusDbl05() {
		return cusDbl05;
	}
	/**
	 * @param cusDbl05 the cusDbl05 to set
	 */
	public void setCusDbl05(Double cusDbl05) {
		this.cusDbl05 = cusDbl05;
	}
	/**
	 * @return the cusDbl06
	 */
	public Double getCusDbl06() {
		return cusDbl06;
	}
	/**
	 * @param cusDbl06 the cusDbl06 to set
	 */
	public void setCusDbl06(Double cusDbl06) {
		this.cusDbl06 = cusDbl06;
	}
	/**
	 * @return the cusDbl07
	 */
	public Double getCusDbl07() {
		return cusDbl07;
	}
	/**
	 * @param cusDbl07 the cusDbl07 to set
	 */
	public void setCusDbl07(Double cusDbl07) {
		this.cusDbl07 = cusDbl07;
	}
	/**
	 * @return the cusDbl08
	 */
	public Double getCusDbl08() {
		return cusDbl08;
	}
	/**
	 * @param cusDbl08 the cusDbl08 to set
	 */
	public void setCusDbl08(Double cusDbl08) {
		this.cusDbl08 = cusDbl08;
	}
	/**
	 * @return the cusDbl09
	 */
	public Double getCusDbl09() {
		return cusDbl09;
	}
	/**
	 * @param cusDbl09 the cusDbl09 to set
	 */
	public void setCusDbl09(Double cusDbl09) {
		this.cusDbl09 = cusDbl09;
	}
	/**
	 * @return the cusDbl10
	 */
	public Double getCusDbl10() {
		return cusDbl10;
	}
	/**
	 * @param cusDbl10 the cusDbl10 to set
	 */
	public void setCusDbl10(Double cusDbl10) {
		this.cusDbl10 = cusDbl10;
	}
	/**
	 * @return the cusDbl11
	 */
	public Double getCusDbl11() {
		return cusDbl11;
	}
	/**
	 * @param cusDbl11 the cusDbl11 to set
	 */
	public void setCusDbl11(Double cusDbl11) {
		this.cusDbl11 = cusDbl11;
	}
	/**
	 * @return the cusDbl12
	 */
	public Double getCusDbl12() {
		return cusDbl12;
	}
	/**
	 * @param cusDbl12 the cusDbl12 to set
	 */
	public void setCusDbl12(Double cusDbl12) {
		this.cusDbl12 = cusDbl12;
	}
	/**
	 * @return the cusDbl13
	 */
	public Double getCusDbl13() {
		return cusDbl13;
	}
	/**
	 * @param cusDbl13 the cusDbl13 to set
	 */
	public void setCusDbl13(Double cusDbl13) {
		this.cusDbl13 = cusDbl13;
	}
	/**
	 * @return the cusDbl14
	 */
	public Double getCusDbl14() {
		return cusDbl14;
	}
	/**
	 * @param cusDbl14 the cusDbl14 to set
	 */
	public void setCusDbl14(Double cusDbl14) {
		this.cusDbl14 = cusDbl14;
	}
	/**
	 * @return the cusDbl15
	 */
	public Double getCusDbl15() {
		return cusDbl15;
	}
	/**
	 * @param cusDbl15 the cusDbl15 to set
	 */
	public void setCusDbl15(Double cusDbl15) {
		this.cusDbl15 = cusDbl15;
	}
	/**
	 * @return the cusDbl16
	 */
	public Double getCusDbl16() {
		return cusDbl16;
	}
	/**
	 * @param cusDbl16 the cusDbl16 to set
	 */
	public void setCusDbl16(Double cusDbl16) {
		this.cusDbl16 = cusDbl16;
	}
	/**
	 * @return the cusDbl17
	 */
	public Double getCusDbl17() {
		return cusDbl17;
	}
	/**
	 * @param cusDbl17 the cusDbl17 to set
	 */
	public void setCusDbl17(Double cusDbl17) {
		this.cusDbl17 = cusDbl17;
	}
	/**
	 * @return the cusDbl18
	 */
	public Double getCusDbl18() {
		return cusDbl18;
	}
	/**
	 * @param cusDbl18 the cusDbl18 to set
	 */
	public void setCusDbl18(Double cusDbl18) {
		this.cusDbl18 = cusDbl18;
	}
	/**
	 * @return the cusDbl19
	 */
	public Double getCusDbl19() {
		return cusDbl19;
	}
	/**
	 * @param cusDbl19 the cusDbl19 to set
	 */
	public void setCusDbl19(Double cusDbl19) {
		this.cusDbl19 = cusDbl19;
	}
	/**
	 * @return the cusDbl20
	 */
	public Double getCusDbl20() {
		return cusDbl20;
	}
	/**
	 * @param cusDbl20 the cusDbl20 to set
	 */
	public void setCusDbl20(Double cusDbl20) {
		this.cusDbl20 = cusDbl20;
	}
	/**
	 * @return the cusDbl21
	 */
	public Double getCusDbl21() {
		return cusDbl21;
	}
	/**
	 * @param cusDbl21 the cusDbl21 to set
	 */
	public void setCusDbl21(Double cusDbl21) {
		this.cusDbl21 = cusDbl21;
	}
	/**
	 * @return the cusDbl22
	 */
	public Double getCusDbl22() {
		return cusDbl22;
	}
	/**
	 * @param cusDbl22 the cusDbl22 to set
	 */
	public void setCusDbl22(Double cusDbl22) {
		this.cusDbl22 = cusDbl22;
	}
	/**
	 * @return the cusDbl23
	 */
	public Double getCusDbl23() {
		return cusDbl23;
	}
	/**
	 * @param cusDbl23 the cusDbl23 to set
	 */
	public void setCusDbl23(Double cusDbl23) {
		this.cusDbl23 = cusDbl23;
	}
	/**
	 * @return the cusDbl24
	 */
	public Double getCusDbl24() {
		return cusDbl24;
	}
	/**
	 * @param cusDbl24 the cusDbl24 to set
	 */
	public void setCusDbl24(Double cusDbl24) {
		this.cusDbl24 = cusDbl24;
	}
	/**
	 * @return the cusDbl25
	 */
	public Double getCusDbl25() {
		return cusDbl25;
	}
	/**
	 * @param cusDbl25 the cusDbl25 to set
	 */
	public void setCusDbl25(Double cusDbl25) {
		this.cusDbl25 = cusDbl25;
	}
	/**
	 * @return the cusDbl26
	 */
	public Double getCusDbl26() {
		return cusDbl26;
	}
	/**
	 * @param cusDbl26 the cusDbl26 to set
	 */
	public void setCusDbl26(Double cusDbl26) {
		this.cusDbl26 = cusDbl26;
	}
	/**
	 * @return the cusDbl27
	 */
	public Double getCusDbl27() {
		return cusDbl27;
	}
	/**
	 * @param cusDbl27 the cusDbl27 to set
	 */
	public void setCusDbl27(Double cusDbl27) {
		this.cusDbl27 = cusDbl27;
	}
	/**
	 * @return the cusDbl28
	 */
	public Double getCusDbl28() {
		return cusDbl28;
	}
	/**
	 * @param cusDbl28 the cusDbl28 to set
	 */
	public void setCusDbl28(Double cusDbl28) {
		this.cusDbl28 = cusDbl28;
	}
	/**
	 * @return the cusDbl29
	 */
	public Double getCusDbl29() {
		return cusDbl29;
	}
	/**
	 * @param cusDbl29 the cusDbl29 to set
	 */
	public void setCusDbl29(Double cusDbl29) {
		this.cusDbl29 = cusDbl29;
	}
	/**
	 * @return the cusDbl30
	 */
	public Double getCusDbl30() {
		return cusDbl30;
	}
	/**
	 * @param cusDbl30 the cusDbl30 to set
	 */
	public void setCusDbl30(Double cusDbl30) {
		this.cusDbl30 = cusDbl30;
	}
	/**
	 * @return the cusDbl31
	 */
	public Double getCusDbl31() {
		return cusDbl31;
	}
	/**
	 * @param cusDbl31 the cusDbl31 to set
	 */
	public void setCusDbl31(Double cusDbl31) {
		this.cusDbl31 = cusDbl31;
	}
	/**
	 * @return the cusDbl32
	 */
	public Double getCusDbl32() {
		return cusDbl32;
	}
	/**
	 * @param cusDbl32 the cusDbl32 to set
	 */
	public void setCusDbl32(Double cusDbl32) {
		this.cusDbl32 = cusDbl32;
	}
	/**
	 * @return the cusDbl33
	 */
	public Double getCusDbl33() {
		return cusDbl33;
	}
	/**
	 * @param cusDbl33 the cusDbl33 to set
	 */
	public void setCusDbl33(Double cusDbl33) {
		this.cusDbl33 = cusDbl33;
	}
	/**
	 * @return the cusDbl34
	 */
	public Double getCusDbl34() {
		return cusDbl34;
	}
	/**
	 * @param cusDbl34 the cusDbl34 to set
	 */
	public void setCusDbl34(Double cusDbl34) {
		this.cusDbl34 = cusDbl34;
	}
	/**
	 * @return the cusDbl35
	 */
	public Double getCusDbl35() {
		return cusDbl35;
	}
	/**
	 * @param cusDbl35 the cusDbl35 to set
	 */
	public void setCusDbl35(Double cusDbl35) {
		this.cusDbl35 = cusDbl35;
	}
	/**
	 * @return the cusDbl36
	 */
	public Double getCusDbl36() {
		return cusDbl36;
	}
	/**
	 * @param cusDbl36 the cusDbl36 to set
	 */
	public void setCusDbl36(Double cusDbl36) {
		this.cusDbl36 = cusDbl36;
	}
	/**
	 * @return the cusDbl37
	 */
	public Double getCusDbl37() {
		return cusDbl37;
	}
	/**
	 * @param cusDbl37 the cusDbl37 to set
	 */
	public void setCusDbl37(Double cusDbl37) {
		this.cusDbl37 = cusDbl37;
	}
	/**
	 * @return the cusDbl38
	 */
	public Double getCusDbl38() {
		return cusDbl38;
	}
	/**
	 * @param cusDbl38 the cusDbl38 to set
	 */
	public void setCusDbl38(Double cusDbl38) {
		this.cusDbl38 = cusDbl38;
	}
	/**
	 * @return the cusDbl39
	 */
	public Double getCusDbl39() {
		return cusDbl39;
	}
	/**
	 * @param cusDbl39 the cusDbl39 to set
	 */
	public void setCusDbl39(Double cusDbl39) {
		this.cusDbl39 = cusDbl39;
	}
	/**
	 * @return the cusDbl40
	 */
	public Double getCusDbl40() {
		return cusDbl40;
	}
	/**
	 * @param cusDbl40 the cusDbl40 to set
	 */
	public void setCusDbl40(Double cusDbl40) {
		this.cusDbl40 = cusDbl40;
	}
	/**
	 * @return the cusDbl41
	 */
	public Double getCusDbl41() {
		return cusDbl41;
	}
	/**
	 * @param cusDbl41 the cusDbl41 to set
	 */
	public void setCusDbl41(Double cusDbl41) {
		this.cusDbl41 = cusDbl41;
	}
	/**
	 * @return the cusDbl42
	 */
	public Double getCusDbl42() {
		return cusDbl42;
	}
	/**
	 * @param cusDbl42 the cusDbl42 to set
	 */
	public void setCusDbl42(Double cusDbl42) {
		this.cusDbl42 = cusDbl42;
	}
	/**
	 * @return the cusDbl43
	 */
	public Double getCusDbl43() {
		return cusDbl43;
	}
	/**
	 * @param cusDbl43 the cusDbl43 to set
	 */
	public void setCusDbl43(Double cusDbl43) {
		this.cusDbl43 = cusDbl43;
	}
	/**
	 * @return the cusDbl44
	 */
	public Double getCusDbl44() {
		return cusDbl44;
	}
	/**
	 * @param cusDbl44 the cusDbl44 to set
	 */
	public void setCusDbl44(Double cusDbl44) {
		this.cusDbl44 = cusDbl44;
	}
	/**
	 * @return the cusDbl45
	 */
	public Double getCusDbl45() {
		return cusDbl45;
	}
	/**
	 * @param cusDbl45 the cusDbl45 to set
	 */
	public void setCusDbl45(Double cusDbl45) {
		this.cusDbl45 = cusDbl45;
	}
	/**
	 * @return the cusDbl46
	 */
	public Double getCusDbl46() {
		return cusDbl46;
	}
	/**
	 * @param cusDbl46 the cusDbl46 to set
	 */
	public void setCusDbl46(Double cusDbl46) {
		this.cusDbl46 = cusDbl46;
	}
	/**
	 * @return the cusDbl47
	 */
	public Double getCusDbl47() {
		return cusDbl47;
	}
	/**
	 * @param cusDbl47 the cusDbl47 to set
	 */
	public void setCusDbl47(Double cusDbl47) {
		this.cusDbl47 = cusDbl47;
	}
	/**
	 * @return the cusDbl48
	 */
	public Double getCusDbl48() {
		return cusDbl48;
	}
	/**
	 * @param cusDbl48 the cusDbl48 to set
	 */
	public void setCusDbl48(Double cusDbl48) {
		this.cusDbl48 = cusDbl48;
	}
	/**
	 * @return the cusDbl49
	 */
	public Double getCusDbl49() {
		return cusDbl49;
	}
	/**
	 * @param cusDbl49 the cusDbl49 to set
	 */
	public void setCusDbl49(Double cusDbl49) {
		this.cusDbl49 = cusDbl49;
	}
	/**
	 * @return the cusDbl50
	 */
	public Double getCusDbl50() {
		return cusDbl50;
	}
	/**
	 * @param cusDbl50 the cusDbl50 to set
	 */
	public void setCusDbl50(Double cusDbl50) {
		this.cusDbl50 = cusDbl50;
	}
	public String getCusStr01() {
        return cusStr01;
    }

    public String getCusStr02() {
        return cusStr02;
    }

    public String getCusStr03() {
        return cusStr03;
    }

    public String getCusStr04() {
        return cusStr04;
    }

    public String getCusStr05() {
        return cusStr05;
    }

    public Date getCusTim01() {
        return cusTim01;
    }

    public Date getCusTim02() {
        return cusTim02;
    }

    public Date getCusTim03() {
        return cusTim03;
    }

	/**
	 * @return the cusTim04
	 */
	public Date getCusTim04() {
		return cusTim04;
	}
	/**
	 * @param cusTim04 the cusTim04 to set
	 */
	public void setCusTim04(Date cusTim04) {
		this.cusTim04 = cusTim04;
	}
	/**
	 * @return the cusTim05
	 */
	public Date getCusTim05() {
		return cusTim05;
	}
	/**
	 * @param cusTim05 the cusTim05 to set
	 */
	public void setCusTim05(Date cusTim05) {
		this.cusTim05 = cusTim05;
	}
	/**
	 * @return the cusTim06
	 */
	public Date getCusTim06() {
		return cusTim06;
	}
	/**
	 * @param cusTim06 the cusTim06 to set
	 */
	public void setCusTim06(Date cusTim06) {
		this.cusTim06 = cusTim06;
	}
	/**
	 * @return the cusTim07
	 */
	public Date getCusTim07() {
		return cusTim07;
	}
	/**
	 * @param cusTim07 the cusTim07 to set
	 */
	public void setCusTim07(Date cusTim07) {
		this.cusTim07 = cusTim07;
	}
	/**
	 * @return the cusTim08
	 */
	public Date getCusTim08() {
		return cusTim08;
	}
	/**
	 * @param cusTim08 the cusTim08 to set
	 */
	public void setCusTim08(Date cusTim08) {
		this.cusTim08 = cusTim08;
	}
	/**
	 * @return the cusTim09
	 */
	public Date getCusTim09() {
		return cusTim09;
	}
	/**
	 * @param cusTim09 the cusTim09 to set
	 */
	public void setCusTim09(Date cusTim09) {
		this.cusTim09 = cusTim09;
	}
	/**
	 * @return the cusTim10
	 */
	public Date getCusTim10() {
		return cusTim10;
	}
	/**
	 * @param cusTim10 the cusTim10 to set
	 */
	public void setCusTim10(Date cusTim10) {
		this.cusTim10 = cusTim10;
	}
	public Space getAssignableSpaces() {
		return assignableSpaces;
	}
    
    //===============================================================

    /**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the organization
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCusInt01(Integer cusInt01) {
        this.cusInt01 = cusInt01;
    }

    public void setCusInt02(Integer cusInt02) {
        this.cusInt02 = cusInt02;
    }

    public void setCusInt03(Integer cusInt03) {
        this.cusInt03 = cusInt03;
    }

    public void setCusInt04(Integer cusInt04) {
        this.cusInt04 = cusInt04;
    }

    public void setCusInt05(Integer cusInt05) {
        this.cusInt05 = cusInt05;
    }

    public void setCusInt06(Integer cusInt06) {
        this.cusInt06 = cusInt06;
    }

    public void setCusInt07(Integer cusInt07) {
        this.cusInt07 = cusInt07;
    }

    public void setCusInt08(Integer cusInt08) {
        this.cusInt08 = cusInt08;
    }

    public void setCusInt09(Integer cusInt09) {
        this.cusInt09 = cusInt09;
    }

    public void setCusInt10(Integer cusInt10) {
        this.cusInt10 = cusInt10;
    }

    public void setCusInt11(Integer severity) {
        this.cusInt11 = severity;
    }

    public void setCusInt12(Integer priority) {
        this.cusInt12 = priority;
    }

    public void setCusDbl01(Double cusDbl01) {
        this.cusDbl01 = cusDbl01;
    }

    public void setCusDbl02(Double cusDbl02) {
        this.cusDbl02 = cusDbl02;
    }

    public void setCusDbl03(Double cusDbl03) {
        this.cusDbl03 = cusDbl03;
    }

    public void setCusStr01(String cusStr01) {
        this.cusStr01 = cusStr01;
    }

    public void setCusStr02(String cusStr02) {
        this.cusStr02 = cusStr02;
    }

    public void setCusStr03(String cusStr03) {
        this.cusStr03 = cusStr03;
    }

    public void setCusStr04(String cusStr04) {
        this.cusStr04 = cusStr04;
    }

    public void setCusStr05(String cusStr05) {
        this.cusStr05 = cusStr05;
    }

    public void setCusTim01(Date cusTim01) {
        this.cusTim01 = cusTim01;
    }

    public void setCusTim02(Date cusTim02) {
        this.cusTim02 = cusTim02;
    }

    public void setCusTim03(Date cusTim03) {
        this.cusTim03 = cusTim03;
    }
    
	public void setAssignableSpaces(Space toSpace) {
		this.assignableSpaces = toSpace;
	}

    //=======================================================================

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }    

    public Item getParent() {
        return parent;
    }

    public void setParent(Item parent) {
        this.parent = parent;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    public String getHtmlDetail() {
        return this.htmlDetail;
    }

    public void setHtmlDetail(String htmlDetail) {
        this.htmlDetail = htmlDetail;
    }

    public User getLoggedBy() {
        return loggedBy;
    }

    public void setLoggedBy(User loggedBy) {
        this.loggedBy = loggedBy;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getPlannedEffort() {
        return plannedEffort;
    }

    public void setPlannedEffort(Integer plannedEffort) {
        this.plannedEffort = plannedEffort;
    }    
    
    public boolean isSendNotifications() {
        return sendNotifications;
    }

    public void setSendNotifications(boolean sendNotifications) {
        this.sendNotifications = sendNotifications;
    }    
    
    public Set<ItemUser> getItemUsers() {
        return itemUsers;
    }

    public void setItemUsers(Set<ItemUser> itemUsers) {
        this.itemUsers = itemUsers;
    }  
    
    public Set<ItemItem> getRelatedItems() {
        return relatedItems;
    }

    public void setRelatedItems(Set<ItemItem> relatedItems) {
        this.relatedItems = relatedItems;
    } 
    
    public Set<ItemItem> getRelatingItems() {
        return relatingItems;
    }

    public void setRelatingItems(Set<ItemItem> relatingItems) {
        this.relatingItems = relatingItems;
    }    
    
    public Set<ItemTag> getItemTags() {
        return itemTags;
    }

    public void setItemTags(Set<ItemTag> itemTags) {
        this.itemTags = itemTags;
    }    
    
    
    public Set<Asset> getAssets() {
		return assets;
	}

	public void setAssets(Set<Asset> assets) {
		this.assets = assets;
	}

    public Date getDueTo() {
		return this.dueTo;
	}
    
    public String getDueToUserFriendly(Date now) {
		return HumanTime.approximately(now, this.dueTo);
	}

    public void setDueTo(Date dueTo) {
		this.dueTo = dueTo;
	}
	
    public User getReportedBy() {
		return this.reportedBy;
	}
    
    public void setReportedBy(User reportedBy) {
		this.reportedBy = reportedBy;
	}
    
	/**
	 * @return the cusStr06
	 */
	public String getCusStr06() {
		return cusStr06;
	}
	/**
	 * @param cusStr06 the cusStr06 to set
	 */
	public void setCusStr06(String cusStr06) {
		this.cusStr06 = cusStr06;
	}
	/**
	 * @return the cusStr07
	 */
	public String getCusStr07() {
		return cusStr07;
	}
	/**
	 * @param cusStr07 the cusStr07 to set
	 */
	public void setCusStr07(String cusStr07) {
		this.cusStr07 = cusStr07;
	}
	/**
	 * @return the cusStr08
	 */
	public String getCusStr08() {
		return cusStr08;
	}
	/**
	 * @param cusStr08 the cusStr08 to set
	 */
	public void setCusStr08(String cusStr08) {
		this.cusStr08 = cusStr08;
	}
	/**
	 * @return the cusStr09
	 */
	public String getCusStr09() {
		return cusStr09;
	}
	/**
	 * @param cusStr09 the cusStr09 to set
	 */
	public void setCusStr09(String cusStr09) {
		this.cusStr09 = cusStr09;
	}
	/**
	 * @return the cusStr10
	 */
	public String getCusStr10() {
		return cusStr10;
	}
	/**
	 * @param cusStr10 the cusStr10 to set
	 */
	public void setCusStr10(String cusStr10) {
		this.cusStr10 = cusStr10;
	}
	/**
	 * @return the cusStr11
	 */
	public String getCusStr11() {
		return cusStr11;
	}
	/**
	 * @param cusStr11 the cusStr11 to set
	 */
	public void setCusStr11(String cusStr11) {
		this.cusStr11 = cusStr11;
	}
	/**
	 * @return the cusStr12
	 */
	public String getCusStr12() {
		return cusStr12;
	}
	/**
	 * @param cusStr12 the cusStr12 to set
	 */
	public void setCusStr12(String cusStr12) {
		this.cusStr12 = cusStr12;
	}
	/**
	 * @return the cusStr13
	 */
	public String getCusStr13() {
		return cusStr13;
	}
	/**
	 * @param cusStr13 the cusStr13 to set
	 */
	public void setCusStr13(String cusStr13) {
		this.cusStr13 = cusStr13;
	}
	/**
	 * @return the cusStr14
	 */
	public String getCusStr14() {
		return cusStr14;
	}
	/**
	 * @param cusStr14 the cusStr14 to set
	 */
	public void setCusStr14(String cusStr14) {
		this.cusStr14 = cusStr14;
	}
	/**
	 * @return the cusStr15
	 */
	public String getCusStr15() {
		return cusStr15;
	}
	/**
	 * @param cusStr15 the cusStr15 to set
	 */
	public void setCusStr15(String cusStr15) {
		this.cusStr15 = cusStr15;
	}
	/**
	 * @return the cusStr16
	 */
	public String getCusStr16() {
		return cusStr16;
	}
	/**
	 * @param cusStr16 the cusStr16 to set
	 */
	public void setCusStr16(String cusStr16) {
		this.cusStr16 = cusStr16;
	}
	/**
	 * @return the cusStr17
	 */
	public String getCusStr17() {
		return cusStr17;
	}
	/**
	 * @param cusStr17 the cusStr17 to set
	 */
	public void setCusStr17(String cusStr17) {
		this.cusStr17 = cusStr17;
	}
	/**
	 * @return the cusStr18
	 */
	public String getCusStr18() {
		return cusStr18;
	}
	/**
	 * @param cusStr18 the cusStr18 to set
	 */
	public void setCusStr18(String cusStr18) {
		this.cusStr18 = cusStr18;
	}
	/**
        else {
	 * @return the cusStr19
	 */
	public String getCusStr19() {
		return cusStr19;
	}
	/**
	 * @param cusStr19 the cusStr19 to set
	 */
	public void setCusStr19(String cusStr19) {
		this.cusStr19 = cusStr19;
	}
	/**
	 * @return the cusStr20
	 */
	public String getCusStr20() {
		return cusStr20;
	}
	/**
	 * @param cusStr20 the cusStr20 to set
	 */
	public void setCusStr20(String cusStr20) {
		this.cusStr20 = cusStr20;
	}
	/**
	 * @return the cusCountry1
	 */
	public Country getCusCountry1() {
		return cusCountry1;
	}
	/**
	 * @param cusCountry1 the cusCountry1 to set
	 */
	public void setCusCountry1(Country cusCountry1) {
		this.cusCountry1 = cusCountry1;
	}
	/**
	 * @return the cusCountry2
	 */
	public Country getCusCountry2() {
		return cusCountry2;
	}
	/**
	 * @param cusCountry2 the cusCountry2 to set
	 */
	public void setCusCountry2(Country cusCountry2) {
		this.cusCountry2 = cusCountry2;
	}
	

    public String getAtachement() {
		return attachment;
	}

	public void setAtachement(String atachement) {
		this.attachment = atachement;
	}
	
	@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id [").append(id);
        sb.append("]; parent [").append(parent == null ? "" : parent.getId());
        sb.append("]; summary [").append(summary);
        sb.append("]; detail [").append(detail);
        sb.append("]; loggedBy [").append(loggedBy);
        sb.append("]; status [").append(status);
        sb.append("]; assignedTo [").append(assignedTo);
        sb.append("]; timeStamp [").append(timeStamp);        
        sb.append("]; severity [").append(cusInt11);
        sb.append("]; priority [").append(cusInt12);
        sb.append("]; cusInt01 [").append(cusInt01);
        sb.append("]; cusInt02 [").append(cusInt02);
        sb.append("]; cusInt03 [").append(cusInt03);
        sb.append("]; cusInt04 [").append(cusInt04);
        sb.append("]; cusInt05 [").append(cusInt05);
        sb.append("]; cusInt06 [").append(cusInt06);
        sb.append("]; cusInt07 [").append(cusInt07);
        sb.append("]; cusInt08 [").append(cusInt08);
        sb.append("]; cusInt09 [").append(cusInt09);
        sb.append("]; cusInt10 [").append(cusInt10);
        sb.append("]; cusDbl01 [").append(cusDbl01);
        sb.append("]; cusDbl02 [").append(cusDbl02);
        sb.append("]; cusDbl03 [").append(cusDbl03);
        sb.append("]; cusStr01 [").append(cusStr01);
        sb.append("]; cusStr02 [").append(cusStr02);
        sb.append("]; cusStr03 [").append(cusStr03);
        sb.append("]; cusStr04 [").append(cusStr04);
        sb.append("]; cusStr05 [").append(cusStr05);
        sb.append("]; cusTim01 [").append(cusTim01);
        sb.append("]; cusTim02 [").append(cusTim02);
        sb.append("]; cusTim03 [").append(cusTim03);
        sb.append("]");
        return sb.toString();
    }


	// Statistics
	
	public Double getTotalIdleTime() {
		return totalIdleTime;
	}
	
	public void setTotalIdleTime(Double totalIdleTime) {
		this.totalIdleTime = totalIdleTime;
	}
	
	
	public Double getTotalResponseTime() {
		return totalResponseTime;
	}
	
	public void setTotalResponseTime(Double totalResponseTime) {
		this.totalResponseTime = totalResponseTime;
	}
	
	public Double getTotalOpenTime() {
		return totalOpenTime;
	}
	
	public void setTotalOpenTime(Double totalOpenTime) {
		this.totalOpenTime = totalOpenTime;
	}

	public Long getTimeFromCreationToClose() {
		return timeFromCreationToClose;
	}

	public void setTimeFromCreationToClose(Long timeFromCreationToClose) {
		this.timeFromCreationToClose = timeFromCreationToClose;
	}

	public Long getTimeFromCreationToFirstReply() {
		return timeFromCreationToFirstReply;
	}

	public void setTimeFromCreationToFirstReply(Long timeFromCreationToFirstReply) {
		this.timeFromCreationToFirstReply = timeFromCreationToFirstReply;
	}

	public void add(ItemUser itemUser){
		this.itemUsers.add(itemUser);
	}//add

	/**
	 * @return the file1
	 */
	public File getFile1() {
		return file1;
	}

	/**
	 * @param file1 the file1 to set
	 */
	public void setFile1(File file1) {
		this.file1 = file1;
	}

	/**
	 * @return the file2
	 */
	public File getFile2() {
		return file2;
	}

	/**
	 * @param file2 the file2 to set
	 */
	public void setFile2(File file2) {
		this.file2 = file2;
	}

	/**
	 * @return the file3
	 */
	public File getFile3() {
		return file3;
	}

	/**
	 * @param file3 the file3 to set
	 */
	public void setFile3(File file3) {
		this.file3 = file3;
	}

	/**
	 * @return the file4
	 */
	public File getFile4() {
		return file4;
	}

	/**
	 * @param file4 the file4 to set
	 */
	public void setFile4(File file4) {
		this.file4 = file4;
	}

	/**
	 * @return the file5
	 */
	public File getFile5() {
		return file5;
	}

	/**
	 * @param file5 the file5 to set
	 */
	public void setFile5(File file5) {
		this.file5 = file5;
	}


	
}
