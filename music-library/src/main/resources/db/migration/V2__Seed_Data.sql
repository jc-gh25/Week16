-- =========================================
-- V2: Seed Data for Music Library
-- =========================================
-- This migration adds sample genres, artists, and albums to the database
-- Data will be inserted only once when this migration runs

-- =========================================
-- Insert Genres
-- =========================================
INSERT INTO genre (name, description) VALUES
('Rock', 'Rock music is a broad genre of popular music that originated as rock and roll in the United States in the late 1940s and early 1950s.'),
('Pop', 'Pop music is a genre of popular music that originated in its modern form during the mid-1950s.'),
('Jazz', 'Jazz is a music genre that originated in the African-American communities of New Orleans in the late 19th and early 20th centuries.'),
('Blues', 'Blues is a music genre and musical form which originated in the Deep South of the United States around the 1860s.'),
('Electronic', 'Electronic music is music that employs electronic musical instruments, digital instruments, or circuitry-based music technology.'),
('Hip Hop', 'Hip hop music or hip-hop music, also known as rap music, is a genre of popular music developed in the United States.'),
('Classical', 'Classical music generally refers to the formal musical tradition of the Western world, considered to be distinct from Western folk music or popular music traditions.'),
('Country', 'Country music is a genre of popular music that originated in the Southern United States in the early 1920s.');

-- =========================================
-- Insert Artists
-- =========================================
INSERT INTO artist (name, description) VALUES
('The Beatles', 'The Beatles were an English rock band formed in Liverpool in 1960. Regarded as the most influential band of all time, they were integral to the development of 1960s counterculture.'),
('The Rolling Stones', 'The Rolling Stones are an English rock band formed in London in 1962. Active for six decades, they are one of the most popular and enduring bands of the rock era.'),
('Pink Floyd', 'Pink Floyd are an English rock band formed in London in 1965. Gaining an early following as one of the first British psychedelic groups, they were distinguished for their extended compositions, sonic experimentation, philosophical lyrics and elaborate live shows.'),
('Queen', 'Queen are a British rock band formed in London in 1970. Their classic line-up was Freddie Mercury, Brian May, Roger Taylor and John Deacon.'),
('Led Zeppelin', 'Led Zeppelin were an English rock band formed in London in 1968. The group consisted of vocalist Robert Plant, guitarist Jimmy Page, bassist/keyboardist John Paul Jones, and drummer John Bonham.'),
('Nirvana', 'Nirvana was an American rock band formed in Aberdeen, Washington, in 1987. Founded by lead singer and guitarist Kurt Cobain and bassist Krist Novoselic, the band went through a succession of drummers before recruiting Dave Grohl in 1990.'),
('Radiohead', 'Radiohead are an English rock band formed in Abingdon, Oxfordshire, in 1985. The band consists of Thom Yorke, brothers Jonny Greenwood and Colin Greenwood, Ed OBrien and Philip Selway.'),
('Daft Punk', 'Daft Punk were a French electronic music duo formed in 1993 in Paris by Guy-Manuel de Homem-Christo and Thomas Bangalter. They achieved popularity in the late 1990s as part of the French house movement.'),
('Miles Davis', 'Miles Dewey Davis III was an American trumpeter, bandleader, and composer. He is among the most influential and acclaimed figures in the history of jazz and 20th-century music.'),
('Taylor Swift', 'Taylor Alison Swift is an American singer-songwriter. Her discography spans multiple genres, and her narrative songwriting—often inspired by her personal life—has received critical praise and widespread media coverage.');

-- =========================================
-- Insert Albums
-- =========================================

-- The Beatles Albums
INSERT INTO album (title, release_date, cover_image_url, track_count, catalog_number, artist_id) VALUES
('Abbey Road', '1969-09-26', 'https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg', 17, 'PCS-7088', 1),
('Sgt. Pepper''s Lonely Hearts Club Band', '1967-06-01', 'https://upload.wikimedia.org/wikipedia/en/5/50/Sgt._Pepper%27s_Lonely_Hearts_Club_Band.jpg', 13, 'PMC-7027', 1),
('Revolver', '1966-08-05', 'https://upload.wikimedia.org/wikipedia/en/c/c4/Revolver.jpg', 14, 'PCS-7009', 1);

-- The Rolling Stones Albums
INSERT INTO album (title, release_date, cover_image_url, track_count, catalog_number, artist_id) VALUES
('Sticky Fingers', '1971-04-23', 'https://upload.wikimedia.org/wikipedia/en/f/f8/StickyFingersCover.jpg', 10, 'COC-59100', 2),
('Let It Bleed', '1969-12-05', 'https://upload.wikimedia.org/wikipedia/en/c/c0/LetItBleed.jpg', 10, 'SKL-5025', 2);

-- Pink Floyd Albums
INSERT INTO album (title, release_date, cover_image_url, track_count, catalog_number, artist_id) VALUES
('The Dark Side of the Moon', '1973-03-01', 'https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png', 10, 'SHVL-804', 3),
('The Wall', '1979-11-30', 'https://upload.wikimedia.org/wikipedia/en/1/13/PinkFloydWallCoverOriginalNoText.jpg', 26, 'SHDW-411', 3),
('Wish You Were Here', '1975-09-12', 'https://upload.wikimedia.org/wikipedia/en/a/a4/Pink_Floyd%2C_Wish_You_Were_Here_%281975%29.png', 5, 'SHVL-814', 3);

-- Queen Albums
INSERT INTO album (title, release_date, cover_image_url, track_count, catalog_number, artist_id) VALUES
('A Night at the Opera', '1975-11-21', 'https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png', 12, 'EMI-EMTC-103', 4),
('News of the World', '1977-10-28', 'https://upload.wikimedia.org/wikipedia/en/e/ea/Queen_News_Of_The_World.png', 11, 'EMI-EMA-784', 4);

-- Led Zeppelin Albums
INSERT INTO album (title, release_date, cover_image_url, track_count, catalog_number, artist_id) VALUES
('Led Zeppelin IV', '1971-11-08', 'https://upload.wikimedia.org/wikipedia/en/2/26/Led_Zeppelin_-_Led_Zeppelin_IV.jpg', 8, 'SD-7208', 5),
('Physical Graffiti', '1975-02-24', 'https://upload.wikimedia.org/wikipedia/en/e/e3/Led_Zeppelin_-_Physical_Graffiti.jpg', 15, 'SS2-200', 5);


-- Radiohead Albums
INSERT INTO album (title, release_date, cover_image_url, track_count, catalog_number, artist_id) VALUES
('OK Computer', '1997-05-21', 'https://upload.wikimedia.org/wikipedia/en/b/ba/Radioheadokcomputer.png', 12, 'CDNODATA01', 7),
('Kid A', '2000-10-02', 'https://upload.wikimedia.org/wikipedia/en/0/02/Radioheadkida.png', 10, 'CDKIDA01', 7);

-- Daft Punk Albums
INSERT INTO album (title, release_date, cover_image_url, track_count, catalog_number, artist_id) VALUES
('Discovery', '2001-03-12', 'https://upload.wikimedia.org/wikipedia/en/2/27/Daft_Punk_-_Discovery.png', 14, 'V2-27539', 8),
('Random Access Memories', '2013-05-17', 'https://upload.wikimedia.org/wikipedia/en/a/a7/Random_Access_Memories.jpg', 13, 'COL-88883716862', 8);

-- Miles Davis Albums
INSERT INTO album (title, release_date, cover_image_url, track_count, catalog_number, artist_id) VALUES
('Kind of Blue', '1959-08-17', 'https://upload.wikimedia.org/wikipedia/en/9/9c/MilesDavisKindofBlue.jpg', 5, 'CL-1355', 9);

-- Taylor Swift Albums
INSERT INTO album (title, release_date, cover_image_url, track_count, catalog_number, artist_id) VALUES
('1989', '2014-10-27', 'https://upload.wikimedia.org/wikipedia/en/f/f6/Taylor_Swift_-_1989.png', 13, 'BMR-00050', 10),
('Folklore', '2020-07-24', 'https://upload.wikimedia.org/wikipedia/en/f/f8/Taylor_Swift_-_Folklore.png', 16, 'REP-00092', 10);

-- =========================================
-- Insert Album-Genre Relationships
-- =========================================

-- Beatles albums (Rock, Pop)
INSERT INTO album_genre (album_id, genre_id) VALUES
(1, 1),  -- Abbey Road -> Rock
(2, 1),  -- Sgt. Pepper -> Rock
(2, 2),  -- Sgt. Pepper -> Pop
(3, 1);  -- Revolver -> Rock

-- Rolling Stones albums (Rock, Blues)
INSERT INTO album_genre (album_id, genre_id) VALUES
(4, 1),  -- Sticky Fingers -> Rock
(4, 4),  -- Sticky Fingers -> Blues
(5, 1),  -- Let It Bleed -> Rock
(5, 4);  -- Let It Bleed -> Blues

-- Pink Floyd albums (Rock)
INSERT INTO album_genre (album_id, genre_id) VALUES
(6, 1),  -- Dark Side -> Rock
(7, 1),  -- The Wall -> Rock
(8, 1);  -- Wish You Were Here -> Rock

-- Queen albums (Rock)
INSERT INTO album_genre (album_id, genre_id) VALUES
(9, 1),   -- A Night at the Opera -> Rock
(10, 1);  -- News of the World -> Rock

-- Led Zeppelin albums (Rock)
INSERT INTO album_genre (album_id, genre_id) VALUES
(11, 1),  -- Led Zeppelin IV -> Rock
(12, 1);  -- Physical Graffiti -> Rock


-- Radiohead albums (Rock, Electronic)
INSERT INTO album_genre (album_id, genre_id) VALUES
(15, 1),  -- OK Computer -> Rock
(16, 1),  -- Kid A -> Rock
(16, 5);  -- Kid A -> Electronic

-- Daft Punk albums (Electronic, Pop)
INSERT INTO album_genre (album_id, genre_id) VALUES
(17, 5),  -- Discovery -> Electronic
(17, 2),  -- Discovery -> Pop
(18, 5),  -- Random Access Memories -> Electronic
(18, 2);  -- Random Access Memories -> Pop

-- Miles Davis albums (Jazz)
INSERT INTO album_genre (album_id, genre_id) VALUES
(19, 3);  -- Kind of Blue -> Jazz

-- Taylor Swift albums (Pop)
INSERT INTO album_genre (album_id, genre_id) VALUES
(21, 2),  -- 1989 -> Pop
(22, 2);  -- Folklore -> Pop
