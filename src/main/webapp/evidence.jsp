<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <link rel="stylesheet" href="js/jquery-ui-1.12.1.custom/jquery-ui.css">
    <link rel="stylesheet" type="text/css" href="css/evi.css" />
    <link rel="stylesheet" type="text/css" href="css/sortable.css" />
    <script src="js/fabric.min.js"></script>
    <script src="js/jquery-3.3.1.min.js"></script>
    <script src="js/jquery-ui-1.12.1.custom/jquery-ui.js"></script>
    <script src="js/evi.js" ></script>

    <title>楽々エビデンス</title>
</head>
<body>

<div class="page_title">
    <h2>楽々エビデンス</h2>
</div>

<div class="keep-one-line">
<div class="evidence-operation-entry">
    <div>
        <select class="evidence-file-list" title="エビデンス">
            <option value="" selected="selected">処理中のエビデンスを選ぶ</option>
            <c:forEach var="evi_name" items="${requestScope.folderList}">
                <option value="${evi_name}">${evi_name}</option>
            </c:forEach>
        </select>
    </div>
    <div class="operation-bar">
        <span id="add_image_sheet" class='ui-icon ui-icon-image' style="margin-right:0"></span>
        <span id="add_separator_sheet" class='ui-icon ui-icon-grip-dotted-horizontal' style="margin-right:0px"></span>
        <span id="add_db_sheet" class='ui-icon ui-icon-calculator' style="margin-right:105px"></span>
        <span class='ui-icon ui-icon-minusthick'> </span>
    </div>
    <div class="sheet-list">
        <ul class="sheet-list-ul">

        </ul>
    </div>
</div>
<div id="image-display">
    <p>エビデンスを選定してから、その内容がここに表示する。</p>

</div>
<%--<img style="border:1px solid grey;" id="mycanvas" src=""/>--%>
</div>


</body>
</html>
