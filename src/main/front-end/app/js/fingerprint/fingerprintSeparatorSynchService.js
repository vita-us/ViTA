(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Stores a flag for the visibility of part- and chapter-separators. This service is called after
  // toggling the visibility and after inserting a new fingerprint to get the current toggle state
  vitaServices.service('FingerprintSynchronizer', function() {
    var isSeparatorVisible = true;

    this.toggle = function() {
      isSeparatorVisible = !isSeparatorVisible;
      this.synchronize();
    };

    this.synchronize = function() {
      var separators = $('.chapter-separators, .part-separators');
      var separatorsKey = $('.separators-key');

      if (isSeparatorVisible) {
        separators.show();
        separatorsKey.show();
      } else {
        separators.hide();
        separatorsKey.hide();
      }
    };

    this.isSeparatorVisible = function() {
      return isSeparatorVisible;
    }
  });

})(angular);
