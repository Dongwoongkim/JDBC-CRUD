package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * 트랜잭션 동기화 매니저 사용
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV3 {
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

    public Member save(Member member) throws SQLException
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
            log.error("db error",e);
            throw e;
        }finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            rs = pstmt.executeQuery();

            if(rs.next())
            {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }
            else
            {
                throw new NoSuchElementException("member not found memberId="+memberId);
            }
        } catch (SQLException e) {
            throw e;
        }finally{
            close(con,pstmt,rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try
        {
            con = getConnection();
            pstmt=con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}",resultSize);

        } catch (SQLException e) {
            throw e;
        }finally
        {
            close(con,pstmt,null);
        }
    }


    public void delete(String memberId) throws SQLException {

        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try
        {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int i = pstmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        }finally {
            close(con,pstmt,null);
        }
    }
}
