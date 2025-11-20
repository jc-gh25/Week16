#!/bin/bash

echo "Running JUnit tests to verify fixes..."

# Navigate to project directory
cd "C:\Users\user1\Desktop\Back End Software Dev\PortableGit\projects\Week16\music-library"

# Run specific test classes that we fixed
echo "Testing AlbumControllerIT..."
mvn test -Dtest=AlbumControllerIT

echo "Testing ArtistControllerIT..."
mvn test -Dtest=ArtistControllerIT

echo "Testing GenreControllerIT..."
mvn test -Dtest=GenreControllerIT

echo "Testing AlbumControllerUpdateDeleteIT..."
mvn test -Dtest=AlbumControllerUpdateDeleteIT

echo "All tests completed!"