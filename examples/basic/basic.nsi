;Include Modern UI
!include "MUI2.nsh"

;Basic configuration
Name "gradle-nsis basic example"
OutFile "basic.exe"
Unicode True
;Default installation folder
InstallDir "$PROGRAMFILES64\gradle_nsis_basic_example"
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
Section "gradle-nsis basic example" SecExample
  SetOutPath "$INSTDIR"
  FILE LICENSE
  FILE basic.nsi
SectionEnd

;Descriptions
LangString DESC_SecExample ${LANG_ENGLISH} "Copies the license and the NSIS file."
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecExample} $(DESC_SecExample)
!insertmacro MUI_FUNCTION_DESCRIPTION_END