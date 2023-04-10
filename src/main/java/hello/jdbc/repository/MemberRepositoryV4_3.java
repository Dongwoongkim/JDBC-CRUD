package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;


/**
 * JdbcTemplate 사용 - 커넥션 조회, 커넥션 동기화, pstmt 생성 / 파라미터 바인딩 ,
 * 결과 바인딩 , 예외발생시 스프링 예외 변환기 실행, 리소스 정리 자동화
 *
 */
@Slf4j
public class MemberRepositoryV4_3 implements MemberRepository {
//    private final SQLExceptionTranslator exTranslator;
    private final JdbcTemplate template; // 커넥션 수신, 파라미터 조회, result 바인딩 , 등등..

    public MemberRepositoryV4_3(DataSource dataSource)
    {
        this.template = new JdbcTemplate(dataSource);
    }

    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values(?,?)";
        template.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }

    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        return template.queryForObject(sql, memberRowMapper(), memberId);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };
    }

    public void update(String memberId, int money) {
        String sql = "update member set money = ? where member_id = ?";
        template.update(sql, money, memberId);
    }

    public void delete(String memberId) {
        String sql = "delete from member where member_id = ?";
        template.update(sql, memberId);
    }
}

