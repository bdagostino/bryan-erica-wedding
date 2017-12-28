$(document).ready(function () {
  $('#invitationTable').DataTable({
    paging: true,
    serverSide: true,
    ordering: false,
    ajax: {
      url: "/admin/invitation/getInvitationData",
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

function openInvitationModal() {
  $.ajax({
    type: "POST",
    url: "/admin/invitation/invitationModal",
    data: $("#invitationModalForm").serialize(),
    success: function (data) {
      $("#invitationModal").html(data);
      $("#invitationModal").modal('show');
    }
  });
}

function submitInvitationForm() {
  $.ajax({
    type: "POST",
    url: "/admin/invitation/createInvitation",
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
    url: "/admin/invitation/invitationModal/addGuest",
    data: $("#invitationModalForm").serialize(),
    success: function (data) {
      $("#invitationModal").html(data);
    },
    error: function () {
      alert("Error Adding Guest To Invitation")
    }
  });
}

function removeGuest() {
  $.ajax({
    type: "POST",
    url: "/admin/invitation/invitationModal/removeGuest",
    data: $("#invitationModalForm").serialize(),
    success: function (data) {
      $("#invitationModal").html(data);
    },
    error: function () {
      alert("Error Removing Guest from Invitation")
    }
  });
}