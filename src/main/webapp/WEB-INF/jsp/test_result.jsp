<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <meta charset="UTF-8" />
        <link rel="stylesheet" type="text/css" href="css/site.css" />
    </head>
<body>
<h2>SQL分析結果</h2>
<jsp:useBean id="sql_entries" scope="request" type="java.util.List"/>
<jsp:useBean id="disorderedSQLID" scope="request" type="java.util.Collection"/>
<jsp:useBean id="non9bitsSQLID" scope="request" type="java.util.Collection"/>
<ul>
<li>このファイルには、<span style="font-weight:bold;color:blue">
<c:out value="${sql_entries.size()}"/></span> 個のSQLが記載されている。</li>
<c:if test="${disorderedSQLID.size() != 0}">
<li><span style="font-weight:bold;color:red">SQLIDの昇順で記載されていない。<br/><c:out value="${disorderedSQLID}"/></span></li>
</c:if>
<c:if test="${non9bitsSQLID.size() != 0}">
<li><span style="font-weight:bold;color:red">SQLID不正有り。<br/><c:out value="${non9bitsSQLID}"/></span></li>
</c:if>
</ul>

<table class="test_result_display_table">
<tr>
    <th>SQLID</th>
    <th>SQL内容</th>
    <th>仮実行報告</th>
</tr>
<c:forEach var="sqlEntry" items="${sql_entries}" varStatus="status">
    <tr>
        <td width="6%"><c:out value="${sqlEntry.id}" /></td>
        <td width="64%"><c:out value="${sqlEntry.content}" /></td>
        <td width="30%"><c:out value="${sqlEntry.oracleMessage}" /></td>
    </tr>
</c:forEach>

</table>


</body>
</html>

