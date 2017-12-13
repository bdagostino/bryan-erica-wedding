$(document).ready(function () {
  $('#guestTable').DataTable({
    paging: true,
    serverSide: true,
    ordering: false,
    ajax: {
      url: "/admin/guest/getGuestData",
      type: "POST",
      contentType: "application/json; charset=utf-8",
      data: function (d) {
        return JSON.stringify(d);
      }
    },
    processing: true,
    columns: [
      {
        data: "id",
        visible: false
      },
      {data:"firstName"},
      {data:"lastName"},
      {data:"attendance"},
      {data:"food.type", defaultContent: ""},
      {data:"dietaryConcerns"},
      {data:"dietaryComments"}
    ]
  });
});