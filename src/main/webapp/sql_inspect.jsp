<%@ page contentType="text/html; charset=UTF-8" %>
<html>
    <head>
        <meta charset="UTF-8" />
        <link rel="stylesheet" type="text/css" href="css/site.css" />
    </head>
<body>
<h2>SQL確認</h2>
<p>SQLファイルをアップロードし、SQL内容の確認を行う。</p>

<form enctype="multipart/form-data" method="post" action="upload">
    <input type="file" name="sql_file" id="sql_file" />
    <br/>
    <br/>
    <button id="upload_button">分析＆仮実行する</button>
</form>
<hr />
</body>
</html>
