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
      headers: getCsrfRequestHeader(),
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
      {data: "ceremonyAttendance"},
      {data: "receptionAttendance"},
      {data: "food"},
      {data: "dietaryConcerns"},
      {data: "dietaryComments"},
      {
        data: null,
        defaultContent: "<button class='btn btn-outline-dark mr-2 edit-button'>Edit</button>",
        visible: ($("#canAdminEdit").val() === 'true')
      }
    ]
  });

  $('#guestTable tbody').on('click', '.edit-button', function () {
    var data = table.row($(this).parents('tr')).data();
    openGuestModal(data.id);
  });

  $('#guestModal').on('shown.bs.modal', function () {
    var guestModalForm = document.getElementById('guestModalForm');
    guestModalForm.addEventListener('submit', function (event) {
      dietaryCommentsCustomValidation();
      if (guestModalForm.checkValidity() === false) {
        event.preventDefault();
        event.stopPropagation();
      }
      guestModalForm.classList.add('was-validated');
    });
  });

  if ($('#alertStatus').val() === 'success') {
    $('#success-alert').fadeIn(1000).delay(2000).fadeOut(1000);
  } else if ($('#alertStatus').val() === 'error') {
    $('#failed-alert').fadeIn(1000).delay(2000).fadeOut(1000);
  }

});

function openGuestModal(guestId) {
  $.ajax({
    type: "POST",
    headers: getCsrfRequestHeader(),
    url: OPEN_GUEST_MODAL_URL,
    data: {guestId: guestId},
    success: function (data) {
      $("#guestModal").html(data).modal('show');
    }
  });
}

function dietaryCommentsCustomValidation() {
  if (document.getElementById('dietaryConcernRadioYes').checked) {
    var dietaryCommentsInput = document.getElementById('inputDietaryComments');
    if (!dietaryCommentsInput.value) {
      dietaryCommentsInput.setCustomValidity('Error');
    }else{
      dietaryCommentsInput.setCustomValidity('');
    }
  }
}

