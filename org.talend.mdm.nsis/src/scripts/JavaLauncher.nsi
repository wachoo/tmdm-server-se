; Java Launcher
;--------------
 
Name "openMDM"
Caption "The Talend open MDM server"
Icon "..\resources\mdm.ico"
OutFile "..\..\build\openMDM.exe"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
  
Section ""	
  StrCpy $0 'jre\bin\java -Dprogram.name=run.jar -Xms512m -Xmx1024m -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Djava.endorsed.dirs=./lib/endorsed -classpath ./bin/run.jar org.jboss.Main -b 0.0.0.0' 
  SetOutPath $EXEDIR
  Exec $0
SectionEnd
 
