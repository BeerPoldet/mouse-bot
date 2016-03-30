set META_FILE_DIR=BKK4667-U21

cd "C:\Users\Poldet\dev\workspaces\mobicat\mouse-bot\absg\com\absg"
call groovyc *.groovy
call groovy AutoBotScriptGenerator.groovy %META_FILE_DIR%
if %1 == "nopause" goto start
pause
:start