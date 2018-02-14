const ADD_ADDITIONAL_GUEST_URL = "/rsvp/addAdditionalGuest";
const REMOVE_ADDITIONAL_GUEST_URL = "/rsvp/removeAdditionalGuest?removalIndex=";

function addAdditionalGuest() {
  $.ajax({
    type: "POST",
    url: ADD_ADDITIONAL_GUEST_URL,
    headers:getCsrfRequestHeader(),
    data: $("#rsvpForm").serialize(),
    success: function (data) {
      $("#rsvpForm").html(data);
    },
    error: function () {
      alert("Error Adding Guest To Invitation")
    }
  });
}

function removeAdditionalGuest(removalIndex) {
  $.ajax({
    type: "POST",
    headers:getCsrfRequestHeader(),
    url: REMOVE_ADDITIONAL_GUEST_URL+removalIndex,
    data: $("#rsvpForm").serialize(),
    success: function (data) {
      $("#rsvpForm").html(data);
    },
    error: function () {
      alert("Error Removing Guest from Invitation")
    }
  });
}