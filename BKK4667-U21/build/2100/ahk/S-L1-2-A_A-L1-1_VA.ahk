global OffsetX := 3
global OffsetY := 26
global InputFileName = "S-L1-2-A_A-L1-1_VA"
global InputFilePath = "C:\Users\Poldet\dev\workspaces\mobicat\mouse-bot\BKK4667-U21\line_sweep_tools\2100"
global BuildPath = "C:\Users\Poldet\dev\workspaces\mobicat\mouse-bot\BKK4667-U21\build\2100"
global LSTPath = "C:\Program Files (x86)\Anritsu\Line Sweep Tools"


Coord(x, y) {
	return % "x" . (X + OffsetX) . " y" . (Y + OffsetY)
}

OpenFile(path, fileName) {
	SetTitleMatchMode, RegEx

	Send, !f 
	Send, {ENTER}
	WinWaitActive, Open Line Sweep Tools File
	FilePath = %path%\%fileName%.dat
	SendMessage, 0x0C, , &FilePath, Edit2, Open Line Sweep Tools File
	ControlClick, WindowsForms10.BUTTON.app.0.33c0d9d1, Open Line Sweep Tools File
	WinWaitActive, , %fileName%
	Send, !e
	Send, {ENTER}

	WinWaitActive, ^Plot Properties
}

PerformActionTab1() {
	SetTitleMatchMode, RegEx

	SendMessage, 0x1330, 0, , WindowsForms10.SysTabControl32.app.0.33c0d9d1, ^Plot Properties
	SendMessage, 0x0C, , "VSWR", WindowsForms10.EDIT.app.0.33c0d9d2, ^Plot Properties
	SendMessage, 0x0C, , "Amara Bangkok Hotel (BKK4667-U21) S-L1-2-A_A-L1-1 (ANT)", WindowsForms10.EDIT.app.0.33c0d9d1, ^Plot Properties
	Send, !a
}

PerformActionTab2() {
	SetTitleMatchMode, RegEx
	
	SendMessage, 0x1330, 1, , WindowsForms10.SysTabControl32.app.0.33c0d9d1, ^Plot Properties
	Control, Choose, 3, WindowsForms10.COMBOBOX.app.0.33c0d9d1, ^Plot Properties
	Send, !a
}

PerformActionTab3() {
	SetTitleMatchMode, RegEx

	SendMessage, 0x1330, 2, , WindowsForms10.SysTabControl32.app.0.33c0d9d1, ^Plot Properties
	Send, {TAB 4}{Del}1.35 ; Type top
	Send, {TAB 1}{Del}0.95 ; Type bottom
	ControlClick, % Coord(397, 134), A ; Choose Meters

	ControlClick, % Coord(152, 184), A ; Choose Single Value
	Sleep, 350
	ControlClick, % Coord(460, 230), A ; Choose Single limit line value
	Send, {Shift down}{Home}{Shift up}{Del}1.3 ; Type limit

	Send, !a
}

PerformActionTab4() {
	SetTitleMatchMode, RegEx

	SendMessage, 0x1330, 3, , WindowsForms10.SysTabControl32.app.0.33c0d9d1, ^Plot Properties

	ControlClick, %  Coord(47, 114), A ; Enable M1 Button
	ControlClick, %  Coord(114, 114), A ; Hit M1 Peak

	ControlClick, %  Coord(47, 174), A ; Enable M2 Button
	Sleep, 250 ; Wait until Distance Textfield become fully enable
	ControlClick, %  Coord(297, 174), A ; Hit Distance Textfield
	Send, {Shift down}{Home}{Shift up}{Del}0 ; Set text Distance Textfield
	
	Send, !a
}

PerformActionTab5() {
	SendMessage, 0x1330, 4, , WindowsForms10.SysTabControl32.app.0.33c0d9d1, ^Plot Properties

	ControlClick, WindowsForms10.BUTTON.app.0.33c0d9d25 ; Thin Line

	Send, !a
}

ExportToCSV(buildPath, fileName) {
	Send, !f 
	Send, {Down 4}{Right 1}{ENTER}
	WinWaitActive, Save As
	BuildFile = %buildPath%\%fileName%.csv
	SendMessage, 0x0C, , &BuildFile, Edit1, Save As
	Sleep, 1000
	Send, !s
}

ExportToImage(buildPath, fileName) {
	Send, !f 
	Send, {Down 4}{Right 1}{Down 1}{Right 1}{Down 1}{ENTER}
	WinWaitActive, Save As
	BuildFile = %buildPath%\%fileName%.jpg
	SendMessage, 0x0C, , &BuildFile, Edit1, Save As
	Sleep, 1000
	Send, !s
}

; START 
SetTitleMatchMode, RegEx

IfWinNotExist, ahk_exe LST.exe 
{
	Run, LST.exe, %LSTPath%
	Sleep, 7000
}

WinActivate, ahk_exe LST.exe
WinWaitActive, ahk_exe LST.exe

OpenFile(InputFilePath, InputFileName)

; TAB 1
PerformActionTab1()

; TAB 2
PerformActionTab2()

; TAB 3
PerformActionTab3()

; TAB 4
PerformActionTab4()

; TAB 5
PerformActionTab5()

; Save
Send, !o

; Export to CSV
ExportToCSV(BuildPath, InputFileName)

ExportToImage(BuildPath, InputFileName)

Send ^s
Sleep, 1000
IfWinExist, Overwite Old DAT?
{
	ControlClick, Button1
}
Send !fc
;WinMinimize, ahk_exe LST.exe