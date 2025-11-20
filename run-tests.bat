@echo off
cd /d "C:\Users\user1\Desktop\Back End Software Dev\PortableGit\projects\Week16\music-library"
echo Running tests...
mvn test -Dtest=AlbumControllerIT
pause