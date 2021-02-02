var plugins = {
    rdMailForm: $(".rd-mailform"),
    regula: $("[data-constraints]"),
    rdInputLabel: $(".form-label")
},
$document = $(document);

$document.ready(function () {
    var isNoviBuilder = window.xMode;

    /**
     * attachFormValidator
     * @description  attach form validation to elements
     */
    function attachFormValidator(elements) {
        for (var i = 0; i < elements.length; i++) {
            var o = $(elements[i]),
            v;
            o.addClass("form-control-has-validation").after("<span class='form-validation'></span>");
            v = o.parent().find(".form-validation");
            if (v.is(":last-child")) {
                o.addClass("form-control-last-child");
            }
        }

        elements
        .on('input change propertychange blur', function (e) {
            var $this = $(this),
            results;

            if (e.type !== "blur") {
                if (!$this.parent().hasClass("has-error")) {
                    return;
                }
            }

            if ($this.parents('.rd-mailform').hasClass('success')) {
                return;
            }

            if ((results = $this.regula('validate')).length) {
                for (i = 0; i < results.length; i++) {
                    $this.siblings(".form-validation").text(results[i].message).parent().addClass("has-error")
                }
            } else {
                $this.siblings(".form-validation").text("").parent().removeClass("has-error")
            }
        })
        .regula('bind');

        var regularConstraintsMessages = [{
                type: regula.Constraint.Required,
                newMessage: "Поле повинно бути заповненим"
            }, {
                type: regula.Constraint.Email,
                newMessage: "Введена електронна пошта не є корректною"
            }, {
                type: regula.Constraint.Numeric,
                newMessage: "Тільки цифри доступні до вводу"
            }, {
                type: regula.Constraint.Selected,
                newMessage: "Виберіть одну з можливостей"
            }
        ];

        for (var i = 0; i < regularConstraintsMessages.length; i++) {
            var regularConstraint = regularConstraintsMessages[i];

            regula.override({
                constraintType: regularConstraint.type,
                defaultMessage: regularConstraint.newMessage
            });
        }
    }

    /**
     * isValidated
     * @description  check if all elements pass validation
     *
     *  by Sters
     * Modified for using with java backend and add some additional logic
     */
    function isValidated(elements, captcha) {
        var results,
        errors = 0;

        if (elements.length) {
            for (j = 0; j < elements.length; j++) {

                var $input = $(elements[j]);
                if ((results = $input.regula('validate')).length) {
                    for (k = 0; k < results.length; k++) {
                        errors++;
                        $input.siblings(".form-validation").text(results[k].message).parent().addClass("has-error");
                    }
                } else {
                    $input.siblings(".form-validation").text("").parent().removeClass("has-error")
                }
            }

            if (captcha) {
                if (captcha.length) {
                    return validateReCaptcha(captcha) && errors === 0
                }
            }

            return errors === 0;
        }
        return true;
    }

    /**
     * RD Mailform
     * @version      3.2.0
     */
    if (plugins.rdMailForm.length) {
        var i,
        j,
        k,
        msg = {
            'MF000': 'Successfully sent!',
            'MF001': 'Recipients are not set!',
            'MF002': 'Form will not work locally!',
            'MF003': 'Please, define email field in your form!',
            'MF004': 'Please, define type of your form!',
            'MF254': 'Something went wrong with PHPMailer!',
            'MF255': 'Aw, snap! Something went wrong.',
            'TR001': 'Custom error without redirection. Error text in field _text_',
            'TR002': 'Redirect to URI from field _text_'
        };

        for (i = 0; i < plugins.rdMailForm.length; i++) {
            var $form = $(plugins.rdMailForm[i]),
            formHasCaptcha = false;

            $form.attr('novalidate', 'novalidate').ajaxForm({
                data: {
                    "form-type": $form.attr("data-form-type") || "contact",
                    "counter": i
                },
                beforeSubmit: function (arr, $form, options) {
                    if (isNoviBuilder)
                        return;

                    var form = $(plugins.rdMailForm[this.extraData.counter]),
                    inputs = form.find("[data-constraints]"),
                    output = $("#" + form.attr("data-form-output")),
                    captcha = form.find('.recaptcha'),
                    captchaFlag = true;

                    output.removeClass("active error success");

                    if (isValidated(inputs, captcha)) {

                        // veify reCaptcha
                        if (captcha.length) {
                            var captchaToken = captcha.find('.g-recaptcha-response').val(),
                            captchaMsg = {
                                'CPT001': 'Please, setup you "site key" and "secret key" of reCaptcha',
                                'CPT002': 'Something wrong with google reCaptcha'
                            };

                            formHasCaptcha = true;

                            $.ajax({
                                method: "POST",
                                url: "bat/reCaptcha.php",
                                data: {
                                    'g-recaptcha-response': captchaToken
                                },
                                async: false
                            })
                            .done(function (responceCode) {
                                if (responceCode !== 'CPT000') {
                                    if (output.hasClass("snackbars")) {
                                        output.html('<p><span class="icon text-middle mdi mdi-check icon-xxs"></span><span>' + captchaMsg[responceCode] + '</span></p>')

                                        setTimeout(function () {
                                            output.removeClass("active");
                                        }, 3500);

                                        captchaFlag = false;
                                    } else {
                                        output.html(captchaMsg[responceCode]);
                                    }

                                    output.addClass("active");
                                }
                            });
                        }

                        if (!captchaFlag) {
                            return false;
                        }

                        form.addClass('form-in-process');

                        if (output.hasClass("snackbars")) {
                            output.html('<p><span class="icon text-middle fa fa-circle-o-notch fa-spin icon-xxs"></span><span>Відсилання</span></p>');
                            output.addClass("active");
                        }
                    } else {
                        return false;
                    }
                },
                error: function (result) {
                    var resultData = JSON.parse(result.responseText);

                    if (isNoviBuilder)
                        return;

                    var output = $("#" + $(plugins.rdMailForm[this.extraData.counter]).attr("data-form-output")),
                    form = $(plugins.rdMailForm[this.extraData.counter]);

                    if (resultData.code == "TR001") {
                        output.text(resultData.text);

                        setTimeout(function () {
                            output.removeClass("active error success");
                            form.removeClass('success');
                        }, 3500);
                    } else {
                        output.text(msg[resultData.code]);
                    };
                    form.removeClass('form-in-process');

                    if (formHasCaptcha) {
                        grecaptcha.reset();
                    }
                },
                success: function (responseText, statusText, xhr, element) {
                    var resultData = JSON.parse(responseText);

                    if (isNoviBuilder)
                        return;

                    var form = $(plugins.rdMailForm[this.extraData.counter]),
                    output = $("#" + form.attr("data-form-output")),
                    select = form.find('select');

                    form
                    .addClass('success')
                    .removeClass('form-in-process');

                    if (formHasCaptcha) {
                        grecaptcha.reset();
                    }

                    if (msg[resultData.code] == undefined) {
                        resultData.code = 'MF255';
                    }

                    if (msg[resultData.code] == 'TR001') {
                        text = resultData.text;
                    } else {
                        text = msg[resultData.code];
                    }
                    output.text(text);

                    if (resultData.code === "MF000") {
                        if (output.hasClass("snackbars")) {
                            output.html('<p><span class="icon text-middle mdi mdi-check icon-xxs"></span><span>' + text + '</span></p>');
                        } else {
                            output.addClass("active success");
                        }
                    } else {
                        if (output.hasClass("snackbars")) {
                            output.html(' <p class="snackbars-left"><span class="icon icon-xxs mdi mdi-alert-outline text-middle"></span><span>' + text + '</span></p>');
                        } else {
                            output.addClass("active error");
                        }
                    }

                    form.clearForm();

                    if (select.length) {
                        select.select2("val", "");
                    }

                    form.find('input, textarea').trigger('blur');

                    setTimeout(function () {
                        output.removeClass("active error success");
                        form.removeClass('success');
                    }, 3500);

                    if (resultData.code === "TR002") {
                        window.location.replace(resultData.text);
                    }
                }
            });
        }
    }

    /**
     * RD Input Label
     * @description Enables RD Input Label Plugin
     */

    if (plugins.rdInputLabel.length) {
        plugins.rdInputLabel.RDInputLabel();
    }
});