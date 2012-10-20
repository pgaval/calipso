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
 * This file incorporates work released by the JTrac project and covered 
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

package gr.abiss.calipso.lucene;

import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.lucene.IndexSearcher;
import gr.abiss.calipso.lucene.Indexer;

import java.io.File;
import java.util.List;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class IndexSearcherTest extends TestCase {
    
    private ApplicationContext context;
    
    @Override
    public void setUp() {
        File home = new File("target/home");
        if (!home.exists()) {
            home.mkdir();
        }
        File file = new File("target/home/indexes");
        if (!file.exists()) {
            file.mkdir();
        } else {            
            for (File f : file.listFiles()) {
                f.delete();
            }
        }
        System.setProperty("calipsoService.home", home.getAbsolutePath());
        context = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/applicationContext-lucene.xml");    
    }
    
    public void testFindItemIdsBySearchingWithinSummaryAndDetailFields() throws Exception {       
        Item item = new Item();
        item.setId(1);
        item.setSummary("this is a test summary");
        item.setDetail("the quick brown fox jumped over the lazy dogs");
        Indexer indexer = (Indexer) context.getBean("indexer");
        indexer.index(item);
        IndexSearcher searcher = (IndexSearcher) context.getBean("indexSearcher");
        List list = searcher.findItemIdsContainingText("lazy");
        assertEquals(1, list.size());
        list = searcher.findItemIdsContainingText("foo");
        assertEquals(0, list.size());
        list = searcher.findItemIdsContainingText("summary");
        assertEquals(1, list.size());
    }
    
}
