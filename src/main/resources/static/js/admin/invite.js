$(document).ready(function () {
  $('#inviteTable').DataTable({
    paging: true,
    serverSide: true,
    ordering: false,
    ajax: {
      url: "/admin/invite/getInviteData",
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
      {data: "invitedGuests"},
      {data: "additionalGuests"},
      {data: "maxAdditionalGuests"}
    ]
  });
});