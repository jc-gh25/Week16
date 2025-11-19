# Database Reset Endpoint

## Overview
A new REST endpoint has been created to delete all data from the Music Library database. This is useful for testing and development purposes.

## Endpoint Details

**URL:** `DELETE /api/reset`

**Description:** Deletes all albums, artists, and genres from the database in the correct order to avoid foreign key constraint violations.

**Response:** 
```json
{
  "message": "Database has been reset successfully. All albums, artists, and genres have been deleted."
}
```

## Implementation

### 1. DatabaseResetService
**Location:** `src/main/java/music/library/service/DatabaseResetService.java`

A new service class that handles the database reset logic:
- Deletes albums first (they have foreign keys to artists and genres)
- Deletes artists second
- Deletes genres last
- Uses `@Transactional` to ensure all deletions happen atomically

### 2. Controller Endpoint
**Location:** `src/main/java/music/library/controller/MusicLibraryController.java`

Added a new endpoint at the end of the controller:
- Uses `@DeleteMapping("/reset")` 
- Includes OpenAPI/Swagger documentation with warnings
- Returns a confirmation message as JSON

## Usage

### Using cURL:
```bash
curl -X DELETE http://localhost:8080/api/reset
```

### Using Postman:
1. Create a new request
2. Set method to DELETE
3. Set URL to `{{baseUrl}}/api/reset`
4. Send the request

### Response Example:
```json
{
  "message": "Database has been reset successfully. All albums, artists, and genres have been deleted."
}
```

## Important Notes

⚠️ **WARNING:** This endpoint permanently deletes ALL data from the database. This action cannot be undone.

- **Use only in development/testing environments**
- Consider adding authentication/authorization in production
- The deletion order is critical to avoid foreign key constraint violations
- All operations are wrapped in a transaction for data consistency

## Testing

After calling this endpoint:
1. Verify all albums are deleted: `GET /api/albums`
2. Verify all artists are deleted: `GET /api/artists`
3. Verify all genres are deleted: `GET /api/genres`

All should return empty paginated results.
