'use strict';

var backendUrl = '';

app.factory('DashboardService', ['$http', function ($http) {

    var service = {};

    service.get = function () {
        return $http
                .get('api/tema', {cache: true})
                .then(function (res) {
                    return res.data;
                });
    };


    return service;
}]);

