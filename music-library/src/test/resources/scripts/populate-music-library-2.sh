#!/usr/bin/env bash
set -euo pipefail

# ---------------------------------------------------------------
# Music Library API – Sample Data Population (bash)
# -------------------------------------------
BASE="${1:-https://javabc.up.railway.app/api}"

# Helper: POST JSON, ignore TLS cert, follow redirects
post_json() {
    local url=$1
    local json=$2
    local label=$3
    curl -k -L -s -X POST "$url" \
         -H "Content-Type: application/json" \
         -d "$json" > /dev/null
  echo "✔ $label"
}

echo "=== Music Library – Populating data ==="
echo "Target API: $BASE"
echo

# ------------------- GENRES -------------------
echo ">> Creating Genres"
post_json "$BASE/genres" '{"name":"Rock","description":"Rock music is a broad genre of popular music that originated as rock and roll in the United States in the late 1940s and early 1950s."}' "Rock"
post_json "$BASE/genres" '{"name":"Pop","description":"Pop music is a genre of popular music that originated in its modern form during the mid‑1950s."}' "Pop"
post_json "$BASE/genres" '{"name":"Jazz","description":"Jazz is a music genre that originated in the African‑American communities of New Orleans in the late 19th and early 20th centuries."}' "Jazz"
post_json "$BASE/genres" '{"name":"Blues","description":"Blues is a music genre and musical form which originated in the Deep South of the United States around the 1860s."}' "Blues"
post_json "$BASE/genres" '{"name":"Electronic","description":"Electronic music is music that employs electronic musical instruments, digital instruments, or circuitry‑based music technology."}' "Electronic"

# ------------------- ARTISTS -------------------
echo
echo ">> Creating Artists"
post_json "$BASE/artists" '{"name":"The Beatles","description":"The Beatles were an English rock band formed in Liverpool in 1960. Regarded as the most influential band of all time."}' "The Beatles"
post_json "$BASE/artists" '{"name":"The Rolling Stones","description":"The Rolling Stones are an English rock band formed in London in 1962. Active for six decades."}' "The Rolling Stones"
post_json "$BASE/artists" '{"name":"Pink Floyd","description":"Pink Floyd are an English rock band formed in London in 1965. Distinguished for their extended compositions and sonic experimentation."}' "Pink Floyd"
post_json "$BASE/artists" '{"name":"Queen","description":"Queen are a British rock band formed in London in 1970. Classic line‑up was Freddie Mercury, Brian May, Roger Taylor and John Deacon."}' "Queen"
post_json "$BASE/artists" '{"name":"Led Zeppelin","description":"Led Zeppelin were an English rock band formed in London in 1968."}' "Led Zeppelin"
post_json "$BASE/artists" '{"name":"Nirvana","description":"Nirvana was an American rock band formed in Aberdeen, Washington, in 1987."}' "Nirvana"
post_json "$BASE/artists" '{"name":"Radiohead","description":"Radiohead are an English rock band formed in Abingdon, Oxfordshire, in 1985."}' "Radiohead"
post_json "$BASE/artists" '{"name":"Daft Punk","description":"Daft Punk were a French electronic music duo formed in 1993 in Paris."}' "Daft Punk"
post_json "$BASE/artists" '{"name":"Miles Davis","description":"Miles Dewey Davis III was an American trumpeter, bandleader, and composer."}' "Miles Davis"
post_json "$BASE/artists" '{"name":"Taylor Swift","description":"Taylor Alison Swift is an American singer‑songwriter."}' "Taylor Swift"

# --------------- ALBUMS -------------------
echo
echo ">> Creating Albums (assumes IDs are sequential 1‑10 for artists, 1‑5 for genres)"
post_json "$BASE/albums" '{"title":"Abbey Road","releaseDate":"1969-09-26","artistId":1,"genreIds":[1]}' "Abbey Road"
post_json "$BASE/albums" '{"title":"Sgt. Pepper'\''s Lonely Hearts Club Band","releaseDate":"1967-06-01","artistId":1,"genreIds":[1,2]}' "Sgt. Pepper's"
post_json "$BASE/albums" '{"title":"Sticky Fingers","releaseDate":"1971-04-23","artistId":2,"genreIds":[1,4]}' "Sticky Fingers"
post_json "$BASE/albums" '{"title":"The Dark Side of the Moon","releaseDate":"1973-03-01","artistId":3,"genreIds":[1]}' "Dark Side of the Moon"
post_json "$BASE/albums" '{"title":"The Wall","releaseDate":"1979-11-30","artistId":3,"genreIds":[1]}' "The Wall"
post_json "$BASE/albums" '{"title":"A Night at the Opera","releaseDate":"1975-11-21","artistId":4,"genreIds":[1]}' "Night at the Opera"
post_json "$BASE/albums" '{"title":"Led Zeppelin IV","releaseDate":"1971-11-08","artistId":5,"genreIds":[1]}' "Led Zeppelin IV"
post_json "$BASE/albums" '{"title":"OK Computer","releaseDate":"1997-05-21","artistId":7,"genreIds":[1]}' "OK Computer"
post_json "$BASE/albums" '{"title":"Discovery","releaseDate":"2001-03-12","artistId":8,"genreIds":[5,2]}' "Discovery"
post_json "$BASE/albums" '{"title":"Random Access Memories","releaseDate":"2013-05-17","artistId":8,"genreIds":[5,2]}' "Random Access Memories"
post_json "$BASE/albums" '{"title":"Kind of Blue","releaseDate":"1959-08-17","artistId":9,"genreIds":[3]}' "Kind of Blue"
post_json "$BASE/albums" '{"title":"1989","releaseDate":"2014-10-27","artistId":10,"genreIds":[2]}' "1989"
post_json "$BASE/albums" '{"title":"Folklore","releaseDate":"2020-07-24","artistId":10,"genreIds":[2]}' "Folklore"
