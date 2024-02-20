package persistence.sql.ddl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.sql.dialect.Database;

import static org.assertj.core.api.Assertions.assertThat;

class CreateDdlTest {

    @DisplayName("Person 클래스의 DDL을 생성한다.")
    @Test
    void createDdl() {
        QueryBuilder queryBuilder = new CreateDdl();
        String ddl = queryBuilder.generate(Person.class, Database.MYSQL);

        assertThat(ddl).isEqualTo("create table users (id bigint auto_increment primary key, nick_name varchar(255), old integer, email varchar(255) not null)");
    }
}
