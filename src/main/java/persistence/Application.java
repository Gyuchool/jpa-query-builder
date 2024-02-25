package persistence;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.sql.column.Columns;
import persistence.sql.column.IdColumn;
import persistence.sql.column.TableColumn;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.dialect.Database;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.mapper.GenericRowMapper;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

            Class<Person> personEntity = Person.class;
            TableColumn tableColumn = new TableColumn(personEntity);
            Dialect dialect = Database.MYSQL.createDialect();
            Columns columns = new Columns(personEntity.getDeclaredFields(), dialect);
            IdColumn idColumn = new IdColumn(personEntity.getDeclaredFields(), dialect);

            createPersonDdl(jdbcTemplate, tableColumn, columns, idColumn);
            Person person1 = new Person("username", 30, "test@test.com", 1);
            insertPerson(person1, jdbcTemplate, dialect);
            selectPerson(jdbcTemplate, dialect);
            deletePerson(person1, jdbcTemplate, dialect);

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    private static void createPersonDdl(JdbcTemplate jdbcTemplate, TableColumn tableColumn, Columns columns, IdColumn idColumn) {
        String ddl = new CreateQueryBuilder(tableColumn, columns, idColumn).build();
        jdbcTemplate.execute(ddl);
    }

    private static void insertPerson(Person person1,
                                     JdbcTemplate jdbcTemplate, Dialect dialect) {

        Person person2 = new Person("username2", "email2@test.com", 12);

        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(dialect);
        jdbcTemplate.execute(insertQueryBuilder.build(person1));
        jdbcTemplate.execute(insertQueryBuilder.build(person2));
    }

    private static void selectPerson(JdbcTemplate jdbcTemplate, Dialect dialect) {
        GenericRowMapper<Person> rowMapper = new GenericRowMapper<>(Person.class, dialect);
        SelectQueryBuilder queryBuilder = new SelectQueryBuilder(dialect);
        selectAllPerson(jdbcTemplate, rowMapper, queryBuilder);

        selectOnePerson(jdbcTemplate, rowMapper, queryBuilder);
    }

    private static void selectAllPerson(JdbcTemplate jdbcTemplate, RowMapper<Person> rowMapper,
                                        SelectQueryBuilder queryBuilder) {
        String findAll = queryBuilder.build(Person.class).findAll();
        List<Person> persons = jdbcTemplate.query(findAll, rowMapper);
    }

    private static void selectOnePerson(JdbcTemplate jdbcTemplate, RowMapper<Person> rowMapper,
                                        SelectQueryBuilder queryBuilder) {
        String selectOneQuery = queryBuilder.build(Person.class).findById(1L);
        jdbcTemplate.queryForObject(selectOneQuery, rowMapper);
    }

    private static void deletePerson(Person person,
                                     JdbcTemplate jdbcTemplate, Dialect dialect) {

        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(dialect);
        String deleteQuery = deleteQueryBuilder.build(person).deleteById(1L);
        jdbcTemplate.execute(deleteQuery);
    }
}
