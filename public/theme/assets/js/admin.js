$(document).ready(function() {
  /* --------------------------------------------------------------------- */
  /* Create FormValidation
  /* --------------------------------------------------------------------- */
  (function($) {
    if (!$(".form_sample_3").length) return;

    var form3 = $('.form_sample_3');
    var error3 = $('.alert-danger', form3);
    var success3 = $('.alert-success', form3);

    //IMPORTANT: update CKEDITOR textarea with actual content before submit
    form3.on('submit', function() {
      for (var instanceName in CKEDITOR.instances) {
        CKEDITOR.instances[instanceName].updateElement();
      }
    })

    form3.validate({
      errorElement: 'span', //default input error message container
      errorClass: 'help-block help-block-error', // default input error message class
      focusInvalid: false, // do not focus the last invalid input
      ignore: "", // validate all fields including form hidden input
      rules: {
        name: {
          minlength: 2,
          required: true
        },
        email: {
          required: true,
          email: true
        },
        options1: {
          required: true
        },
        options2: {
          required: true
        },
        select2tags: {
          required: true
        },
        datepicker: {
          required: true
        },
        occupation: {
          minlength: 5,
        },
        membership: {
          required: true
        },
        service: {
          required: true,
          minlength: 2
        },
        editor2: {
          required: true
        }
      },

      messages: { // custom messages for radio buttons and checkboxes
        membership: {
          required: "Please select a Membership type"
        },
        service: {
          required: "Please select  at least 2 types of Service",
          minlength: jQuery.validator.format("Please select  at least {0} types of Service")
        }
      },

      errorPlacement: function(error, element) { // render error placement for each input type
        if (element.parent(".input-group").size() > 0) {
          error.insertAfter(element.parent(".input-group"));
        } else if (element.attr("data-error-container")) {
          error.appendTo(element.attr("data-error-container"));
        } else if (element.parents('.radio-list').size() > 0) {
          error.appendTo(element.parents('.radio-list').attr("data-error-container"));
        } else if (element.parents('.radio-inline').size() > 0) {
          error.appendTo(element.parents('.radio-inline').attr("data-error-container"));
        } else if (element.parents('.checkbox-list').size() > 0) {
          error.appendTo(element.parents('.checkbox-list').attr("data-error-container"));
        } else if (element.parents('.checkbox-inline').size() > 0) {
          error.appendTo(element.parents('.checkbox-inline').attr("data-error-container"));
        } else {
          error.insertAfter(element); // for other inputs, just perform default behavior
        }
      },

      invalidHandler: function(event, validator) { //display error alert on form submit   
        success3.hide();
        error3.show();
        Metronic.scrollTo(error3, -200);
      },

      highlight: function(element) { // hightlight error inputs
        $(element)
          .closest('.form-group').addClass('has-error'); // set error class to the control group
      },

      unhighlight: function(element) { // revert the change done by hightlight
        $(element)
          .closest('.form-group').removeClass('has-error'); // set error class to the control group
      },

      success: function(label) {
        label
          .closest('.form-group').removeClass('has-error'); // set success class to the control group
      },

      submitHandler: function(form) {
        success3.show();
        error3.hide();
        form[0].submit(); // submit the form
      }
    });

    //apply validation on select2 dropdown value change, this only needed for chosen dropdown integration.
    $('.select2me', form3).change(function() {
      form3.validate().element($(this)); //revalidate the chosen dropdown value and show error or success message for the input
    });

    // initialize select2 tags
    $("#select2_tags").change(function() {
      form3.validate().element($(this)); //revalidate the chosen dropdown value and show error or success message for the input 
    }).select2({
      tags: ["red", "green", "blue", "yellow", "pink"]
    });

    //initialize datepicker
    $('.date-picker').datepicker({
      rtl: Metronic.isRTL(),
      autoclose: true
    });
    $('.date-picker .form-control').change(function() {
      form3.validate().element($(this)); //revalidate the chosen dropdown value and show error or success message for the input 
    })
    
  })(jQuery);



  /* --------------------------------------------------------------------- */
  /* Metronic
  /* --------------------------------------------------------------------- */
  (function($) {
    if ($("body.login-page").length) return;

    Metronic.init(); // init metronic core components
    Layout.init(); // init current layout
    QuickSidebar.init(); // init quick sidebar
    Demo.init(); // init demo features
  })(jQuery);



  /* --------------------------------------------------------------------- */
  /* data-az-placeholder
  /* --------------------------------------------------------------------- */
  (function($) {
    if (!$("[data-az-placeholder]").length) return;

    $("[data-az-placeholder]").each(function() {
      var placeholderContent = $(this).attr("data-az-placeholder");
      $(this).attr("placeholder", placeholderContent);

      $(this).on("focus", function() {
        $(this).attr("placeholder", "");
      });

      $(this).on("blur", function() {
        $(this).attr("placeholder", placeholderContent);
      });
    });
  })(jQuery);



  // /* --------------------------------------------------------------------- */
  // /* Click to show password
  // /* --------------------------------------------------------------------- */
  // (function($) {
  //   if (!$(".az-inputbox[type='password']").length) return;

  //   $(".az-inputbox-wrapper").each(function() {
  //     var inputboxPass = $(this).find(".az-inputbox[type='password']");

  //     inputboxPass.after('<i class="az-inputbox-icon az-icon-show-password fa fa-eye"></i>');
  //     var iconShowPassword = $(this).find(".az-icon-show-password");

  //     iconShowPassword.hide();

  //     inputboxPass.keyup(function() {
  //       $(this).val().length >= 1 ? iconShowPassword.show() : iconShowPassword.hide();
  //     });

  //     iconShowPassword.on("click", function() {
  //       inputboxPass.attr("type") === "password" ? inputboxPass.attr("type", "text") : inputboxPass.attr("type", "password");
  //     });
  //   });
  // })(jQuery);











});

