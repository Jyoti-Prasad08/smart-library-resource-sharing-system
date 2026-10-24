@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
javac -d out src\app\Main.java src\app\LibraryWindow.java src\model\Borrowable.java src\model\LibraryResource.java src\model\Book.java src\model\EBook.java src\model\Journal.java src\model\ProjectResource.java src\model\Student.java src\model\Loan.java src\model\Reservation.java src\service\LibraryService.java
if errorlevel 1 (
    echo.
    echo Compilation failed. Check the messages above.
    pause
    exit /b 1
)
java -cp out app.Main
pause
