set META_FILE_DIR=BKK4667-U21

cd "C:\Users\Poldet\dev\workspaces\mobicat\mouse-bot\absg\com\absg"
call groovyc *.groovy
call groovy VerifyWorker.groovy %META_FILE_DIR%

if not "%1" == "nopause" (
	pause
)