;NSIS Installer for openMDM
;Talend Open Data Solutions

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "openMDM"
  Caption "openMDM Setup"
  OutFile "openMDM_installer.exe"

  XPStyle on

  ;Default installation folder
  InstallDir "$PROGRAMFILES\Talend"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\openMDM" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel admin
  
  ;Set compression algorithm
  SetCompressor bzip2

;Constants
!define MDM_SERVICE "openMDMService"

;--------------------------------
;Variables

  Var StartMenuFolder

;--------------------------------
;Interface Configuration

  !define MUI_HEADERIMAGE
  !define MUI_HEADERIMAGE_BITMAP "logo-openMDM.bmp" ; optional
  !define MUI_ICON "mdm_installer.ico"
  !define MUI_UNICON "mdm_uninstaller.ico"
  !define MUI_ABORTWARNING
  

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "openMDM\License.txt"
  ;!insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY

  
  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\openMDM" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
	
  !insertmacro MUI_PAGE_INSTFILES
  !define MUI_FINISHPAGE_RUN ""
  !define MUI_FINISHPAGE_RUN_TEXT "run as a windows service(Manual)"
  !define MUI_FINISHPAGE_RUN_FUNCTION runService
  !insertmacro MUI_PAGE_FINISH  	
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "install" Installation

  SetOutPath "$INSTDIR"
  
  ;Files to Install
  File /r openMDM

  ;Store installation folder
  WriteRegStr HKCU "Software\openMDM" "" $INSTDIR
  
  
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
  ;Create shortcuts
  CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
  CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninstall_openMDM.exe"
  CreateShortCut "$SMPROGRAMS\$StartMenuFolder\openMDM.lnk" "$INSTDIR\openMDM\openMDM.exe"

  ;create desktop shortcut
  CreateShortCut "$DESKTOP\openMDM.lnk" "$INSTDIR\openMDM\openMDM.exe" ""


  !insertmacro MUI_STARTMENU_WRITE_END

  ;write uninstall information to the registry
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\openMDM" "DisplayName" "openMDM (remove only)"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\openMDM" "UninstallString" "$INSTDIR\Uninstall_openMDM.exe"

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall_openMDM.exe"

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_ENGLISH} "A test section."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Uninstaller Section

Section "Uninstall"
  ;remove the service  
  Exec '"$INSTDIR\openMDM\bin\service.bat" uninstall /NCRC'	
  
  ;Remove files
  RMDir /r "$INSTDIR\openMDM"
  Delete "$INSTDIR\Uninstall_openMDM.exe"

  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
    
  ;Remove Entries in Program Menus
  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\openMDM.lnk"
  RMDir "$SMPROGRAMS\$StartMenuFolder"

  ;Remove Desktop Icon
  Delete "$DESKTOP\openMDM.lnk"
  
  DeleteRegKey /ifempty HKCU "Software\openMDM"

  ;Delete Uninstaller And Unistall Registry Entries
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\openMDM"
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\openMDM"  
  
  ;DeleteRegKey HKEY_LOCAL_MACHINE "Software\Microsoft\Windows\CurrentVersion\Run\openMDM"	

SectionEnd

Function runService
  	;remove the service  
  	
	Exec '"$INSTDIR\openMDM\bin\service.bat" install /NCRC'	
	;registry it to let openMDM star when windows startup
	;WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Run\openMDM" "openMDM" "$INSTDIR\openMDM\openMDM.exe"	  
FunctionEnd
