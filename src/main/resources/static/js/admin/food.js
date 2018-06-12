$(document).ready(function () {
  var table = $('#foodTable').DataTable({
    paging: true,
    serverSide: true,
    ordering: false,
    ajax: {
      url: "/admin/food/getFoodData",
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
      {data: "type"},
      {data: "description"},
      {
        data: null,
        defaultContent: "<button class='btn btn-outline-dark mr-2 edit-button'>Edit</button><button class='btn btn-outline-dark delete-button'>Delete</button>",
        visible: ($("#canAdminEdit").val() === 'true')
      }
    ]
  });

  $('#foodTable tbody').on('click', '.edit-button', function () {
    var data = table.row($(this).parents('tr')).data();
    openFoodModal(data.id);
  });

  $('#foodTable tbody').on('click', '.delete-button', function () {
    var data = table.row($(this).parents('tr')).data();
    deleteFood(data.id);
  });


  $('#foodModal').on('shown.bs.modal', function () {
    var foodModalForm = document.getElementById('foodModalForm');
    foodModalForm.addEventListener('submit', function (event) {
      if (foodModalForm.checkValidity() === false) {
        event.preventDefault();
        event.stopPropagation();
      }
      foodModalForm.classList.add('was-validated');
    });
  });

  if ($('#alertStatus').val() === 'success') {
    $('#success-alert').fadeIn(1000).delay(2000).fadeOut(1000);
  }else if ($('#alertStatus').val() === 'error') {
    $('#failed-alert').fadeIn(1000).delay(2000).fadeOut(1000);
  }

});

function openFoodModal(foodId) {
  $.ajax({
    type: "POST",
    headers: getCsrfRequestHeader(),
    url: "/admin/food/openFoodModal",
    data: {foodId: foodId},
    success: function (data) {
      $("#foodModal").html(data);
      $("#foodModal").modal('show');
    }
  });
}

function deleteFood(foodId) {
  $.ajax({
    type: "POST",
    headers: getCsrfRequestHeader(),
    url: "/admin/food/removeFood",
    contentType: "text/plain; charset=utf-8",
    data: String(foodId),
    success: function (data) {
      window.location.href = data;
    },
    error: function () {
      window.location.href = "/admin/food?status=error";
    }
  });
}

