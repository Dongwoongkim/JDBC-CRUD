package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    /**
     * Exception을 상속받은 클래스는 체크 예외가 됨
     */
    static class MyCheckedException extends Exception
    {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    /**
     * check 예외는 예외를 잡아서 처리하거나, 던지거나 둘 중 하나 필수
     */
    static class Service
    {
        Repository repository = new Repository();

        /**
         * 내던져진 예외 처리
         */
        public void callCatch()
        {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 예외 처리 로직
                log.info("예외 처리, message = {}",e.getMessage(),e);
            }
        }

        /**
         * 내던져진 예외 던지기
         * 체크 예외는 예외를 잡지 않고 밖으로 던지려면 throws 예외를 메소드에 필수로 선언해야함
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository
    {
        // throws 키워드로 예외 내던지기
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }

    @Test
    void checked_catch()
    {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw()
    {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }
}

