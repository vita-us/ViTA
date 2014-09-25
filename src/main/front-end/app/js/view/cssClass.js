(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('CssClass', function() {
    this.forRankingValue = function(ranking) {
      return "ranking-" + ranking;
    };
  });

})(angular);
