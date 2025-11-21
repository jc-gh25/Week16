package music.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for resetting the database to a clean state.
 * Deletes all data from tables and resets auto-increment sequences.
 */
@Service
public class DatabaseResetService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Resets the database by deleting all data from tables and resetting auto-increment sequences.
     * 
     * The order of deletion is critical to avoid foreign key constraint violations:
     * 1. Delete from album_genre first (junction table with foreign keys)
     * 2. Delete from Album (depends on Artist and Genre)
     * 3. Delete from Artist (parent table)
     * 4. Delete from Genre (parent table)
     * 
     * Then reset auto-increment sequences in the same order.
     * 
     * For MySQL, AUTO_INCREMENT is reset using: ALTER TABLE table_name AUTO_INCREMENT = 1
     */
    @Transactional
    public void resetDatabase() {
        try {
            // Disable foreign key checks to allow deletion without constraint violations
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

            // Delete data from tables in reverse order of dependencies
            // album_genre is the junction table, delete it first
            jdbcTemplate.execute("DELETE FROM album_genre");
            // Album has foreign keys to Artist and Genre, so delete it next
            jdbcTemplate.execute("DELETE FROM album");
            jdbcTemplate.execute("DELETE FROM artist");
            jdbcTemplate.execute("DELETE FROM genre");

            // Reset auto-increment sequences for MySQL
            // This ensures the next created entity will have ID = 1
            // Note: album_genre likely doesn't have auto-increment (it's a junction table)
            jdbcTemplate.execute("ALTER TABLE album AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE artist AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE genre AUTO_INCREMENT = 1");

            // Re-enable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

            System.out.println("✓ Database reset successfully. All tables cleared and auto-increment sequences reset to 1.");

        } catch (Exception e) {
            System.err.println("✗ Error resetting database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to reset database", e);
        }
    }
}