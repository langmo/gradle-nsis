;Include Modern UI
!include "MUI2.nsh"

;Basic configuration
Name "gradle-nsis extended example"
!ifdef WIN64
	OutFile "ExtendedExample64bit.exe"
!else
	OutFile "ExtendedExample32bit.exe"
!endif
Unicode True
;Default installation folder
!ifdef WIN64
	InstallDir "$PROGRAMFILES64\gradle_nsis_extended_example"
!else
	InstallDir "$PROGRAMFILES32\gradle_nsis_extended_example"
!endif
;Request admin privileges for Vista/7/8/10
RequestExecutionLevel admin
!define MUI_ABORTWARNING

;Pages
!insertmacro MUI_PAGE_LICENSE "LICENSE"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES  
  
;Languages
!insertmacro MUI_LANGUAGE "English"

;Installer Sections
Section "gradle-nsis extended example" SecExample
  SetOutPath "$INSTDIR"
  FILE LICENSE
  !ifdef WIN64
	FILE Example64.exe
  !else
	FILE Example32.exe
  !endif
  SetOutPath "$INSTDIR\lib"
  FILE "lib\*"
SectionEnd

;Descriptions
LangString DESC_SecExample ${LANG_ENGLISH} "Copies the license, the JAR and the EXE file."
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecExample} $(DESC_SecExample)
!insertmacro MUI_FUNCTION_DESCRIPTION_END