package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


/**
 * SQLExceptionTranslator 사용
 * -
 */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV4_2 implements MemberRepository {
    private final DataSource dataSource;
    private final SQLExceptionTranslator exTranslator;

    public MemberRepositoryV4_2(DataSource dataSource)
    {
        this.dataSource = dataSource;
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
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
            throw exTranslator.translate("save", sql, e);
        }finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) {
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
            throw exTranslator.translate("findById",sql,e);
        }finally{
            close(con,pstmt,rs);
        }
    }

    public void update(String memberId, int money) {
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
            throw exTranslator.translate("update",sql,e);
        }finally
        {
            close(con,pstmt,null);
        }
    }

    public void delete(String memberId){
        String sql = "delete from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;

        try
        {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw exTranslator.translate("delete",sql,e);
        }finally {
            close(con,pstmt,null);
        }
    }


    private Connection getConnection() {

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
}
