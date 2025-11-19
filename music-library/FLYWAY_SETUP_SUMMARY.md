# Flyway Migration Setup - Summary

## ✅ What Was Done

### 1. Fixed V1 Migration File
- **Created**: `V1__Create_Schema.sql` (correct Flyway naming)
- **Old file**: `V1_0__create_schema.sql` should be **DELETED** manually
- **Contains**: All table definitions (artist, genre, album, album_genre)

### 2. Created V2 Seed Data File
- **Created**: `V2__Seed_Data.sql`
- **Contains**: 
  - 8 Genres (Rock, Pop, Jazz, Blues, Electronic, Hip Hop, Classical, Country)
  - 10 Artists (Beatles, Rolling Stones, Pink Floyd, Queen, Led Zeppelin, Nirvana, Radiohead, Daft Punk, Miles Davis, Taylor Swift)
  - 22 Albums with cover images, release dates, and catalog numbers
  - Album-Genre relationships

### 3. Updated application.yaml
- **Changed**: `jpa.hibernate.ddl-auto` from `update` to `validate`
- **Changed**: `flyway.enabled` from `false` to `true`
- **Added**: `flyway.locations: classpath:db/migration`

## 📁 File Structure

```
music-library/
└── src/
    └── main/
        └── resources/
            ├── application.yaml (UPDATED)
            └── db/
                └── migration/
                    ├── V1__Create_Schema.sql (NEW - correct)
                    ├── V1_0__create_schema.sql (OLD - DELETE THIS)
                    └── V2__Seed_Data.sql (NEW)
```

## ⚠️ IMPORTANT: Manual Action Required

**You MUST delete the old file manually:**
- Delete: `src/main/resources/db/migration/V1_0__create_schema.sql`
- Keep: `src/main/resources/db/migration/V1__Create_Schema.sql`

Having both files will cause Flyway to fail!

## 🚀 How It Works Now

### First Deployment (Fresh Database):
1. Flyway runs `V1__Create_Schema.sql` → Creates all tables
2. Flyway runs `V2__Seed_Data.sql` → Inserts sample data
3. Flyway records these migrations in `flyway_schema_history` table
4. Your database now has structure + data!

### Subsequent Deployments (Railway Rebuilds):
1. Flyway checks `flyway_schema_history` table
2. Sees V1 and V2 already ran
3. **Skips them** (doesn't re-run)
4. Your data persists! ✅

### Adding More Data Later:
Create `V3__More_Data.sql` with new INSERT statements:
```sql
INSERT INTO artist (name, description) VALUES
('New Artist', 'Description here');
```

## 🔄 What Changed from Before

### Before (Hibernate ddl-auto: update):
- ❌ Data lost on Railway rebuilds
- ❌ No version control of schema changes
- ❌ Manual data re-insertion needed

### After (Flyway enabled):
- ✅ Data survives Railway rebuilds
- ✅ Schema changes version controlled
- ✅ Automatic data seeding
- ✅ Professional production approach

## 🧪 Testing Locally

1. **Delete your local database** (to test fresh setup):
   ```sql
   DROP DATABASE music_library;
   CREATE DATABASE music_library;
   ```

2. **Restart your Spring Boot app**:
   - Flyway will run V1 (create tables)
   - Flyway will run V2 (insert data)
   - Check console for: `Successfully applied 2 migrations`

3. **Verify data**:
   ```sql
   SELECT COUNT(*) FROM artist;  -- Should be 10
   SELECT COUNT(*) FROM album;   -- Should be 22
   SELECT COUNT(*) FROM genre;   -- Should be 8
   ```

## 🚂 Deploying to Railway

1. **Commit and push your changes**:
   ```bash
   git add .
   git commit -m "Enable Flyway with schema and seed data"
   git push
   ```

2. **Railway will rebuild**:
   - Flyway runs migrations automatically
   - Data is seeded on first deployment
   - Subsequent deployments preserve data

## 📊 Sample Data Included

- **8 Genres**: Rock, Pop, Jazz, Blues, Electronic, Hip Hop, Classical, Country
- **10 Artists**: Beatles, Rolling Stones, Pink Floyd, Queen, Led Zeppelin, Nirvana, Radiohead, Daft Punk, Miles Davis, Taylor Swift
- **22 Albums**: Classic albums with cover images from Wikimedia Commons

## 🔧 Troubleshooting

### If Flyway fails on startup:
1. Check console for error message
2. Common issues:
   - Old `V1_0__create_schema.sql` still exists (delete it!)
   - Database already has tables (Flyway will try to validate)
   - Syntax error in SQL files

### If you need to reset everything:
```sql
-- Drop all tables and Flyway history
DROP TABLE IF EXISTS album_genre;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS artist;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS flyway_schema_history;
```

Then restart your app - Flyway will recreate everything fresh.

## 📝 Next Steps

1. **Delete** `V1_0__create_schema.sql` manually
2. **Test locally** by dropping and recreating your database
3. **Commit and push** to Railway
4. **Verify** your data persists across Railway rebuilds

## 🎉 Benefits

- ✅ No more manual data population after deployments
- ✅ Database changes are version controlled
- ✅ Team members get same database state
- ✅ Easy to add more data (just create V3, V4, etc.)
- ✅ Professional production-ready setup
