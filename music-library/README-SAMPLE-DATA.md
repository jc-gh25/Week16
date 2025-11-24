# Music Library API - Sample Data Population

This directory contains scripts and files to populate your Music Library API with sample data including artists, genres, and albums with working album cover image URLs.

## 📦 What's Included

- **populate-music-library.sh** - Bash script for Linux/Mac
- **populate-music-library.bat** - Batch script for Windows
- **Music-Library-Sample-Data.postman_collection.json** - Postman collection with all requests
- **README-SAMPLE-DATA.md** - This file

## 📊 Sample Data Overview

The scripts will create:
- **10 Genres**: Rock, Pop, Jazz, Blues, Electronic, Hip Hop, Classical, Country, R&B/Soul, and Classical Crossover
- **50 Artists**: A diverse collection spanning multiple genres and eras
- **103 Albums**: Classic and contemporary albums from each artist with:
  - Working cover image URLs (from iTunes Search API)
  - Release dates
  - Track counts
  - Catalog numbers
  - Genre associations
  - Diverse music styles from classical to hip-hop

## 🚀 Usage Options

### Option 1: Using Shell Script (Linux/Mac)

1. Make the script executable:
   ```bash
   chmod +x populate-music-library.sh
   ```

2. Run against your deployed API:
   ```bash
   ./populate-music-library.sh https://javabc.up.railway.app
   ```

3. Or run against localhost:
   ```bash
   ./populate-music-library.sh http://localhost:8080
   ```

### Option 2: Using Batch Script (Windows)

1. Open Command Prompt or PowerShell

2. Run against your deployed API:
   ```cmd
   populate-music-library.bat https://javabc.up.railway.app
   ```

3. Or run against localhost:
   ```cmd
   populate-music-library.bat http://localhost:8080
   ```

### Option 3: Using Postman Collection

1. **Import the Collection**:
   - Open Postman
   - Click "Import" button
   - Select `Music-Library-Sample-Data.postman_collection.json`
   - The collection will be imported with all requests organized in folders

2. **Set the Base URL**:
   - The collection has a variable `{{baseUrl}}` set to `https://javabc.up.railway.app`
   - To change it:
     - Click on the collection name
     - Go to "Variables" tab
     - Update the `baseUrl` value (e.g., to `http://localhost:8080` for local testing)

3. **Run the Collection**:
   
   **Option A: Run All Requests Automatically**
   - Right-click on the collection name
   - Select "Run collection"
   - Click "Run Music Library - Sample Data Population"
   - All requests will execute in order

   **Option B: Run Requests Manually**
   - Expand the folders in order:
     1. "1. Create Genres" - Run all 10 genre requests
     2. "2. Create Artists" - Run all 50 artist requests
     3. "3. Create Albums" - Run all 103 album requests
     4. "4. Verify Data" - Run these to check your data

4. **Verify the Data**:
   - Use the requests in the "4. Verify Data" folder
   - Or visit your API in a browser:
     - `https://javabc.up.railway.app/api/artists`
     - `https://javabc.up.railway.app/api/albums`
     - `https://javabc.up.railway.app/api/genres`

## 🖼️ Album Cover Images

All album cover images are sourced from the iTunes Search API and are stored locally in the project. The images are:
- Downloaded from iTunes Search API during data population
- Stored in: `music-library\src\main\resources\static\covers\`
- Served via the application's static resources
- High-quality album artwork for all 103 albums

The music library includes a diverse collection spanning:
- Classical music (Max Richter, Philip Glass, Andrea Bocelli)
- Blues legends (B.B. King, Muddy Waters)
- Classical crossover (Katherine Jenkins, Il Divo, Josh Groban)
- Hip-hop pioneers (Grandmaster Flash, Run-DMC)
- And many more across all 10 genres!

## 🔧 Troubleshooting

### Script Issues

**Problem**: "curl: command not found"
- **Solution**: Install curl:
  - Mac: `brew install curl`
  - Ubuntu/Debian: `sudo apt-get install curl`
  - Windows: Download from https://curl.se/windows/

**Problem**: Script fails with "Connection refused"
- **Solution**: Make sure your API is running and accessible at the specified URL

**Problem**: "Failed to extract ID"
- **Solution**: Check if the API is returning proper JSON responses. The script expects responses with `artistId`, `genreId`, or `albumId` fields.

### Postman Issues

**Problem**: Requests fail with 404
- **Solution**: Verify the `baseUrl` variable is set correctly in the collection

**Problem**: Albums fail to create
- **Solution**: Make sure you've created the genres and artists first (run folders 1 and 2 before folder 3)

**Problem**: Genre/Artist IDs don't match
- **Solution**: The album requests assume sequential IDs (1, 2, 3...). If your database has different IDs, you'll need to:
  1. Check the actual IDs by running "Get All Artists" and "Get All Genres"
  2. Update the album request bodies with the correct IDs

## 📝 Customization

### Modifying the Data

To add your own data:

1. **For Scripts**: Edit the `.sh` or `.bat` file and add new `create_entity` calls (bash) or `curl` commands (batch)

2. **For Postman**: 
   - Duplicate an existing request
   - Modify the JSON body with your data
   - Ensure artist and genre IDs are correct

### Adding More Albums

When adding albums, make sure to:
1. Use valid `artistId` values (from created artists)
2. Use valid `genreId` values (from created genres)
3. Use proper date format: `YYYY-MM-DD`
4. Include working image URLs (or leave blank)

Example album JSON:
```json
{
  "title": "Your Album Title",
  "releaseDate": "2024-01-15",
  "coverImageUrl": "https://example.com/cover.jpg",
  "trackCount": 12,
  "catalogNumber": "ABC-123",
  "artist": {"artistId": 1},
  "genres": [{"genreId": 1}, {"genreId": 2}]
}
```

## 🎯 Next Steps

After populating your database:

1. **Test the API**: Use the "Verify Data" folder in Postman to check all entities
2. **Browse the UI**: Visit your deployed app at https://javabc.up.railway.app
3. **Try Filtering**: Use query parameters like `?page=0&size=10&sort=name,asc`
4. **Update Records**: Try PUT requests to modify existing data
5. **Test Relationships**: Verify that albums show their artists and genres correctly

## 📚 API Endpoints Reference

- `POST /api/artists` - Create artist
- `GET /api/artists` - List all artists (paginated)
- `GET /api/artists/{id}` - Get specific artist
- `PUT /api/artists/{id}` - Update artist
- `DELETE /api/artists/{id}` - Delete artist

- `POST /api/albums` - Create album
- `GET /api/albums` - List all albums (paginated)
- `GET /api/albums/{id}` - Get specific album
- `PUT /api/albums/{id}` - Update album
- `DELETE /api/albums/{id}` - Delete album

- `POST /api/genres` - Create genre
- `GET /api/genres` - List all genres (paginated)
- `GET /api/genres/{id}` - Get specific genre
- `PUT /api/genres/{id}` - Update genre
- `DELETE /api/genres/{id}` - Delete genre

## 💡 Tips

- **Run in Order**: Always create genres and artists before albums (albums reference them)
- **Check IDs**: If using the Postman collection manually, verify IDs match between requests
- **Use Collection Runner**: For fastest population, use Postman's Collection Runner feature
- **Backup Data**: Before running scripts, consider backing up your database if it has existing data
- **Test Locally First**: Try the scripts on localhost before running against production

## 🆘 Support

If you encounter issues:
1. Check that your API is running and accessible
2. Verify the base URL is correct
3. Look at the API response for error messages
4. Check the console/terminal output for detailed error information
5. Ensure your database is properly configured and accessible

---

**Enjoy your populated Music Library API!** 🎵🎸🎹
