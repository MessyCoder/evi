<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="js/jquery-ui-1.12.1.custom/jquery-ui.css">
    <link rel="stylesheet" type="text/css" href="css/site.css" />

    <script src="js/jquery-3.3.1.min.js"></script>
    <script src="js/jquery-ui-1.12.1.custom/jquery-ui.js"></script>
    <script src="js/evi.js" ></script>
    <style>
        #image-display {
            /*float: left;*/
            padding-bottom: 100px;
            margin-left:225px ;
        }
        .portlet {
            width: fit-content;
            height: fit-content;
            margin: 0 1em 1em 0;
            padding: 0.3em;

        }
        .portlet-header {
            padding: 0.2em 0.3em;
            margin-bottom: 0.5em;
            position: relative;
        }
        .portlet-toggle {
            position: absolute;
            top: 50%;
            right: 0;
            margin-top: -8px;
        }
        .portlet-content {
            padding: 0.4em;
        }
        .portlet-placeholder {
            border: 1px solid #cccccc;
            background-color: sandybrown;
            margin: 0 1em 1em 0;
            height: 20px;
        }
    </style>
</head>
<body>
<div style="display">
<h2>テストエビデンスを作成しましょう</h2>
<hr/>
</div>
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
        <span id="add_sheet" class='ui-icon ui-icon-image' style="margin-right:0"></span>
        <span id="add_separator" class='ui-icon ui-icon-grip-dotted-horizontal' style="margin-right:0px"></span>
        <span id="add_db" class='ui-icon ui-icon-calculator' style="margin-right:100px"></span>
        <span class='ui-icon ui-icon-minusthick'> </span>
    </div>
    <div class="sheet-list">
        <ul class="sheet-list-ul">

        </ul>
    </div>
</div>
<div id="image-display"></div>
<%--<img style="border:1px solid grey;" id="mycanvas" src=""/>--%>



</body>
</html>
