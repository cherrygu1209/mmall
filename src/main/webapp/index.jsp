<html>
<body>
<h2>Hello World!</h2>

SpringMVC upload file
<form name="testUpload" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="springmvc upload file"/>
</form>

Rich text file upload
<form name="testRichTextUpload" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="springmvc richtext upload file"/>
</form>
</body>
</html>
