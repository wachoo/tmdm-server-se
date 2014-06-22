eXist can be deployed
	-as a standalone application
	-as a war file inside Jboss
	

In both cases, you need to take some COMMON STEPS then steps specific to the chosen implementation.


COMMON STEPS
a-In jboss/server/default/lib, remove
	-all the z.exist* libraries
	-z.xmldb.jar and z.xmlrpc-1.2-patched.jar if they exist
Then add the  z.exist-1.2.6-*.jar files in common/ to the jboss/server/default/lib directory

b-in //amalto/mdm.conf or jboss/mdm.conf, depending on your implementation,
update (or add) the entry xmldb.administrator.password with the value nimda
NOTE: the database password is determined by the element cluster/@dbaPassword in conf.xml.


STANDALONE
Assuming, you have installed eXist in /opt/eXist-1.2.6:
a-Replace the file in /opt/eXist-1.2.6/conf.xml with the one in standalone/conf.xml
b-Replace the file in /opt/eXist-1.2.6/tools/jetty/etc/jetty.xml with the one in standalone/jetty.xml
c-in //amalto/mdm.conf or jboss/mdm.conf, depending on your implementation,
update (or add) the entry xmldb.server.port with the value 8088

AS WAR
a-Unzip the war downloaded from eXist web site as jboss/server/default/deploy/eXist-1.2.6.war
b-add the file asWAR/jboss-web.xml to jboss/server/default/deploy/eXist-1.2.6.war/WEB-INF/
c-replace the file in jboss/server/default/deploy/eXist-1.2.6.war/WEB-INF/conf.xml with the one in asWAR/conf.xml
d-backup the libs in jboss/lib/endorsed and replace them with the libs in asWAR/endorsed_libs

