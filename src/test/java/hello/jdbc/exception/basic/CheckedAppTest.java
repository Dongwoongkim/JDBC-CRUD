package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.net.ConnectException;
import java.sql.SQLException;

@Slf4j
public class CheckedAppTest {

    static class Controller
    {
        Service service = new Service();

        public void request() throws Exception {
            service.logic();
        }
    }

    static class Service
    {
        NetworkClient networkClient = new NetworkClient();
        Repository repository = new Repository();

        public void logic() throws SQLException, ConnectException {

            networkClient.call();
            repository.call();

        }
    }

    static class NetworkClient
    {
        public void call() throws ConnectException {
            // checked exception
            throw new ConnectException("연결 실패");
        }

    }

    static class Repository
    {
        public void call() throws SQLException {
            // checked exception
            throw new SQLException("ex");
        }
    }

    @Test
    void checked()
    {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }
}
