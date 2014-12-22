(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Stores a flag for the visibility of part- and chapter-separators. This service is called after
  // toggling the visibility and after inserting a new fingerprint to get the current toggle state
  vitaServices.factory('FingerprintSynchronizer', function() {
    var isSeparatorVisible = true;

    return function(isVisible) {
      // this function can be called without parameter to not update the flag but to set the
      // correct visibility for separators in new fingerprints
      isSeparatorVisible = typeof isVisible === 'boolean' ? isVisible : isSeparatorVisible;

      var separators = $(".chapter-separators, .part-separators");

      if (isSeparatorVisible) {
        separators.show();
      } else {
        separators.hide();
      }
    };
  });

})(angular);