(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('DocumentViewCtrl', ['$scope', 'DocumentViewReceiver',
      function($scope, DocumentViewReceiver) {
        DocumentViewReceiver.sendTestMessage();

        $scope.response = 'nothing received';
        DocumentViewReceiver.onReceive(function(messageData) {
          $scope.response = messageData.message || 'no message';
          $scope.$apply();
        });

      }]);

})(angular);
