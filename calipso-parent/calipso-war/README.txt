# ============================================
# HOWTO BUILD AND RUN
# ============================================
# --------------------------------------------
# For Jetty/HSQLDB (great for development, demos etc):
# -------------------------------------------- 
# Leave calipso.home below commented out, use the command line
# to navigate into the project folder and run the following commands:
#       
#       ant mvn-add-jars
#       mvn jetty:run -DskipTests=true
#       
# The first line adds some jars in your local maven repo to be able to build 
# the project. The second builds the project and launches it on the Jetty 
# servlet container and HSQLDB. The last one is only needed for Eclipse IDE 
# users, you should refresh after executing it. Finally, to access Calipso fire
# up a browser and go to http://localhost:8080/calipso-war/
# 
# To reload the webapp after making changes in your IDE just hit "enter" while 
# in the Jetty console. To setup debugging (JPDA) for Eclipse/Netbeans checkout:
#
#       https://cwiki.apache.org/WICKET/maven-jetty-plugin.html
#      
# --------------------------------------------
# For other container/database configurations:
# --------------------------------------------
# 1) Create a database, e.g. "calipso"
# 2) Add the appropriate driver in your servlet container's shared libraries (e.g. TOMCAT_HOME/shared/lib)
# 3) Create your calipso.home (see property below), e.g. "calipso" in your home directory (e.g. /home/username/calipso)
# 4) Copy the file calipso.properties in it and edit it according to your environment
# 5) Edit this file accordingly and RENAME it to "build.properties"
# 6) Open a console and navigate to the directory this file lies in:
#       cd /path/to/calipso-war
# 7) Run the following commands (the last one is only needed for Eclipse IDE users, you should refresh after executing it):
#       ant mvn-add-jars
#       mvn org.apache.maven.plugins:maven-eclipse-plugin:2.6:clean org.apache.maven.plugins:maven-eclipse-plugin:2.6:eclipse -DdownloadSources=true -DdownloadJavadocs=true
# 8) Build the project:
#       mvn install -DskipTests=true
# 9) Copy the target/calipso webapp directory to your target server as appropriate (e.g. as a WAR) 
# 10) Make sure your container is running, then fire up a browser and go to http://localhost:8080/calipso
# 11) Use username "admin", password "admin"
#
# ============================================
# CONFIGURATION PROPERTIES
# ============================================
# Location of the Maven Binary or just "mvn" (without the quotes)
mvn.executable=mvn
# Directory of your Tomcat base dir
tomcat.home=/home/MYUSERNAME/path/to/tomcat
# Calipso Version
calipso.version=2.1.2
# Calipso Home (to store uploads, indexes and whatnot)
calipso.home=target/calipso_home
# Wicket Depoy Mode
calipso.deploy.mode=DEPLOYMENT
# WAR Name
war.name=calipso