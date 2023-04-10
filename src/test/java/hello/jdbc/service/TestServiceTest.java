package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class TestServiceTest {

    MemberRepository memberRepository;
    TestService memberService;
    DataSource dataSource;

    @BeforeEach
    void init()
    {
        dataSource = new DriverManagerDataSource(URL,USERNAME, PASSWORD);
        memberRepository = new TestRepository(dataSource);
        memberService = new TestService(memberRepository);
    }

    @Test
    void duplicate()
    {
        memberService.create(new Member("myId",0));
        memberService.create(new Member("myId",0));
    }
}
