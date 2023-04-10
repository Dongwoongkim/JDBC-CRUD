package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    /**
     * RuntimeException을 상속받은 예외는 Unchecked 예외
     */
    static class MyUncheckedException extends RuntimeException
    {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * Unchecked 예외는 예외를 잡거나, 던지지 않아도 됨.
     * 잡지 않으면 자동으로 밖으로 던짐
     */
    static class Service
    {
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외 잡아서 처리
         */
        public void callCatch()
        {
            try
            {
                repository.call();
            }catch(MyUncheckedException e)
            {
                log.info("예외 처리, message = {} ", e.getMessage(), e);
            }
        }

        /**
         * 던질 때는 Chekced 예외와 달리 메소드에 throws 를 선언해 주지 않아도 됨.
         */
        public void callThrow() throws MyUncheckedException
        {
            repository.call();
        }
    }

    static class Repository
    {
        public void call()
        {
            throw new MyUncheckedException("ex");
        }
    }

    @Test
    void unchecked_catch()
    {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_Throw()
    {
        Service service = new Service();
        Assertions.assertThatThrownBy(()-> service.callThrow())
                        .isInstanceOf(MyUncheckedException.class);
    }
}
