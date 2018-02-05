const OPEN_GUEST_MODAL_URL = "/admin/guest/openGuestModal";
const SAVE_GUEST_URL = "/admin/guest/saveGuest";

$(document).ready(function () {
  var table = $('#guestTable').DataTable({
    paging: true,
    serverSide: true,
    ordering: false,
    ajax: {
      url: "/admin/guest/getGuestData",
      type: "POST",
      headers:getCsrfRequestHeader(),
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
      {data: "firstName"},
      {data: "lastName"},
      {data: "attendance"},
      {data: "food"},
      {data: "dietaryConcerns"},
      {data: "dietaryComments"},
      {data: null, defaultContent: "<button>Edit</button>", visible: ($("#canAdminEdit").val()==='true')}
    ]
  });

  $('#guestTable tbody').on('click', 'button', function () {
    var data = table.row($(this).parents('tr')).data();
    openGuestModal(data.id);
  });

});

function openGuestModal(guestId) {
  $.ajax({
    type: "POST",
    headers:getCsrfRequestHeader(),
    url: OPEN_GUEST_MODAL_URL,
    data: {guestId: guestId},
    success: function (data) {
      $("#guestModal").html(data);
      $("#guestModal").modal('show');
    }
  });
}

function submitGuestForm(){
  $.ajax({
    type: "POST",
    headers:getCsrfRequestHeader(),
    url: SAVE_GUEST_URL,
    data: $("#guestModalForm").serialize(),
    success: function (data) {
      $("#guestModal").html(data);
    },
    error: function () {
      alert("Error Saving Guest");
    }
  });
}

