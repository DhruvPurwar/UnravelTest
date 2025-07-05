package cloud.unravel.com;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.pool.HikariPoolMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class DatabaseManager {

    @Autowired
    private DataSource dataSource;

    private HikariDataSource hikariDataSource;

    @PostConstruct
    public void init() {
        if (dataSource instanceof HikariDataSource) {
            hikariDataSource = (HikariDataSource) dataSource;
            startMonitoring();
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startMonitoring() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                monitorConnectionPool();
            }
        }, 0, 10000); // Run every 10 seconds
    }

    private void monitorConnectionPool() {
        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();
        HikariConfigMXBean configMXBean = hikariDataSource.getHikariConfigMXBean();

        int activeConnections = poolMXBean.getActiveConnections();
        int idleConnections = poolMXBean.getIdleConnections();
        int totalConnections = poolMXBean.getTotalConnections();
        int threadsAwaitingConnection = poolMXBean.getThreadsAwaitingConnection();

        System.out.println("Active Connections: " + activeConnections);
        System.out.println("Idle Connections: " + idleConnections);
        System.out.println("Total Connections: " + totalConnections);
        System.out.println("Threads Awaiting Connection: " + threadsAwaitingConnection);

        // Custom logic to log warnings or adjust settings
        if (threadsAwaitingConnection > configMXBean.getMaximumPoolSize() / 2) {
            System.out.println("Warning: High number of threads awaiting connections!");
        }
    }
}
