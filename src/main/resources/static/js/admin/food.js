$(document).ready(function () {
  var table = $('#foodTable').DataTable({
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
      {data:"description"},
      {data: null, defaultContent: "<button class='edit-button'>Edit</button><button class='delete-button'>Delete</button>", visible: ($("#canAdminEdit").val()==='true')}
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
});

function openFoodModal(foodId){
  $.ajax({
    type: "POST",
    headers:getCsrfRequestHeader(),
    url: "/admin/food/openFoodModal",
    data: {foodId: foodId},
    success: function (data) {
      $("#foodModal").html(data);
      $("#foodModal").modal('show');
    }
  });
}

function submitFoodForm(){
  $.ajax({
    type: "POST",
    headers:getCsrfRequestHeader(),
    url: "/admin/food/saveFood",
    data: $("#foodModalForm").serialize(),
    success: function (data) {
      $("#foodModal").html(data);
    },
    error: function () {
      alert("Error Saving Food");
    }
  });
}

function deleteFood(foodId){
  $.ajax({
    type: "POST",
    headers:getCsrfRequestHeader(),
    url: "/admin/food/removeFood",
    data: {foodId: foodId},
    success: function (data) {
      $("#foodModal").html(data);
      $("#foodModal").modal('show');
    },
    error: function (data) {
      alert(data);
    }
  });
}

