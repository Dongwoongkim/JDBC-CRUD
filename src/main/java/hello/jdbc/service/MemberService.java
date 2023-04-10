package hello.jdbc.service;

import hello.jdbc.domain.Member;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

public interface MemberService {

    public void accountTransfer(String fromId, String toId, int money);

}
