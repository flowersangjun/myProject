<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% response.sendRedirect(request.getContextPath()+"/main/main.do"); %>

<%--
MVC 패턴 작업 순서
1.프로젝트를 MVC패턴에 맞게 구성
2.테이블 생성
3.자바빈(VO)
4.DAO
5.모델 클래스
6.JSP(View)
7.ActionMap 설정
 --%>
