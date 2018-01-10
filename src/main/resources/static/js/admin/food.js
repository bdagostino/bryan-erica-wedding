$(document).ready(function () {
  $('#foodTable').DataTable({
    paging: true,
    serverSide: true,
    ordering: false,
    ajax: {
      url: "/admin/food/getFoodData",
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
      {data: "type"},
      {data:"description"}
    ]
  });

  $('#foodModalForm').submit(function (event) {
    event.preventDefault();
    var food = {
      type: $('#inputType').val(),
      description: $('#inputDescription').val()
    };

    $.ajax({
      type: "POST",
      headers:getCsrfRequestHeader(),
      contentType: "application/json; charset=utf-8",
      url: "/admin/food/addFood",
      data: JSON.stringify(food),
      success: function () {
        location.reload(true);
      },
      error: function (data) {
        clearInvalidFeedback();
        var fieldErrors = data.responseJSON.fieldErrorList;
        var globalError = data.responseJSON.globalError;
        if(fieldErrors != null){
          fieldErrors.forEach(function(error){
            checkForTypeError(error);
            checkForDescriptionError(error);
          });
        }if(globalError){
          alert('Unknown Error has Occurred!');
        }
      }
    });
  });
});

function clearInvalidFeedback(){
  $('#inputType').removeClass().addClass('form-control');
  $('#inputDescription').removeClass().addClass('form-control');
}

function checkForTypeError(error){
  if(error.field === 'type'){
    $('#inputType').addClass('is-invalid');
    $('#typeFeedback').text(error.message);
  }
}

function checkForDescriptionError(error){
  if(error.field === 'description'){
    $('#inputDescription').addClass('is-invalid');
    $('#descriptionFeedback').text(error.message);
  }
}