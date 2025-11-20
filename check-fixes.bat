@echo off
echo Checking if files compile...
cd /d "C:\Users\user1\Desktop\Back End Software Dev\PortableGit\projects\Week16\music-library"

echo Compiling main sources...
mvn compile -q

if %ERRORLEVEL% EQU 0 (
    echo Main sources compiled successfully!
    echo Compiling test sources...
    mvn test-compile -q
    
    if %ERRORLEVEL% EQU 0 (
        echo Test sources compiled successfully!
        echo Running specific failing tests...
        mvn test -Dtest=AlbumControllerIT#testGetAlbumById,AlbumControllerIT#testGetAllAlbums,AlbumControllerUpdateDeleteIT#setUp,ArtistControllerIT#testGetAllArtists,GenreControllerIT#testGetAllGenres -q
    ) else (
        echo Test compilation failed!
    )
) else (
    echo Main compilation failed!
)

pause