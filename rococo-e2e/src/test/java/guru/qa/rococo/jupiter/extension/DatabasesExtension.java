package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.jdbc.Connections;
import guru.qa.rococo.data.jdbc.DataSources;
import guru.qa.rococo.data.jpa.EntityManagers;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabasesExtension implements SuiteExtension {

  private static final Config CFG = Config.getInstance();
  private final JdbcTemplate authJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
  private final JdbcTemplate userdataJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
  private final JdbcTemplate artistJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.artistJdbcUrl()));
  private final JdbcTemplate museumJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.museumJdbcUrl()));
  private final JdbcTemplate paintingJdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.paintingJdbcUrl()));

  @Override
  public void beforeSuite(ExtensionContext context) {
    artistJdbcTemplate.execute("TRUNCATE TABLE artist;");
    userdataJdbcTemplate.execute("TRUNCATE TABLE `user`;");
    authJdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
    authJdbcTemplate.execute("TRUNCATE TABLE authority;");
    authJdbcTemplate.execute("TRUNCATE TABLE `user`;");
    authJdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
    museumJdbcTemplate.execute("TRUNCATE TABLE museum;");
    paintingJdbcTemplate.execute("TRUNCATE TABLE painting;");
  }

  @Override
  public void afterSuite() {
    Connections.closeAllConnections();
    EntityManagers.closeAllEmfs();
  }
}
