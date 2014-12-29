(function(angular) {
	'use strict';

	var vitaDirectives = angular.module('vitaDirectives');

	vitaDirectives.directive('plotview', [
		function() {

			function link(scope, element) {
			}

			return {
				restrict: 'A',
				scope: {
					width: '=',
					height: '='
				},
				link: link
			};
		}]);

})(angular);
