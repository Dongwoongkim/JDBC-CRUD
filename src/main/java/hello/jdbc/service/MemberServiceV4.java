package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Random;

/**
 *  서비스계층에 비즈니스 로직만 깔끔하게.
 *  - 예외 누수문제 해결
 *  SQLException 제거 (Repostiory에서 Checked Exception -> Unchecked Exception)
 *  MemberRepository 인터페이스 의존
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 implements MemberService{
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void accountTransfer(String fromId, String toId, int money) {
        buisnesslogic(fromId,toId,money);
    }
    private void buisnesslogic(String fromId, String toId, int money) {
        // 비즈니스 로직
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney()- money);
        validation(toMember);
        memberRepository.update(toId,toMember.getMoney()+ money);
    }

    private String getRandomNum(String memberId) {
        return memberId + new Random().nextInt(10000);
    }
    private void validation(Member toMember)
    {
        if(toMember.getMemberId().equals("ex"))
        {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
