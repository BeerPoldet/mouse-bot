set META_FILE_NAME=meta.properties
set META_FILE_DIR=test_ps3

cd "C:\Users\Poldet\dev\workspaces\mobicat\mouse-bot\absg\com\absg"
call groovyc *.groovy
call groovy AutoBotScriptGenerator.groovy %META_FILE_NAME% %META_FILE_DIR%
timeout /t -1