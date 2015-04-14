# Cluster support for Lucene

You may configure cluster support for Lucene using classes in this module. There's 2 possible setup:

1) A JMS Module to integrate in Hibernate Search configuration. This solution has the disavantage of a SPoF (only a single master node).
2) A JMS-Topic based that removes the SPoF described in (1).  

## Full text JMS Module

This project allows setting up a shared full text index Lucene. It consists in:

* A set of slave nodes that pushes changes to a JMS queue, and periodically fetch a copy the master index.
* A master node that reads the messages pushed by slave nodes

The new following sections present a setup example.


### Install MDM cluster

Set up nodes:

* Install MDM server node1 (port 8180)
* Install MDM server node2 (port 8280)
* Each MDM Server has its *own* full text directory (no share at this point).
* Start both nodes and checks initialization is ok.
* Stop MDM instances

### Description and options
Each MDM node should work on its own index directory: in a slow paced environment, 2 MDM nodes can accommodate
a shared directory. But when you start stressing the nodes, lock issues appears as Lucene tries to
concurrently access and modify the indexes. Hibernate Search (the extension of Hibernate used in MDM to serve
full text queries) provides support for distributed indexes. From documentation of Hibernate Search 3.2.1:

```
Out of the box support for the Apache Lucene back end and the JMS back end. Default to lucene. Supports also jms, blackhole, jgroupsMaster and jgroupsSlave.
```

Available options are:

* lucene (<- the one used by default)
* jms
* blackhole: not very interesting (according to javadoc: "This backend does not do anything: the Documents are not sent to any index but are discarded.").
* jgroups

This leave 2 options for distributed indexes:

* jms
* jgroups

This setup only covers the "jms" option, as configuring "jgroups" requires heavy JBoss configuration that may have
bigger impacts than "jms" option ("jgroups" implies to enable JBoss in cluster mode).

### Set up JMS-based replication

This solution uses the JBoss JMS of the Master MDM server.

#### In MASTER node

* Add a queue "jms/mdm/fulltext"
Adds following content in jbossmq-destinations-service.xml (.../server/default/deploy/jms):

```
  <mbean code="org.jboss.mq.server.jmx.Queue"
	 name="jboss.mq.destination:service=Queue,name=jms/mdm/fulltext">
    <depends optional-attribute-name="DestinationManager">jboss.mq:service=DestinationManager</depends>
  </mbean>
```

* Restart MDM Server, look for line:

```
2015-01-20 11:18:16,810 INFO  [org.jboss.mq.server.jmx.Queue.jms/mdm/fulltext] Bound to JNDI name: queue/jms/mdm/fulltext
```

* In tem.ear (jboss_dir/server/default/deploy):
** Add jar "org.talend.mdm.storage.sql.jms-5.6-SNAPSHOT.jar" to tem.ear: this jar contains the JMS consumer that will receive updates from slave nodes.
** Edit META-INF/application.xml: add the following module:

```
  <module>
    <ejb>org.talend.mdm.storage.sql.jms-5.6-SNAPSHOT.jar</ejb>
  </module>
```

* Restart MDM Server, look for line:

```
"2015-01-20 13:16:05,781 INFO  [org.jboss.ejb.EjbModule] Deploying JMSFullTextController"
"... Enabled JMS sharing for full text indexes."
```

* Modify datasources.xml (jboss_dir/server/default/conf):

```
<property name="hibernate.show_sql">false</property>
<property name="hibernate.search.default.sourceBase">/home/fhuaulme/tmp/Talend-MDMServer-cluster/data/indexes/MySQL-Default</property> <!-- (1) -->
<property name="hibernate.search.default.indexBase">/home/fhuaulme/tmp/Talend-MDMServer-20141207_1530-V5.6.1_node1/jboss-4.2.2.GA/server/default/data/indexes/MySQL-Default</property> <!-- (2) -->
<property name="hibernate.search.default.refresh">1800</property> <!-- (3) -->
<property name="hibernate.search.default.directory_provider">org.hibernate.search.store.FSMasterDirectoryProvider</property>
```

(1): you have a common directory (shared by both master and slave nodes), where slave nodes will refresh their local copy.
(2): the master local index.
(3): Every 1800 seconds (30 min), master polls index from (1) and copy to (2). Documentation of Hibernate indicates
time period should be around twice the time needed to copy content from (1) to (2). As index size varies from an environment
to another, advising a value is hard.

* Restart master node. You need to have master node up before starting slave nodes

#### In SLAVE node

* Modify datasources.xml (jboss_dir/server/default/conf):

```
<property name="hibernate.show_sql">false</property>
<property name="hibernate.search.default.sourceBase">/home/fhuaulme/tmp/Talend-MDMServer-cluster/data/indexes/MySQL-Default</property> <!-- (1) -->
<property name="hibernate.search.default.indexBase">/home/fhuaulme/tmp/Talend-MDMServer-20141207_1530-V5.6.1_node2/jboss-4.2.2.GA/server/default/data/indexes/MySQL-Default</property> <!-- (2) -->
<property name="hibernate.search.default.refresh">1800</property> <!-- (3) -->
<property name="hibernate.search.default.directory_provider">org.hibernate.search.store.FSSlaveDirectoryProvider</property> <!-- (4) -->
<property name="hibernate.search.worker.backend">jms</property> <!-- (5) -->
<property name="hibernate.search.worker.jms.connection_factory">/ConnectionFactory</property> <!-- (5) -->
<property name="hibernate.search.worker.jndi.java.naming.provider.url">jnp://localhost:1199</property> <!-- (5) -->
<property name="hibernate.search.worker.jms.queue">queue/jms/mdm/fulltext</property> <!-- (5) -->
```

(1): you have a common directory (shared by both master and slave nodes), slave node gets the latest index from this location.
(2): the slave local index.
(3): Every 1800 seconds (30 min), slave polls index from (1) and copy to (2). Documentation of Hibernate indicates
time period should be around twice the time needed to copy content from (1) to (2). As index size varies from an environment
to another, advising a value is hard.
(4) Be careful about the class name.
(5) JMS related properties: "jms" for backend specifies to Hibernate Search that any index update should be pushed to the
queue specified in these properties.
Please note the property "hibernate.search.worker.jndi.java.naming.provider.url" uses the RMI port of the *master* server.

* Make sure master node is up, since slave node will try to connect to jnp://localhost:1199.

* Restart slave node.

## JMS-Topic

To configure 

### ActiveMQ configuration

Make sure your ActiveMQ (JMS provider) is up. You can start an ActiveMQ server using Docker:
```
$ docker run -p 61616:61616 -p 8161:8161 tv2norge/activemq
```
(61616 is the active mq port, and 8161 is the active mq web console)
Log in the web console (http://0.0.0.0:8161) with "admin"/"admin" (default credentials).
Create a new topic (e.g. "mdm.search.lucene").

### Cluster node installation

You must repeat the following steps for all members of your cluster:

*Jar copy (!) Two JARs to copy (!)*
Copy the JAR built by this project in tem.ear (/lib directory in tem.ear).
Copy the ActiveMQ JAR in tem.ear (/lib directory in tem.ear).

*MDM Configuration*
Modify the MDM configuration properties (in mdm.conf).

"hibernate.search.jms.context_factory" defaults to "org.apache.activemq.jndi.ActiveMQInitialContextFactory".
"hibernate.search.jms.provider_url" defaults to "tcp://localhost:61616".
"hibernate.search.jms.topic" defaults to "mdm.search.lucene".
"hibernate.search.jms.connection_factory" defaults to "ConnectionFactory".

*Datasources configuration*
Modify datasource properties (in datasources.xml):
```
<properties>
    <property key="hibernate.ejb.interceptor">com.amalto.core.storage.hibernate.SearchInterceptor</property>
</properties>
```
Your datasources.xml may then look like (example below is for H2, but changes are database agnostic):
```
<master>
    <type>RDBMS</type>
    <rdbms-configuration>
        <dialect>H2</dialect>
        <connection-driver-class>org.h2.Driver</connection-driver-class>
        <connection-url>jdbc:h2:///opt/Talend-MDMServer-20141126_1546-V5.6.1_node1/jboss-4.2.2.GA/server/default/data/H2-Default/${container}_MASTER</connection-url>
        <connection-username>sa</connection-username>
        <connection-password></connection-password>
        <fulltext-index-directory>/opt/Talend-MDMServer-20141126_1546-V5.6.1_node1/jboss-4.2.2.GA/server/default/data/indexes/H2-Default</fulltext-index-directory>
        <schema-generation>update</schema-generation>
        <properties>
            <property name="hibernate.show_sql">false</property>
            <property name="hibernate.connection.DB_CLOSE_ON_EXIT">FALSE</property>
            <property name="hibernate.connection.MVCC">TRUE</property>
            <property name="hibernate.ejb.interceptor">com.amalto.core.storage.hibernate.SearchInterceptor</property>
        </properties>
    </rdbms-configuration>
</master>
```

*Last step*
Start your MDM server.
