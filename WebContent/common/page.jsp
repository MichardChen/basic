<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="page" scope="page" value="${page}"/>
<c:set var="pageUrl" scope="page" value="${pageUrl}" />
 
<c:set var="pageNumber" scope="page" value="${page.pageNumber}" /> 
<c:set var="pageSize" scope="page" value="${page.pageSize}" /> 
<c:set var="totalPage" scope="page" value="${page.totalPage}" /> 
<c:set var="totalRow" scope="page" value="${page.totalRow}" /> 
 
<div>
<!-- 分页逻辑 -->
<!-- 上一页标签 -->
<p class="green-black">
总共
 &nbsp;&nbsp;
${totalPage }
&nbsp;&nbsp;
页
&nbsp;&nbsp;&nbsp;
<c:choose>
    <c:when test="${pageNumber<=1}">
        <span>上一页</span>&nbsp;&nbsp;
    </c:when>
    <c:otherwise>
        <a href="${pageUrl}${pageNumber-1}">上一页</a>
    </c:otherwise>
</c:choose>
当前第
&nbsp;&nbsp;
<c:choose>
    <c:when test="${totalPage==0}">
        ${totalPage}
    </c:when>
    <c:otherwise>
        ${pageNumber}
    </c:otherwise>
</c:choose>
&nbsp;&nbsp;
页
&nbsp;&nbsp;
<c:choose>
    <c:when test="${pageNumber==totalPage || totalPage==0}">
        <span>下一页</span>
    </c:when>
    <c:when test="${pageNumber<totalPage}">
        <a href="${pageUrl}${pageNumber+1}">下一页</a>
    </c:when>
    <c:otherwise>
     
    </c:otherwise>
</c:choose>
&nbsp;&nbsp;
<span>选择:</span>
<select name="pageSelect" onchange="window.location.href=this.options[selectedIndex].value" style="color:black;">
    <c:forEach var="i" begin="1" end="${totalPage}" step="1">
        <c:choose>
            <c:when test="${i==pageNumber}">
                <option value="${pageUrl}${i}" selected>${i}</option>
            </c:when>
            <c:when test="${i!=pageNumber}">
                <option value="${pageUrl}${i}">${i}</option>
            </c:when>
            <c:otherwise>
             
            </c:otherwise>
        </c:choose>
    </c:forEach>
</select>
</p>
</div>
