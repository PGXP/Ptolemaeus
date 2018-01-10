'use strict';

app.controller('DashboardController', [
  '$q',
  '$scope',
  '$rootScope',
  function($q, $scope, $rootScope) {
    //normalise window.URL
    window.URL ||
      (window.URL = window.webkitURL || window.msURL || window.oURL);

    //normalise navigator.getUserMedia
    navigator.getUserMedia ||
      (navigator.getUserMedia =
        navigator.webkitGetUserMedia ||
        navigator.mozGetUserMedia ||
        navigator.msGetUserMedia);

    var options = {
      video: true,
      toString: function() {
        return 'video';
      }
    };
    navigator.getUserMedia(options, successCallback);

    function successCallback(stream) {
      video.src =
        window.URL && window.URL.createObjectURL
          ? window.URL.createObjectURL(stream)
          : stream;
    }

    function drawFrame() {
      var canvas = document.querySelector('canvas'),
        context = canvas.getContext('2d');

      context.drawImage(video, 0, 0, canvas.width, canvas.height);
      setTimeout(drawFrame, 50);
    }
  }
]);
