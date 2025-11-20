#!/bin/bash

echo "Running tests to verify fixes..."

# Run specific test classes to check our fixes
echo "Testing AlbumControllerIT..."
mvn test -Dtest=AlbumControllerIT -q

echo "Testing ArtistControllerIT..."
mvn test -Dtest=ArtistControllerIT -q

echo "Testing GenreControllerIT..."
mvn test -Dtest=GenreControllerIT -q

echo "Testing AlbumControllerUpdateDeleteIT..."
mvn test -Dtest=AlbumControllerUpdateDeleteIT -q

echo "All tests completed!"