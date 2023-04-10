package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
public class UncheckedAppTest {

    static class Controller
    {
        Service service = new Service();
        public void request(){
            service.logic();
        }
    }

    static class Service
    {
        NetworkClient networkClient = new NetworkClient();
        Repository repository = new Repository();

        public void logic(){
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient
    {
        public void call(){
            // Unchecked exception 내던지기
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
                // catch문에서 체크 -> 언체크(런타임) 변경해서 내던지기
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        private void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException() {
        }
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException() {}
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }

    @Test
    void unchecked()
    {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(()->controller.request())
                .isInstanceOf(Exception.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e);
        }
    }
}
