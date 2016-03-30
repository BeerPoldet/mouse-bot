set BAT_PATH=C:\Users\Poldet\dev\workspaces\mobicat\mouse-bot

rem call %BAT_PATH%\step1.bat > %BAT_PATH%\operation.log
call %BAT_PATH%\step2.bat "nopause" >> %BAT_PATH%\operation.log
call %BAT_PATH%\step3.bat "nopause" >> %BAT_PATH%\operation.log
call %BAT_PATH%\step4.bat "nopause" >> %BAT_PATH%\operation.log