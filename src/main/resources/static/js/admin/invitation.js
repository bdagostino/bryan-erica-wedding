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

  $('#inputMaxGuests').on('input', function () {
    var guestDivCount = $('div.guestDiv').length;
    if ($(this).val() < 1) {
      $(this).removeClass('is-valid');
      $(this).addClass('is-invalid');
      $('#addGuestButton').attr('disabled', 'disabled');
      $('#invitationModalSubmitButton').attr('disabled', 'disabled');
      if (guestDivCount > 0) {
        $('#deleteGuestButton').removeAttr('disabled');
      }
    } else {
      $(this).removeClass('is-invalid');
      $(this).addClass('is-valid');
      if (guestDivCount > 0) {
        $('#deleteGuestButton').removeAttr('disabled');
        if (guestDivCount <= $(this).val()) {
          $('#invitationModalSubmitButton').removeAttr('disabled');
        }
      }
      if (guestDivCount < $(this).val()) {
        $('#addGuestButton').removeAttr('disabled');
      }
      if (guestDivCount > $(this).val()) {
        $('#invitationModalSubmitButton').attr('disabled', 'disabled');
        $('#addGuestButton').attr('disabled', 'disabled');
      }
    }
  });


  $('#addGuestButton').click(function () {
    var guestDivCount = $('div.guestDiv').length;
    var nextGuestIndex = guestDivCount + 1;
    if (guestDivCount > 0) {
      $('div.guestDiv').last().after(addGuestDiv(nextGuestIndex));
      if (nextGuestIndex >= parseInt($('#inputMaxGuests').val())) {
        $('#addGuestButton').attr('disabled', 'disabled');
      }
    } else {
      $('#addGuestHeader').after(addGuestDiv(nextGuestIndex));
      if (nextGuestIndex >= parseInt($('#inputMaxGuests').val())) {
        $('#addGuestButton').attr('disabled', 'disabled');
      }
    }
    $('#deleteGuestButton').removeAttr('disabled');
    $('#invitationModalSubmitButton').removeAttr('disabled');
  });

  $('#deleteGuestButton').click(function () {
    var guestDivCount = $('div.guestDiv').length;
    if (guestDivCount > 0) {
      $('div.guestDiv').last().remove();
    }
    if (parseInt($('#inputMaxGuests').val()) > 0) {
      if (guestDivCount - 1 < parseInt($('#inputMaxGuests').val())) {
        $('#addGuestButton').removeAttr('disabled');
      }
      if (guestDivCount - 1 <= parseInt($('#inputMaxGuests').val())) {
        $('#invitationModalSubmitButton').removeAttr('disabled');
      }
    }
    if (guestDivCount - 1 < 1) {
      $('#deleteGuestButton').attr('disabled', 'disabled');
      $('#invitationModalSubmitButton').attr('disabled', 'disabled');
    }
  });

  $('#invitationModalForm').submit(function (event) {
    event.preventDefault();
    var guestList = [];
    var guestFormData = $('div.guestDiv');
    $.each(guestFormData, function (index, object) {
      var guest = {
        firstName: $(object).find('input#inputGuest' + (index + 1) + 'FirstName').val(),
        lastName: $(object).find('input#inputGuest' + (index + 1) + 'LastName').val()
      };
      guestList.push(guest);
    });

    var invitation = {
      guestList: guestList,
      maxGuests: parseInt($('#inputMaxGuests').val())
    };

    $.ajax({
      type: "POST",
      contentType: "application/json; charset=utf-8",
      url: "/admin/invitation/createInvitation",
      data: JSON.stringify(invitation),
      success: function () {
        location.reload(true);
      },
      error: function (data) {
        clearInvalidFeedback();
        var fieldErrors = data.responseJSON.fieldErrorList;
        var globalError = data.responseJSON.globalError;
        if(fieldErrors != null){
        fieldErrors.forEach(function(error){
          checkForGuestListError(error);
        });
        }if(globalError){
          alert('Unknown Error has Occurred!');
        }
      }
    });
  });


});

function addGuestDiv(guestNumber) {
  var guestDiv = '<div class="guestDiv">\n' +
      '            <div class="form-row">\n' +
      '              <h4>Guest ' + guestNumber + '</h4>\n' +
      '            </div>\n' +
      '            <div class="form-row">\n' +
      '              <div class="col mb-3">\n' +
      '                <label for="inputGuest' + guestNumber + 'FirstName">First name</label>\n' +
      '                <div class="input-group">\n' +
      '                  <span class="input-group-addon">Text</span>\n' +
      '                  <input id="inputGuest' + guestNumber + 'FirstName" type="text" class="form-control" placeholder="First Name"/>\n' +
      '                  <div id="guest' + guestNumber + 'FirstNameFeedback" class="invalid-feedback" style="padding-left: 1em"></div>\n' +
      '                </div>\n' +
      '              </div>\n' +
      '            </div>\n' +
      '            <div class="form-row">\n' +
      '              <div class="col mb-3">\n' +
      '                <label for="inputGuest' + guestNumber + 'LastName">Last name</label>\n' +
      '                <div class="input-group">\n' +
      '                  <span class="input-group-addon">Text</span>\n' +
      '                  <input id="inputGuest' + guestNumber + 'LastName" type="text" class="form-control" placeholder="Last Name"/>\n' +
      '                  <div id="guest' + guestNumber + 'LastNameFeedback" class="invalid-feedback" style="padding-left: 1em"></div>\n' +
      '                </div>\n' +
      '              </div>\n' +
      '            </div>\n' +
      '          </div>';
  return guestDiv;
}

function clearInvalidFeedback(){
  var guestFormData = $('div.guestDiv');
  $.each(guestFormData, function (index, object) {
      $(object).find('input#inputGuest' + (index + 1) + 'FirstName').removeClass().addClass('form-control');
      $(object).find('input#inputGuest' + (index + 1) + 'LastName').removeClass().addClass('form-control');
  })
}

function checkForGuestListError(error){
  var errorField = error.field;
  if(errorField.indexOf('guestList')===0){
    var splitErrorFieldArray = errorField.split('.',2);
    var index = getArrayIndex(splitErrorFieldArray[0]);
    var inputField = splitErrorFieldArray[1];
    if(inputField === 'firstName'){
      $('#inputGuest' + (index + 1) + 'FirstName').addClass('is-invalid');
      $('#guest' + (index + 1) + 'FirstNameFeedback').text(error.message);
    }if(inputField === 'lastName'){
      $('#inputGuest' + (index + 1) + 'LastName').addClass('is-invalid');
      $('#guest' + (index + 1) + 'LastNameFeedback').html(error.message);
    }
  }
}

function getArrayIndex(arrayName){
  return parseInt(arrayName.substring(arrayName.indexOf('[')+1,arrayName.indexOf(']')));
}