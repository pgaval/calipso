# ============================================
# HOWTO BUILD AND RUN
# ============================================
# First, choose between HSQL or custom database setup, then follow build instructions bellow.
#
#
# HSQLDB Database Setup
# ============================================
# Just make sure "calipso.home" bellow is commented out.
#
#
# Custom Database Setup
# ============================================
# 1) Create a database, e.g. "calipso"
#
# 2) Add the appropriate driver in your servlet container's shared libraries (e.g. TOMCAT_HOME/shared/lib)
#
# 3) Create your calipso.home (see property below), e.g. "calipso" in your home directory (e.g. /home/username/calipso)
#
# 4) Copy the file calipso.properties in it and edit it according to your environment
#
#
# Build and run
# ============================================
# 1) Rename this file to "build.properties"
#
# 2) Open a console and navigate to the directory this file lies in:
#       cd /path/to/calipso-parent
#
# 3) Install JARs missing from Maven central. If this produces an error edit the "mvn.executable" property bellow (e.g. to point to mvn.bat for windows)
#       ant mvn-add-jars
#
# 4) Eclipse IDE users will want this. You should know how to import multimodule projects in Eclipse.
#       mvn org.apache.maven.plugins:maven-eclipse-plugin:2.6:clean org.apache.maven.plugins:maven-eclipse-plugin:2.6:eclipse -DdownloadSources=true -DdownloadJavadocs=true
#
# 5) Build the project:
#       mvn clean install -Dmaven.test.skip=true
#
# 6) Navigate in the WAR module folder and run it on Jetty:
#       cd calipso-war
#       mvn jetty:run -Dmaven.test.skip=true
#
# 7) Fire up a browser and go to http://localhost:8080/calipso
#
# 8) Use username "admin", password "admin"
#
# You can also deploy the generated WAR to another servlet container like Tomcat.
#
# ============================================
# CONFIGURATION PROPERTIES
# ============================================
#
# Location of the Maven Binary or just "mvn" (without the quotes)
# Windows users may want to point to their mvn.bat
mvn.executable=mvn

# Calipso Home (to store uploads, indexes and whatnot)
# Leave commented out to use HSQLDB
# calipso.home=target/calipso_home

# Wicket Deploy Mode
calipso.deploy.mode=DEPLOYMENT

# WAR Name
war.name=calipso
