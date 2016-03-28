---- Aggregate ----
version: 0.0.1

Aggregate is a set of web services to provide MasteryGrids interface with 
the course structure, content access and progress and knowledge levels of 
students on content and topics. 

Configuration:
-----------------
Rename the file WebContent/config_default.xml to config.xml 
and sets the correct database connection parameters

Interface to User Model
------------------------
A java interface UMInterface.java is defined and should be implemented. 
As a working example, the interface is implemented in PAWSUMInterfaceV2.java
with access to PAWS lab user model services.

Note for MySQL configuration
----------------------------
Set a bigger capacity for MySQL system variable group_concat_max_len
We set it as: 
group_concat_max_len = 8192
This is necessary to support queries retrieving long text whent using group_concat function.
