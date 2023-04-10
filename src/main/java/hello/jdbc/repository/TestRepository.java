package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * DB에서 발생하는 오류코드를 이용해 예외 전환
 */
@RequiredArgsConstructor
@Slf4j
public class TestRepository implements MemberRepository {

    private final DataSource dataSource;

    private Connection getConnection() throws SQLException {
        // 트랜잭션 동기화 매니저를 사용하려면 DataSourceUtils를 통해 커넥션얻어야함.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection = {}, class = {}",con,con.getClass());
        return con;
    }

    private void close(Connection con, Statement stmt, ResultSet rs)
    {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        // 트랜잭션 동기화 매니저를 사용하려면 DataSourceUtils 사용해야 함.
        // 트랜잭션을 사용하기 위해 동기화된 커넥션은 닫지 않고 유지.
        // 트랜잭션 동기화 매니저가 관리하는 커넥션이 없는 경우엔 해당 커넥션 닫음.
        DataSourceUtils.releaseConnection(con,dataSource);
    }

    public Member save(Member member)
    {
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try
        {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            if(e.getErrorCode()==23505)
            {
                throw new MyDuplicateKeyException(e);
            }
            throw new MyDbException(e);
        }finally {
            close(con, pstmt, null);
        }
    }

    @Override
    public Member findById(String memberId) {
        return null;
    }

    @Override
    public void update(String memberId, int money) {

    }

    @Override
    public void delete(String memberId) {

    }

}
