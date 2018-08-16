$(document).ready(function () {

    var rsvpSearchForm = document.getElementById('rsvpSearchForm');
    rsvpSearchForm.addEventListener('submit', function (event) {
      if (rsvpSearchForm.checkValidity() === false) {
        event.preventDefault();
        event.stopPropagation();
      }
      rsvpSearchForm.classList.add('was-validated');
    });

});