#!/bin/bash

# Music Library API - Sample Data Population Script
# This script populates the database with sample artists, genres, and albums
# Usage: ./populate-music-library.sh [BASE_URL]
# Example: ./populate-music-library.sh https://javabc.up.railway.app
# Default: http://localhost:8080

BASE_URL="${1:-http://localhost:8080}"
API_URL="${BASE_URL}/api"

echo "=========================================="
echo "Music Library API - Data Population"
echo "=========================================="
echo "Target API: $API_URL"
echo ""

# Color codes for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to make POST request and extract ID from response
create_entity() {
    local endpoint=$1
    local data=$2
    local name=$3
    
    echo -n "Creating $name... "
    response=$(curl -s -X POST "${API_URL}${endpoint}" \
        -H "Content-Type: application/json" \
        -d "$data")
    
    if [ $? -eq 0 ]; then
        # Extract ID from response (works for artistId, genreId, albumId)
        id=$(echo "$response" | grep -o '"[a-zA-Z]*Id":[0-9]*' | head -1 | grep -o '[0-9]*')
        if [ -n "$id" ]; then
            echo -e "${GREEN}✓ Created (ID: $id)${NC}"
            echo "$id"
            return 0
        else
            echo -e "${RED}✗ Failed to extract ID${NC}"
            echo "$response"
            return 1
        fi
    else
        echo -e "${RED}✗ Failed${NC}"
        return 1
    fi
}

echo "=========================================="
echo "Step 1: Creating Genres"
echo "=========================================="

# Create Genres
genre_rock=$(create_entity "/genres" '{
  "name": "Rock",
  "description": "Rock music is a broad genre of popular music that originated as rock and roll in the United States in the late 1940s and early 1950s."
}' "Rock")

genre_pop=$(create_entity "/genres" '{
  "name": "Pop",
  "description": "Pop music is a genre of popular music that originated in its modern form during the mid-1950s."
}' "Pop")

genre_jazz=$(create_entity "/genres" '{
  "name": "Jazz",
  "description": "Jazz is a music genre that originated in the African-American communities of New Orleans in the late 19th and early 20th centuries."
}' "Jazz")

genre_blues=$(create_entity "/genres" '{
  "name": "Blues",
  "description": "Blues is a music genre and musical form which originated in the Deep South of the United States around the 1860s."
}' "Blues")

genre_electronic=$(create_entity "/genres" '{
  "name": "Electronic",
  "description": "Electronic music is music that employs electronic musical instruments, digital instruments, or circuitry-based music technology."
}' "Electronic")

genre_hiphop=$(create_entity "/genres" '{
  "name": "Hip Hop",
  "description": "Hip hop music or hip-hop music, also known as rap music, is a genre of popular music developed in the United States."
}' "Hip Hop")

genre_classical=$(create_entity "/genres" '{
  "name": "Classical",
  "description": "Classical music generally refers to the formal musical tradition of the Western world, considered to be distinct from Western folk music or popular music traditions."
}' "Classical")

genre_country=$(create_entity "/genres" '{
  "name": "Country",
  "description": "Country music is a genre of popular music that originated in the Southern United States in the early 1920s."
}' "Country")

echo ""
echo "=========================================="
echo "Step 2: Creating Artists"
echo "=========================================="

# Create Artists
artist_beatles=$(create_entity "/artists" '{
  "name": "The Beatles",
  "description": "The Beatles were an English rock band formed in Liverpool in 1960. Regarded as the most influential band of all time, they were integral to the development of 1960s counterculture."
}' "The Beatles")

artist_stones=$(create_entity "/artists" '{
  "name": "The Rolling Stones",
  "description": "The Rolling Stones are an English rock band formed in London in 1962. Active for six decades, they are one of the most popular and enduring bands of the rock era."
}' "The Rolling Stones")

artist_pink_floyd=$(create_entity "/artists" '{
  "name": "Pink Floyd",
  "description": "Pink Floyd are an English rock band formed in London in 1965. Gaining an early following as one of the first British psychedelic groups, they were distinguished for their extended compositions, sonic experimentation, philosophical lyrics and elaborate live shows."
}' "Pink Floyd")

artist_queen=$(create_entity "/artists" '{
  "name": "Queen",
  "description": "Queen are a British rock band formed in London in 1970. Their classic line-up was Freddie Mercury, Brian May, Roger Taylor and John Deacon."
}' "Queen")

artist_led_zeppelin=$(create_entity "/artists" '{
  "name": "Led Zeppelin",
  "description": "Led Zeppelin were an English rock band formed in London in 1968. The group consisted of vocalist Robert Plant, guitarist Jimmy Page, bassist/keyboardist John Paul Jones, and drummer John Bonham."
}' "Led Zeppelin")

artist_nirvana=$(create_entity "/artists" '{
  "name": "Nirvana",
  "description": "Nirvana was an American rock band formed in Aberdeen, Washington, in 1987. Founded by lead singer and guitarist Kurt Cobain and bassist Krist Novoselic, the band went through a succession of drummers before recruiting Dave Grohl in 1990."
}' "Nirvana")

artist_radiohead=$(create_entity "/artists" '{
  "name": "Radiohead",
  "description": "Radiohead are an English rock band formed in Abingdon, Oxfordshire, in 1985. The band consists of Thom Yorke, brothers Jonny Greenwood and Colin Greenwood, Ed OBrien and Philip Selway."
}' "Radiohead")

artist_daft_punk=$(create_entity "/artists" '{
  "name": "Daft Punk",
  "description": "Daft Punk were a French electronic music duo formed in 1993 in Paris by Guy-Manuel de Homem-Christo and Thomas Bangalter. They achieved popularity in the late 1990s as part of the French house movement."
}' "Daft Punk")

artist_miles_davis=$(create_entity "/artists" '{
  "name": "Miles Davis",
  "description": "Miles Dewey Davis III was an American trumpeter, bandleader, and composer. He is among the most influential and acclaimed figures in the history of jazz and 20th-century music."
}' "Miles Davis")

artist_taylor_swift=$(create_entity "/artists" '{
  "name": "Taylor Swift",
  "description": "Taylor Alison Swift is an American singer-songwriter. Her discography spans multiple genres, and her narrative songwriting—often inspired by her personal life—has received critical praise and widespread media coverage."
}' "Taylor Swift")

echo ""
echo "=========================================="
echo "Step 3: Creating Albums"
echo "=========================================="

# Create Albums with cover images from reliable sources
# Using Wikimedia Commons and other public domain/freely licensed images

# The Beatles Albums
create_entity "/albums" "{
  \"title\": \"Abbey Road\",
  \"releaseDate\": \"1969-09-26\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg\",
  \"trackCount\": 17,
  \"catalogNumber\": \"PCS-7088\",
  \"artist\": {\"artistId\": $artist_beatles},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "Abbey Road"

create_entity "/albums" "{
  \"title\": \"Sgt. Pepper's Lonely Hearts Club Band\",
  \"releaseDate\": \"1967-06-01\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/5/50/Sgt._Pepper%27s_Lonely_Hearts_Club_Band.jpg\",
  \"trackCount\": 13,
  \"catalogNumber\": \"PMC-7027\",
  \"artist\": {\"artistId\": $artist_beatles},
  \"genres\": [{\"genreId\": $genre_rock}, {\"genreId\": $genre_pop}]
}" "Sgt. Pepper's Lonely Hearts Club Band"

create_entity "/albums" "{
  \"title\": \"Revolver\",
  \"releaseDate\": \"1966-08-05\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/c/c4/Revolver.jpg\",
  \"trackCount\": 14,
  \"catalogNumber\": \"PCS-7009\",
  \"artist\": {\"artistId\": $artist_beatles},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "Revolver"

# The Rolling Stones Albums
create_entity "/albums" "{
  \"title\": \"Sticky Fingers\",
  \"releaseDate\": \"1971-04-23\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/f/f8/StickyFingersCover.jpg\",
  \"trackCount\": 10,
  \"catalogNumber\": \"COC-59100\",
  \"artist\": {\"artistId\": $artist_stones},
  \"genres\": [{\"genreId\": $genre_rock}, {\"genreId\": $genre_blues}]
}" "Sticky Fingers"

create_entity "/albums" "{
  \"title\": \"Let It Bleed\",
  \"releaseDate\": \"1969-12-05\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/c/c0/LetItBleed.jpg\",
  \"trackCount\": 10,
  \"catalogNumber\": \"SKL-5025\",
  \"artist\": {\"artistId\": $artist_stones},
  \"genres\": [{\"genreId\": $genre_rock}, {\"genreId\": $genre_blues}]
}" "Let It Bleed"

# Pink Floyd Albums
create_entity "/albums" "{
  \"title\": \"The Dark Side of the Moon\",
  \"releaseDate\": \"1973-03-01\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png\",
  \"trackCount\": 10,
  \"catalogNumber\": \"SHVL-804\",
  \"artist\": {\"artistId\": $artist_pink_floyd},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "The Dark Side of the Moon"

create_entity "/albums" "{
  \"title\": \"The Wall\",
  \"releaseDate\": \"1979-11-30\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/1/13/PinkFloydWallCoverOriginalNoText.jpg\",
  \"trackCount\": 26,
  \"catalogNumber\": \"SHDW-411\",
  \"artist\": {\"artistId\": $artist_pink_floyd},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "The Wall"

create_entity "/albums" "{
  \"title\": \"Wish You Were Here\",
  \"releaseDate\": \"1975-09-12\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/a/a4/Pink_Floyd%2C_Wish_You_Were_Here_%281975%29.png\",
  \"trackCount\": 5,
  \"catalogNumber\": \"SHVL-814\",
  \"artist\": {\"artistId\": $artist_pink_floyd},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "Wish You Were Here"

# Queen Albums
create_entity "/albums" "{
  \"title\": \"A Night at the Opera\",
  \"releaseDate\": \"1975-11-21\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png\",
  \"trackCount\": 12,
  \"catalogNumber\": \"EMI-EMTC-103\",
  \"artist\": {\"artistId\": $artist_queen},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "A Night at the Opera"

create_entity "/albums" "{
  \"title\": \"News of the World\",
  \"releaseDate\": \"1977-10-28\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/e/ea/Queen_News_Of_The_World.png\",
  \"trackCount\": 11,
  \"catalogNumber\": \"EMI-EMA-784\",
  \"artist\": {\"artistId\": $artist_queen},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "News of the World"

# Led Zeppelin Albums
create_entity "/albums" "{
  \"title\": \"Led Zeppelin IV\",
  \"releaseDate\": \"1971-11-08\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/2/26/Led_Zeppelin_-_Led_Zeppelin_IV.jpg\",
  \"trackCount\": 8,
  \"catalogNumber\": \"SD-7208\",
  \"artist\": {\"artistId\": $artist_led_zeppelin},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "Led Zeppelin IV"

create_entity "/albums" "{
  \"title\": \"Physical Graffiti\",
  \"releaseDate\": \"1975-02-24\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/e/e3/Led_Zeppelin_-_Physical_Graffiti.jpg\",
  \"trackCount\": 15,
  \"catalogNumber\": \"SS2-200\",
  \"artist\": {\"artistId\": $artist_led_zeppelin},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "Physical Graffiti"


# Radiohead Albums
create_entity "/albums" "{
  \"title\": \"OK Computer\",
  \"releaseDate\": \"1997-05-21\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/b/ba/Radioheadokcomputer.png\",
  \"trackCount\": 12,
  \"catalogNumber\": \"CDNODATA01\",
  \"artist\": {\"artistId\": $artist_radiohead},
  \"genres\": [{\"genreId\": $genre_rock}]
}" "OK Computer"

create_entity "/albums" "{
  \"title\": \"Kid A\",
  \"releaseDate\": \"2000-10-02\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/0/02/Radioheadkida.png\",
  \"trackCount\": 10,
  \"catalogNumber\": \"CDKIDA01\",
  \"artist\": {\"artistId\": $artist_radiohead},
  \"genres\": [{\"genreId\": $genre_rock}, {\"genreId\": $genre_electronic}]
}" "Kid A"

# Daft Punk Albums
create_entity "/albums" "{
  \"title\": \"Discovery\",
  \"releaseDate\": \"2001-03-12\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/2/27/Daft_Punk_-_Discovery.png\",
  \"trackCount\": 14,
  \"catalogNumber\": \"V2-27539\",
  \"artist\": {\"artistId\": $artist_daft_punk},
  \"genres\": [{\"genreId\": $genre_electronic}, {\"genreId\": $genre_pop}]
}" "Discovery"

create_entity "/albums" "{
  \"title\": \"Random Access Memories\",
  \"releaseDate\": \"2013-05-17\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/a/a7/Random_Access_Memories.jpg\",
  \"trackCount\": 13,
  \"catalogNumber\": \"COL-88883716862\",
  \"artist\": {\"artistId\": $artist_daft_punk},
  \"genres\": [{\"genreId\": $genre_electronic}, {\"genreId\": $genre_pop}]
}" "Random Access Memories"

# Miles Davis Albums
create_entity "/albums" "{
  \"title\": \"Kind of Blue\",
  \"releaseDate\": \"1959-08-17\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/9/9c/MilesDavisKindofBlue.jpg\",
  \"trackCount\": 5,
  \"catalogNumber\": \"CL-1355\",
  \"artist\": {\"artistId\": $artist_miles_davis},
  \"genres\": [{\"genreId\": $genre_jazz}]
}" "Kind of Blue"


# Taylor Swift Albums
create_entity "/albums" "{
  \"title\": \"1989\",
  \"releaseDate\": \"2014-10-27\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/f/f6/Taylor_Swift_-_1989.png\",
  \"trackCount\": 13,
  \"catalogNumber\": \"BMR-00050\",
  \"artist\": {\"artistId\": $artist_taylor_swift},
  \"genres\": [{\"genreId\": $genre_pop}]
}" "1989"

create_entity "/albums" "{
  \"title\": \"Folklore\",
  \"releaseDate\": \"2020-07-24\",
  \"coverImageUrl\": \"https://upload.wikimedia.org/wikipedia/en/f/f8/Taylor_Swift_-_Folklore.png\",
  \"trackCount\": 16,
  \"catalogNumber\": \"REP-00092\",
  \"artist\": {\"artistId\": $artist_taylor_swift},
  \"genres\": [{\"genreId\": $genre_pop}]
}" "Folklore"

echo ""
echo "=========================================="
echo "Population Complete!"
echo "=========================================="
echo ""
echo "Summary:"
echo "- 8 Genres created"
echo "- 10 Artists created"
echo "- 17 Albums created with cover images"
echo ""
echo "You can now browse your music library at:"
echo "$BASE_URL"
echo ""
echo "API Endpoints:"
echo "- GET $API_URL/artists"
echo "- GET $API_URL/albums"
echo "- GET $API_URL/genres"
echo ""
