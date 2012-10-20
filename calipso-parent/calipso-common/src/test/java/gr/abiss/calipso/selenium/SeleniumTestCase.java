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

package gr.abiss.calipso.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openqa.selenium.server.SeleniumServer;

/** 
 * base class for Selenium test scripts that hack JUnit so as to be
 * able to run test methods in the order in which they appear
 * in the source file. If the class name is "AllTest.java", following
 * boiler-plate must be included (I said this was a hack :)
 *
 *  static {
 *      clazz = AllTest.class;
 *  }
 *  
 *  public AllTest(String name) {
 *      super(name);
 *  }
 *
 */
public abstract class SeleniumTestCase extends TestCase {
    
    public SeleniumTestCase(String name) {
        super(name);
    }
  
    private static ThreadLocalSelenium threadLocalSelenium;
    protected static Class clazz;
    protected CalipsoSelenium selenium;    
    protected static SeleniumServer server;
    
    public static Test suite() throws Exception {        
        threadLocalSelenium = new ThreadLocalSelenium();
        Constructor constructor = clazz.getDeclaredConstructors()[0];
        Method[] methods = clazz.getMethods();
        TestSuite s = new TestSuite();
        for(Method m : methods) {
            if (m.getName().startsWith("test")) {
                Test test = (Test) constructor.newInstance(new Object[] { m.getName() });
                s.addTest(test);
            }
        }
        return s;
    }
    
    private static class ThreadLocalSelenium extends ThreadLocal {
        @Override
        public CalipsoSelenium initialValue() {
            try {
                server = new SeleniumServer();
                server.start();
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
            CalipsoSelenium s = new CalipsoSelenium("localhost", 8080/*SeleniumServer.getDefaultPort()*/, "*iexplore", "http://localhost:8080/calipsoService");
            s.start();
            return s;
        }
    }
    
    @Override
    public final void setUp() {
        selenium = (CalipsoSelenium) threadLocalSelenium.get();        
    }    
    
    protected void assertTextPresent(String text) {
        assertTrue(selenium.isTextPresent(text));
    }    
    
    protected void stopSelenium() {
        selenium.stop();
        server.stop();
    }
    
    /**
     * custom extension of Selenium to automatically wait for page to load
     * after clicking a button or link
     */
    public static class CalipsoSelenium extends DefaultSelenium {
        
        public CalipsoSelenium(String host, int port, String browser, String url) {
            super(host, port, browser, url);
        }
        
        public void clickAndWait(String locator) {
            click(locator);
            waitForPageToLoad("30000");
        }        
        
    }
    
}
