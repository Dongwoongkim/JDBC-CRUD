package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * Repository 계층으로 부터 누수된 전환된 예외 복구 작업 추가
 */
@RequiredArgsConstructor
public class TestService implements MemberService{

    private final MemberRepository repository;

    @Override
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        Member fromMember = repository.findById(fromId);
        Member toMember = repository.findById(toId);

        repository.update(fromId,fromMember.getMoney()-money);
        validation(fromMember);
        repository.update(toId,toMember.getMoney()+money);
    }

    public void create(Member member)
    {
        try
        {
            repository.save(member);
        } catch(DuplicateKeyException e)
        {
            String retryId = getRandomNum(member.getMemberId());
            repository.save(new Member(retryId,0));
        }
    }

    private String getRandomNum(String memberId) {
        return memberId + new Random().nextInt(10000);
    }

    private void validation(Member fromMember) {
        if(fromMember.getMemberId().equals("ex"))
        {
            throw new IllegalStateException("ex");
        }
    }
}
