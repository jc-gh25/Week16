@echo off
REM Music Library API - Sample Data Population Script (Windows)
REM This script populates the database with sample artists, genres, and albums
REM Usage: populate-music-library.bat [BASE_URL]
REM Example: populate-music-library.bat https://javabc.up.railway.app
REM Default: http://localhost:8080

setlocal enabledelayedexpansion

if "%1"=="" (
    set BASE_URL=http://localhost:8080
) else (
    set BASE_URL=%1
)

set API_URL=%BASE_URL%/api

echo ==========================================
echo Music Library API - Data Population
echo ==========================================
echo Target API: %API_URL%
echo.

echo ==========================================
echo Step 1: Creating Genres
echo ==========================================

REM Create Rock Genre
curl -s -X POST "%API_URL%/genres" -H "Content-Type: application/json" -d "{\"name\":\"Rock\",\"description\":\"Rock music is a broad genre of popular music that originated as rock and roll in the United States in the late 1940s and early 1950s.\"}" > temp_genre_rock.json
echo Created Rock genre

REM Create Pop Genre
curl -s -X POST "%API_URL%/genres" -H "Content-Type: application/json" -d "{\"name\":\"Pop\",\"description\":\"Pop music is a genre of popular music that originated in its modern form during the mid-1950s.\"}" > temp_genre_pop.json
echo Created Pop genre

REM Create Jazz Genre
curl -s -X POST "%API_URL%/genres" -H "Content-Type: application/json" -d "{\"name\":\"Jazz\",\"description\":\"Jazz is a music genre that originated in the African-American communities of New Orleans in the late 19th and early 20th centuries.\"}" > temp_genre_jazz.json
echo Created Jazz genre

REM Create Blues Genre
curl -s -X POST "%API_URL%/genres" -H "Content-Type: application/json" -d "{\"name\":\"Blues\",\"description\":\"Blues is a music genre and musical form which originated in the Deep South of the United States around the 1860s.\"}" > temp_genre_blues.json
echo Created Blues genre

REM Create Electronic Genre
curl -s -X POST "%API_URL%/genres" -H "Content-Type: application/json" -d "{\"name\":\"Electronic\",\"description\":\"Electronic music is music that employs electronic musical instruments, digital instruments, or circuitry-based music technology.\"}" > temp_genre_electronic.json
echo Created Electronic genre

echo.
echo ==========================================
echo Step 2: Creating Artists
echo ==========================================

REM Create The Beatles
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"The Beatles\",\"description\":\"The Beatles were an English rock band formed in Liverpool in 1960. Regarded as the most influential band of all time.\"}" > temp_artist_beatles.json
echo Created The Beatles

REM Create The Rolling Stones
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"The Rolling Stones\",\"description\":\"The Rolling Stones are an English rock band formed in London in 1962. Active for six decades.\"}" > temp_artist_stones.json
echo Created The Rolling Stones

REM Create Pink Floyd
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"Pink Floyd\",\"description\":\"Pink Floyd are an English rock band formed in London in 1965. Distinguished for their extended compositions and sonic experimentation.\"}" > temp_artist_pink_floyd.json
echo Created Pink Floyd

REM Create Queen
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"Queen\",\"description\":\"Queen are a British rock band formed in London in 1970. Classic line-up was Freddie Mercury, Brian May, Roger Taylor and John Deacon.\"}" > temp_artist_queen.json
echo Created Queen

REM Create Led Zeppelin
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"Led Zeppelin\",\"description\":\"Led Zeppelin were an English rock band formed in London in 1968.\"}" > temp_artist_led_zeppelin.json
echo Created Led Zeppelin

REM Create Nirvana
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"Nirvana\",\"description\":\"Nirvana was an American rock band formed in Aberdeen, Washington, in 1987.\"}" > temp_artist_nirvana.json
echo Created Nirvana

REM Create Radiohead
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"Radiohead\",\"description\":\"Radiohead are an English rock band formed in Abingdon, Oxfordshire, in 1985.\"}" > temp_artist_radiohead.json
echo Created Radiohead

REM Create Daft Punk
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"Daft Punk\",\"description\":\"Daft Punk were a French electronic music duo formed in 1993 in Paris.\"}" > temp_artist_daft_punk.json
echo Created Daft Punk

REM Create Miles Davis
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"Miles Davis\",\"description\":\"Miles Dewey Davis III was an American trumpeter, bandleader, and composer.\"}" > temp_artist_miles_davis.json
echo Created Miles Davis

REM Create Taylor Swift
curl -s -X POST "%API_URL%/artists" -H "Content-Type: application/json" -d "{\"name\":\"Taylor Swift\",\"description\":\"Taylor Alison Swift is an American singer-songwriter.\"}" > temp_artist_taylor_swift.json
echo Created Taylor Swift

echo.
echo ==========================================
echo Step 3: Creating Albums
echo ==========================================
echo NOTE: Using artist ID 1-10 and genre ID 1-5
echo If your IDs are different, you may need to adjust the script
echo.

REM The Beatles - Abbey Road
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"Abbey Road\",\"releaseDate\":\"1969-09-26\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg\",\"trackCount\":17,\"catalogNumber\":\"PCS-7088\",\"artist\":{\"artistId\":1},\"genres\":[{\"genreId\":1}]}"
echo Created Abbey Road

REM The Beatles - Sgt. Pepper's
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"Sgt. Pepper's Lonely Hearts Club Band\",\"releaseDate\":\"1967-06-01\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/5/50/Sgt._Pepper%%27s_Lonely_Hearts_Club_Band.jpg\",\"trackCount\":13,\"catalogNumber\":\"PMC-7027\",\"artist\":{\"artistId\":1},\"genres\":[{\"genreId\":1},{\"genreId\":2}]}"
echo Created Sgt. Pepper's Lonely Hearts Club Band

REM The Rolling Stones - Sticky Fingers
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"Sticky Fingers\",\"releaseDate\":\"1971-04-23\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/f/f8/StickyFingersCover.jpg\",\"trackCount\":10,\"catalogNumber\":\"COC-59100\",\"artist\":{\"artistId\":2},\"genres\":[{\"genreId\":1},{\"genreId\":4}]}"
echo Created Sticky Fingers

REM Pink Floyd - The Dark Side of the Moon
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"The Dark Side of the Moon\",\"releaseDate\":\"1973-03-01\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png\",\"trackCount\":10,\"catalogNumber\":\"SHVL-804\",\"artist\":{\"artistId\":3},\"genres\":[{\"genreId\":1}]}"
echo Created The Dark Side of the Moon

REM Pink Floyd - The Wall
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"The Wall\",\"releaseDate\":\"1979-11-30\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/1/13/PinkFloydWallCoverOriginalNoText.jpg\",\"trackCount\":26,\"catalogNumber\":\"SHDW-411\",\"artist\":{\"artistId\":3},\"genres\":[{\"genreId\":1}]}"
echo Created The Wall

REM Queen - A Night at the Opera
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"A Night at the Opera\",\"releaseDate\":\"1975-11-21\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png\",\"trackCount\":12,\"catalogNumber\":\"EMI-EMTC-103\",\"artist\":{\"artistId\":4},\"genres\":[{\"genreId\":1}]}"
echo Created A Night at the Opera

REM Led Zeppelin - Led Zeppelin IV
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"Led Zeppelin IV\",\"releaseDate\":\"1971-11-08\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/2/26/Led_Zeppelin_-_Led_Zeppelin_IV.jpg\",\"trackCount\":8,\"catalogNumber\":\"SD-7208\",\"artist\":{\"artistId\":5},\"genres\":[{\"genreId\":1}]}"
echo Created Led Zeppelin IV


REM Radiohead - OK Computer
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"OK Computer\",\"releaseDate\":\"1997-05-21\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/b/ba/Radioheadokcomputer.png\",\"trackCount\":12,\"catalogNumber\":\"CDNODATA01\",\"artist\":{\"artistId\":7},\"genres\":[{\"genreId\":1}]}"
echo Created OK Computer

REM Daft Punk - Discovery
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"Discovery\",\"releaseDate\":\"2001-03-12\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/2/27/Daft_Punk_-_Discovery.png\",\"trackCount\":14,\"catalogNumber\":\"V2-27539\",\"artist\":{\"artistId\":8},\"genres\":[{\"genreId\":5},{\"genreId\":2}]}"
echo Created Discovery

REM Daft Punk - Random Access Memories
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"Random Access Memories\",\"releaseDate\":\"2013-05-17\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/a/a7/Random_Access_Memories.jpg\",\"trackCount\":13,\"catalogNumber\":\"COL-88883716862\",\"artist\":{\"artistId\":8},\"genres\":[{\"genreId\":5},{\"genreId\":2}]}"
echo Created Random Access Memories

REM Miles Davis - Kind of Blue
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"Kind of Blue\",\"releaseDate\":\"1959-08-17\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/9/9c/MilesDavisKindofBlue.jpg\",\"trackCount\":5,\"catalogNumber\":\"CL-1355\",\"artist\":{\"artistId\":9},\"genres\":[{\"genreId\":3}]}"
echo Created Kind of Blue

REM Taylor Swift - 1989
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"1989\",\"releaseDate\":\"2014-10-27\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/f/f6/Taylor_Swift_-_1989.png\",\"trackCount\":13,\"catalogNumber\":\"BMR-00050\",\"artist\":{\"artistId\":10},\"genres\":[{\"genreId\":2}]}"
echo Created 1989

REM Taylor Swift - Folklore
curl -s -X POST "%API_URL%/albums" -H "Content-Type: application/json" -d "{\"title\":\"Folklore\",\"releaseDate\":\"2020-07-24\",\"coverImageUrl\":\"https://upload.wikimedia.org/wikipedia/en/f/f8/Taylor_Swift_-_Folklore.png\",\"trackCount\":16,\"catalogNumber\":\"REP-00092\",\"artist\":{\"artistId\":10},\"genres\":[{\"genreId\":2}]}"
echo Created Folklore

REM Clean up temp files
del temp_*.json 2>nul

echo.
echo ==========================================
echo Population Complete!
echo ==========================================
echo.
echo Summary:
echo - 5 Genres created
echo - 10 Artists created
echo - 13 Albums created with cover images
echo.
echo You can now browse your music library at:
echo %BASE_URL%
echo.
echo API Endpoints:
echo - GET %API_URL%/artists
echo - GET %API_URL%/albums
echo - GET %API_URL%/genres
echo.

endlocal
