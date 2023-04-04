package com.springbook.biz.board.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.springbook.biz.board.BoardVO;
import com.springbook.biz.common.JDBCUtil;

@Repository("boardDAO")
public class BoardDAOSpring extends JdbcDaoSupport {
	//SQL ���� ���ɾ�
	//private final String BOARD_INSERT = "INSERT INTO BOARD(SEQ, TITLE, WRITER, CONTENT) "
	//		                          + "VALUES((SELECT NVL(MAX(SEQ), 0) + 1 FROM BOARD), ?, ?, ?)";
	private final String BOARD_INSERT = "INSERT INTO studydb.BOARD(SEQ, TITLE, WRITER, CONTENT) "
            + "VALUES((SELECT IFNULL(MAX(SEQ), 0) + 1 FROM BOARD A), ?, ?, ?)";	
//	private final String BOARD_INSERT = "INSERT INTO studydb.BOARD(SEQ, TITLE, WRITER, CONTENT) "
//            + "VALUES((SELECT IFNULL(MAX(SEQ), 0) + 1 FROM BOARD A), vo.getTitle(), vo.getWriter(), vo.getContent())";	
	
//	Object[] args = {vo.getTitle(), vo.getWriter(), vo.getContent()};
//	jdbcTemplate.update(BOARD_INSERT, args);
	private final String BOARD_UPDATE = "UPDATE studydb.BOARD SET TITLE = ?, CONTENT = ? WHERE SEQ = ?";
	private final String BOARD_DELETE = "DELETE FROM studydb.BOARD WHERE SEQ = ?";
	private final String BOARD_GET = "SELECT * FROM studydb.BOARD WHERE SEQ = ?";
	private final String BOARD_LIST = "SELECT * FROM studydb.BOARD ORDER BY SEQ DESC";
	
	//�޼ҵ忡�� Autowired ��� ����
	//�ش� �޼ҵ带 �����������̳ʰ� �ڵ����� ȣ��
	@Autowired
	//��ӹ��� JdbcDaoSupport��ü���� setDataSource �޼ҵ带 ȣ���ؼ� �����ͼҽ� ��ü ����������
	public void setSuperDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}
	
	//CRUD ����� �޼ҵ� ����
	//�� ���
	//getJdbcTemplate() : jdbcTemplate��ü�� ����. JdbcDaoSupport Ŭ�������� ��ӹ޾� ����ϴ� �޼ҵ�
	public void insertBoard(BoardVO vo) {
		System.out.println("====> JDBC�� insertBoard() ��� ó��");
		getJdbcTemplate().update(BOARD_INSERT, vo.getTitle(), vo.getWriter(), vo.getContent());
	}
	
	//�� ����
	public void upadateBoard(BoardVO vo) {
		System.out.println("====> JDBC�� upadateBoard() ��� ó��");
		getJdbcTemplate().update(BOARD_UPDATE, vo.getTitle(), vo.getWriter(), vo.getContent());		
	}
	
	//�� ����
	public void deleteBoard(BoardVO vo) {
		System.out.println("====> JDBC�� deleteBoard() ��� ó��");
		getJdbcTemplate().update(BOARD_DELETE, vo.getTitle(), vo.getWriter(), vo.getContent());
	}
	
	//�� �� ��ȸ
	public BoardVO getBoard(BoardVO vo) {
		System.out.println("====> JDBC�� getBoard() ��� ó��");
		Object[] args = {vo.getSeq()};
		return getJdbcTemplate().queryForObject(BOARD_GET, args, new BoardRowMapper());
	}
	
	//�� ��� ��ȸ
	public List<BoardVO> getBoardList(BoardVO vo) {
		System.out.println("====> JDBC�� getBoard() ��� ó��");
		return getJdbcTemplate().query(BOARD_LIST, new BoardRowMapper());
	}
}