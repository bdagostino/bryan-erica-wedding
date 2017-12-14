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
      {data: "firstName"},
      {data: "lastName"},
      {data: "attendance"},
      {data: "food.type", defaultContent: ""},
      {data: "dietaryConcerns"},
      {data: "dietaryComments"}
    ]
  });

  $('#guestModalForm').submit(function (event) {
    event.preventDefault();
    var guest = {
      firstName: $('#inputFirstName').val(),
      lastName: $('#inputLastName').val()
    };

    $.ajax({
      type: "POST",
      contentType: "application/json; charset=utf-8",
      url: "/admin/guest/addGuest",
      data: JSON.stringify(guest),
      success: function () {
        location.reload(true);
      },
      error: function (data) {
        clearInvalidFeedback();
        var fieldErrors = data.responseJSON.fieldErrorList;
        var globalError = data.responseJSON.globalError;
        if(fieldErrors != null){
          fieldErrors.forEach(function(error){
            checkForFirstNameError(error);
            checkForLastNameError(error);
          });
        }if(globalError){
          alert('Unknown Error has Occurred!');
        }
      }
    });
  });
});

function clearInvalidFeedback(){
  $('#inputFirstName').removeClass().addClass('form-control');
  $('#inputLastName').removeClass().addClass('form-control');
}

function checkForFirstNameError(error){
  if(error.field === 'firstName'){
    $('#inputFirstName').addClass('is-invalid');
    $('#firstNameFeedback').text(error.message);
  }
}

function checkForLastNameError(error){
  if(error.field === 'lastName'){
    $('#inputLastName').addClass('is-invalid');
    $('#lastNameFeedback').html(error.message);
  }
}