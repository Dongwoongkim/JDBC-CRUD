package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // 트랜잭션 시작
        // 커넥션 생성
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try
        {
            buisnesslogic(fromId, toId, money);
            transactionManager.commit(status);
        } catch (Exception e) {
            log.error("error detected!");
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        } // release는 transactionManager가 알아서 해줌.
    }
    private void buisnesslogic(String fromId, String toId, int money) throws SQLException {
        // 비즈니스 로직
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney()- money);
        validation(toMember);
        memberRepository.update(toId,toMember.getMoney()+ money);
    }

    private void validation(Member toMember)
    {
        if(toMember.getMemberId().equals("ex"))
        {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
