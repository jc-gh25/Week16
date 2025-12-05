package music.library.actuator;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

// The custom DB health indicator adds a clear status field for monitoring tools (Datadog, Prometheus).
@Component("dbHealth")
public class DbHealth implements HealthIndicator {

	@Autowired
	private DataSource dataSource;

	@Override
	public Health health() {
		try (Connection conn = dataSource.getConnection()) {
			// simple ping – if it succeeds we are good
			return Health.up().withDetail("status", "DB reachable").build();
		} catch (SQLException e) {
			return Health.down(e).build();
		}
	}
}
