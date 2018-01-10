function getCsrfRequestHeader(){
  var token = $("meta[name='_csrf']").attr("content");
  var csrfHeader = $("meta[name='_csrf_header']").attr("content");
  var header = {};
  header[csrfHeader] = token;
  return header;
}