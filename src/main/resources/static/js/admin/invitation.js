const ADD_GUEST_URL = "/admin/invitation/invitationModal/addGuest";
const REMOVE_GUEST_URL = "/admin/invitation/invitationModal/removeGuest";


$(document).ready(function () {
  var table = $('#invitationTable').DataTable({
    paging: true,
    serverSide: true,
    ordering: false,
    ajax: {
      url: "/admin/invitation/getInvitationData",
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
      {data: "invitedGuests"},
      {data: "additionalGuests"},
      {data: "maxAdditionalGuests"},
      {data: null, defaultContent: "<button>Edit</button>", visible: ($("#canAdminEdit").val()==='true')}
    ]
  });

  $('#invitationTable tbody').on('click', 'button', function () {
    var data = table.row($(this).parents('tr')).data();
    openInvitationModal(data.id);
  });
});

function openInvitationModal(invitationId) {
  $.ajax({
    type: "POST",
    headers:getCsrfRequestHeader(),
    url: "/admin/invitation/openInvitationModal",
    data: {invitationId: invitationId},
    success: function (data) {
      $("#invitationModal").html(data);
      $("#invitationModal").modal('show');
    }
  });
}

function submitInvitationForm() {
  $.ajax({
    type: "POST",
    headers:getCsrfRequestHeader(),
    url: "/admin/invitation/saveInvitation",
    data: $("#invitationModalForm").serialize(),
    success: function (data) {
      $("#invitationModal").html(data);
    },
    error: function () {
      alert("Error Creating Invitation");
    }
  });
}

function addGuest() {
  $.ajax({
    type: "POST",
    url: ADD_GUEST_URL,
    headers:getCsrfRequestHeader(),
    data: $("#invitationModalForm").serialize(),
    success: function (data) {
      $("#invitationModal").html(data);
    },
    error: function () {
      alert("Error Adding Guest To Invitation")
    }
  });
}

function removeGuest(removalIndex) {
  $('#removalIndex').val(removalIndex);
  $.ajax({
    type: "POST",
    headers:getCsrfRequestHeader(),
    url: REMOVE_GUEST_URL,
    data: $("#invitationModalForm").serialize(),
    success: function (data) {
      $("#invitationModal").html(data);
    },
    error: function () {
      alert("Error Removing Guest from Invitation")
    }
  });
}