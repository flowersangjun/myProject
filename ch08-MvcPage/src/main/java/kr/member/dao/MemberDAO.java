package kr.member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import kr.member.vo.MemberVO;
import kr.util.DBUtil;

public class MemberDAO {
	//싱글턴 패턴
		private static MemberDAO instance = new MemberDAO();
		
		public static MemberDAO getInstance() {
			return instance;
		}
		
		private MemberDAO() {}
		
		//회원가입
		public void insertMember(MemberVO member) throws Exception{
			Connection conn = null;
			PreparedStatement pstmt = null;
			PreparedStatement pstmt2 = null;
			PreparedStatement pstmt3 = null;
			ResultSet rs = null;
			String sql = null;
			int num = 0; //시퀀스 번호 저장
			
			try {
				//JDBC 수행 1,2단계 : 커넥션풀로부터 커넥션을 할당
				conn=DBUtil.getConnection();
				//오토 커밋 해제
				conn.setAutoCommit(false);
				
				//회원번호(mem_num) 생성
				sql = "select zmember_seq.nextval from dual";
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				//JDBC 수행 4단계
				rs = pstmt.executeQuery();
				if(rs.next()) {
					num = rs.getInt(1);
				}
				
				//zmember에 데이터 저장
				sql = "insert into zmember (mem_num,id) values(?,?)";
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt2 = conn.prepareStatement(sql);
				//?에 데이터 바인딩
				pstmt2.setInt(1, num);
				pstmt2.setString(2, member.getId());
				//JDBC 수행 4단계 : SQL문 실행
				pstmt2.executeUpdate();
				
				//zmember_detail에 데이터 저장
				sql = "insert into zmember_detail(mem_num,name,passwd,phone,email,zipcode,address1,address2) values (?,?,?,?,?,?,?,?)";
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt3 = conn.prepareStatement(sql);
				//?에 데이터 바인딩
				pstmt3.setInt(1, num);
				pstmt3.setString(2, member.getName());
				pstmt3.setString(3, member.getPasswd());
				pstmt3.setString(4, member.getPhone());
				pstmt3.setString(5, member.getEmail());
				pstmt3.setString(6, member.getZipcode());
				pstmt3.setString(7, member.getAddress1());
				pstmt3.setString(8, member.getAddress2());
				//JDBC 수행 4단계 : SQL문 실행
				pstmt3.executeUpdate();
				
				
				//SQL 실행시 모두 성공하면 commit
				conn.commit();
			}catch (Exception e) {
				// TODO: handle exception
				//SQL문이 하나라도 실패하면 rollback
				conn.rollback();
				throw new Exception(e);
			}finally {
				//자원정리
				DBUtil.executeClose(null,pstmt3,null);
				DBUtil.executeClose(null,pstmt2,null);
				DBUtil.executeClose(rs,pstmt,conn);
			}
		}
		
		//ID 중복 체크 및 로그인 처리
		public MemberVO checkMember(String id) throws Exception{
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			MemberVO member = null;
			String sql = null;
			
			try {
				//JDBC 수행 1,2단계 : 커넥션풀로부터 커넥션을 할당
				conn=DBUtil.getConnection();
				
				//회원번호(mem_num) 생성
				sql = "select * from zmember m left outer join zmember_detail d on m.mem_num=d.mem_num where m.id=?";
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				//?에 데이터 바인딩
				pstmt.setString(1, id);
				//JDBC 수행 4단계
				rs = pstmt.executeQuery();
				if(rs.next()) {
					member = new MemberVO();
					member.setMem_num(rs.getInt("mem_num"));
					member.setId(rs.getString("id"));
					member.setAuth(rs.getInt("auth"));
					member.setPasswd(rs.getString("passwd"));
					member.setPhoto(rs.getString("photo"));
					member.setEmail(rs.getString("email"));
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				throw new Exception(e);
			}finally {
				//자원정리
				DBUtil.executeClose(rs,pstmt,conn);
			}
			return member;
		}
		
		//회원상세 정보
		public MemberVO getMember(int mem_num) throws Exception{
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			MemberVO member = null;
			String sql = null;
			
			try {
				//JDBC 수행 1,2단계 : 커넥션풀로부터 커넥션을 할당
				conn=DBUtil.getConnection();
				//회원번호(mem_num) 생성
				sql = "select * from zmember m join zmember_detail d on m.mem_num = d.mem_num where m.mem_num = ?";
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				//?에 데이터 바인딩
				pstmt.setInt(1, mem_num);
				//JDBC 수행 4단계
				rs = pstmt.executeQuery();
				if(rs.next()) {
					member = new MemberVO();
					member.setMem_num(rs.getInt("mem_num"));
					member.setId(rs.getString("id"));
					member.setAuth(rs.getInt("auth"));
					member.setPasswd(rs.getString("passwd"));
					member.setName(rs.getString("name"));
					member.setPhone(rs.getString("phone"));
					member.setEmail(rs.getString("email"));
					member.setZipcode(rs.getString("zipcode"));
					member.setAddress1(rs.getString("address1"));
					member.setAddress2(rs.getString("address2"));
					member.setPhoto(rs.getString("photo"));
					member.setReg_date(rs.getDate("reg_date"));
					member.setModify_date(rs.getDate("modify_date"));
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				throw new Exception(e);
			}finally {
				//자원정리
				DBUtil.executeClose(rs,pstmt,conn);
			}
			
			return member;
		}
		//회원정보 수정
		public void updateMember(MemberVO member) throws Exception{
			Connection conn = null;
			PreparedStatement pstmt = null;
			String sql = null;
			
			try {
				//JDBC 수행 1,2단계 : 커넥션풀로부터 커넥션을 할당
				conn=DBUtil.getConnection();
				//회원번호(mem_num) 생성
				sql = "update zmember_detail set name=? , phone=?, email=?, zipcode=?, address1=?, address2=?, modify_date=sysdate where mem_num=?";
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				//?에 데이터 바인딩
				pstmt.setString(1, member.getName());
				pstmt.setString(2, member.getPhone());
				pstmt.setString(3, member.getEmail());
				pstmt.setString(4, member.getZipcode());
				pstmt.setString(5, member.getAddress1());
				pstmt.setString(6, member.getAddress2());
				pstmt.setInt(7, member.getMem_num());
				//JDBC 수행 4단계
				pstmt.executeUpdate();
				
			}catch (Exception e) {
				// TODO: handle exception
				throw new Exception(e);
			}finally {
				//자원정리
				DBUtil.executeClose(null,pstmt,conn);
			}
		}
		//비밀번호 수정
		public void updatePassword(String passwd,int mem_num) throws Exception{
			Connection conn = null;
			PreparedStatement pstmt = null;
			String sql = null;
			
			try {
				//JDBC 수행 1,2단계 : 커넥션풀로부터 커넥션을 할당
				conn=DBUtil.getConnection();
				//회원번호(mem_num) 생성
				sql = "update zmember_detail set passwd=? where mem_num=?";
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				//?에 데이터 바인딩
				pstmt.setString(1, passwd);
				pstmt.setInt(2, mem_num);
				//JDBC 수행 4단계
				pstmt.executeUpdate();
				
			}catch (Exception e) {
				// TODO: handle exception
				throw new Exception(e);
			}finally {
				//자원정리
				DBUtil.executeClose(null,pstmt,conn);
			}
		}
		//프로필 사진 수정
		public void updateMyPhoto(String photo, int mem_num) throws Exception{
			Connection conn = null;
			PreparedStatement pstmt = null;
			String sql = null;
			
			try {
				//JDBC 수행 1,2단계 : 커넥션풀로부터 커넥션을 할당
				conn=DBUtil.getConnection();
				//회원번호(mem_num) 생성
				sql = "update zmember_detail set photo=? where mem_num=?";
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				//?에 데이터 바인딩
				pstmt.setString(1, photo);
				pstmt.setInt(2, mem_num);
				//JDBC 수행 4단계
				pstmt.executeUpdate();
				
			}catch (Exception e) {
				// TODO: handle exception
				throw new Exception(e);
			}finally {
				//자원정리
				DBUtil.executeClose(null,pstmt,conn);
			}
		}
		//회원탈퇴(회원정보 삭제)
		public void deleteMember(int mem_num) throws Exception{
			Connection conn = null;
			PreparedStatement pstmt = null;
			PreparedStatement pstmt2 = null;
			String sql = null;
			
			try {
				//JDBC 수행 1,2단계 : 커넥션풀로부터 커넥션을 할당
				conn=DBUtil.getConnection();
				//auto commit 해제
				conn.setAutoCommit(false);
				
				//zmember의 auth 값 변경
				sql = "update zmember set auth=0 where mem_num=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, mem_num);
				pstmt.executeUpdate();
				//zmember_detail의 레코드 삭제
				//회원번호(mem_num) 생성
				sql = "delete from zmember_detail where mem_num=?";
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt2 = conn.prepareStatement(sql);
				//?에 데이터 바인딩
				pstmt2.setInt(1, mem_num);
				//JDBC 수행 4단계
				pstmt2.executeUpdate();
				
				//모든 SQL문의 실행이 성공하면 커밋
				conn.commit();
			}catch (Exception e) {
				// TODO: handle exception
				//SQL문이 하나라도 실패하면 롤백
				conn.rollback();
				throw new Exception(e);
			}finally {
				//자원정리
				DBUtil.executeClose(null,pstmt2,null);
				DBUtil.executeClose(null,pstmt,conn);
			}
		}
		//관리자
		//전체글 개수(검색글 개수)
		public int getMemberCountByAdmin(String keyfield,String keyword) throws Exception{
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql = null;
			String sub_sql = "";
			int count = 0;
			
			try {
				//JDBC 수행 1,2단계 : 커넥션풀로부터 커넥션을 할당
				conn=DBUtil.getConnection();
				
				if(keyword!=null && !"".equals(keyword)) {
					//검색처리
					if(keyfield.equals("1")) sub_sql = "where id like ?";
					else if(keyfield.equals("2")) sub_sql = "where name like ?";
					else if(keyfield.equals("3")) sub_sql = "where email like ?";
				}
				
				sql = "select count(*) from zmember left outer join zmember_detail using (mem_num) " + sub_sql;
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				if(keyword!=null && !"".equals(keyword)) {
					pstmt.setString(1, "%"+keyword+"%");
				}
				rs = pstmt.executeQuery();
				if(rs.next()) {
					count = rs.getInt(1);
				}
			}catch (Exception e) {
				// TODO: handle exception
				throw new Exception(e);
			}finally {
				//자원정리
				DBUtil.executeClose(rs,pstmt,conn);
			}
			
			return count;
		}
		//목록(검색글 목록)
		public List<MemberVO> getListMemberByAdmin(int start,int end,String keyfield,String keyword) throws Exception {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			List<MemberVO> list = null;
			String sql = null;
			String sub_sql = "";
			int cnt = 0;
			
			try {
				//JDBC 수행 1,2단계 : 커넥션풀로부터 커넥션을 할당
				conn=DBUtil.getConnection();
				
				if(keyword!=null && !"".equals(keyword)) {
					//검색처리
					if(keyfield.equals("1")) sub_sql = "where id like ?";
					else if(keyfield.equals("2")) sub_sql = "where name like ?";
					else if(keyfield.equals("3")) sub_sql = "where email like ?";
				}
				
				
				//회원번호(mem_num) 생성
				sql = "select * from (select a.*, rownum rnum from (select * from zmember m left outer join zmember_detail d using(mem_num)" + sub_sql + " order by mem_num desc nulls last)a) where rnum >= ? and rnum <= ?" ;
				//JDBC 수행 3단계 : PrearedStatement 객체 생성
				pstmt = conn.prepareStatement(sql);
				if(keyword!=null && !"".equals(keyword)) {
					pstmt.setString(++cnt, "%"+keyword+"%");
				}
				pstmt.setInt(++cnt, start);
				pstmt.setInt(++cnt, end);
				//JDBC 수행 4단계
				rs = pstmt.executeQuery();
				
				list = new ArrayList<MemberVO>();
				while(rs.next()) {
					MemberVO member = new MemberVO();
					member.setMem_num(rs.getInt("mem_num"));
					member.setId(rs.getString("id"));
					member.setAuth(rs.getInt("auth"));
					member.setPasswd(rs.getString("passwd"));
					member.setName(rs.getString("name"));
					member.setPhone(rs.getString("phone"));
					member.setEmail(rs.getString("email"));
					member.setZipcode(rs.getString("zipcode"));
					member.setAddress1(rs.getString("address1"));
					member.setAddress2(rs.getString("address2"));
					member.setPhoto(rs.getString("photo"));
					member.setReg_date(rs.getDate("reg_date"));
					member.setModify_date(rs.getDate("modify_date"));
					
					list.add(member);
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				throw new Exception(e);
			}finally {
				//자원정리
				DBUtil.executeClose(rs,pstmt,conn);
			}
			
			return list;
		}
		//회원정보 수정
}
