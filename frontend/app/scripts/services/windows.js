'use strict';

app.factory('Win', ['$rootScope', function ($rootScope) {

    var service = {};

    service.popup = function (mensagem) {
        if (typeof Windows !== 'undefined' &&
            typeof Windows.UI !== 'undefined' &&
            typeof Windows.UI.Popups !== 'undefined') {
            // Create the message dialog and set its content
            var msg = new Windows.UI.Popups.MessageDialog(mensagem);
            // Add commands
            msg.commands.append(new Windows.UI.Popups.UICommand("Okay", systemAlertCommandInvokedHandler));
            // Set default command
            msg.defaultCommandIndex = 0;
            // Show the message dialog
            msg.showAsync();
        }
    };

    service.contact = function () {
        if (typeof Windows != 'undefined') {
            // Create the picker
            var picker = new Windows.ApplicationModel.Contacts.ContactPicker();
            picker.desiredFieldsWithContactFieldType.append(Windows.ApplicationModel.Contacts.ContactFieldType.email);
            // Open the picker for the user to select a contact
            picker.pickContactAsync().done(function (contact) {
                if (contact !== null) {
                    var output = "Selecione o contato:\n" + contact.displayName;
                    return output;
                } else {
                    // The picker was dismissed without selecting a contact
                    console("Nenhum contato selecionado");
                }
            });
        }
    };

    service.notification = function (mensagem) {
        if (Windows !== 'undefined' &&
            Windows.UI !== 'undefined' &&
            Windows.UI.Notifications !== 'undefined') {
            var notifications = Windows.UI.Notifications;
            //Get the XML template where the notification content will be suplied
            var template = notifications.ToastTemplateType.toastImageAndText01;
            var toastXml = notifications.ToastNotificationManager.getTemplateContent(template);
            //Supply the text to the XML content
            var toastTextElements = toastXml.getElementsByTagName("text");
            toastTextElements[0].appendChild(toastXml.createTextNode(mensagem));
            //Supply an image for the notification
            var toastImageElements = toastXml.getElementsByTagName("image");
            //Set the image this could be the background of the note, get the image from the web
            toastImageElements[0].setAttribute("src", "https://app.condominiofacil.net/img/ms-icon-70x70.png");
            toastImageElements[0].setAttribute("alt", "red graphic");
            //Specify a long duration
            var toastNode = toastXml.selectSingleNode("/toast");
            toastNode.setAttribute("duration", "long");
            //Specify the audio for the toast notification
            var toastNode = toastXml.selectSingleNode("/toast");
            var audio = toastXml.createElement("audio");
            audio.setAttribute("src", "ms-winsoundevent:Notification.IM");
            //Specify launch paramater
            toastXml.selectSingleNode("/toast").setAttribute("launch", '{"type":"toast","param1":"12345","param2":"67890"}');
            //Create a toast notification based on the specified XML
            var toast = new notifications.ToastNotification(toastXml);
            //Send the toast notification
            var toastNotifier = notifications.ToastNotificationManager.createToastNotifier();
            toastNotifier.show(toast);
        }
    };

    service.agenda = function (mensagem) {
        // Create an Appointment that should be added the user's
        // appointments provider app.
        var appointment = new Windows.ApplicationModel.Appointments.Appointment();

        appointment.startTime = new Date(2014, 2, 28, 18); // March 28th, 2014 at 6:00pm
        appointment.duration = (60 * 60 * 100000) / 100; // 1 hour in 100ms units
        appointment.location = "Ben Miller's home";
        appointment.subject = "Frank's Birthday";
        appointment.details = "Surprise party to celebrate Frank's 60th birthday! Hoping you all can join us.";
        appointment.reminder = (15 * 60 * 1000000000) / 100; // Remind me 15 minutes prior to appointment

        // Get the selection rect of the button pressed to add this appointment
        var boundingRect = e.srcElement.getBoundingClientRect();
        var selectionRect = {
            x: boundingRect.left, y: boundingRect.top,
            width: boundingRect.width, height: boundingRect.height
        };

        // ShowAddAppointmentAsync returns an appointment id if the appointment given was added to the user's calendar.
        // This value should be stored in app data and roamed so that the appointment can be replaced or removed in the future.
        // An empty string return value indicates that the user canceled the operation before the appointment was added.
        Windows.ApplicationModel.Appointments.AppointmentManager.showAddAppointmentAsync(
            appointment, selectionRect, Windows.UI.Popups.Placement.default)
            .done(function (appointmentId) {
                if (appointmentId) {
                    document.querySelector('#result').innerText =
                        "Appointment Id: " + appointmentId;
                } else {
                    document.querySelector('#result').innerText = "Appointment not added";
                }
            });
    }

    //   if(Windows && Windows.ApplicationModel && Windows.ApplicationModel.Appointments) {}
    // Create an Appointment that should be added the user's appointments provider app.
    // var appointment = new Windows.ApplicationModel.Appointments.Appointment();
    // Get the selection rect of the button pressed to add this appointment
    // var boundingRect = e.srcElement.getBoundingClientRect();
    //  var selectionRect = { x: boundingRect.left, y: boundingRect.top, width: boundingRect.width, height: boundingRect.height };
    // ShowAddAppointmentAsync returns an appointment id if the appointment given was added to the user's calendar.
    // This value should be stored in app data and roamed so that the appointment can be replaced or removed in the future.
    // An empty string return value indicates that the user canceled the operation before the appointment was added.
    //  Windows.ApplicationModel.Appointments.AppointmentManager.showAddAppointmentAsync(appointment, selectionRect, Windows.UI.Popups.Placement.default)
    //      .done(function (appointmentId) {
    //         if (appointmentId) {
    //             console.log("Appointment Id: " + appointmentId);
    //         } else {
    //            console.log("Appointment not added");
    //         }
    //     });
    //}
    return service;

}]);
